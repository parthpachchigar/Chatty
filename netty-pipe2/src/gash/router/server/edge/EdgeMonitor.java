package gash.router.server.edge;

import java.util.Timer;
import java.util.TimerTask;

import gash.router.client.UdpClientHandler;
import gash.router.container.RoutingConf;
import gash.router.container.RoutingConf.RoutingEntry;
import gash.router.server.state.State;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import routing.MsgInterface.Route;
import gash.router.server.state.State;

public class EdgeMonitor {
	Timer timer;

	public EdgeMonitor() {
    //  new EdgeMonitor(); this should help to call the scheduled task .
		
		timer = new Timer();
		timer.schedule(new DiscoverTask(), 0, 5 * 1000);
		timer.schedule(new DisplayEdgeTask(), 0, 4 * 1000);
	}

	static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));
	private static EdgeMonitor instance;

	public static EdgeMonitor getInstance() {
		if (instance == null) {
			instance = new EdgeMonitor();
		}
		return instance;
	}

	public static void discoverEdges() {
		EdgeInfo.availableNodes.clear();
		Route.Builder routebuilder = Route.newBuilder();

		Route msg = routebuilder.build();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
					.handler(new ChannelInitializer<DatagramChannel>() {
						@Override
						public void initChannel(DatagramChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new EdgeDiscoveryHandler());
						}
					});

			Channel ch = null;
			try {
				ch = b.bind(0).sync().channel();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ByteBuf buf = Unpooled.copiedBuffer(msg.toByteArray());

			try {
				ch.writeAndFlush(new DatagramPacket(buf, SocketUtils.socketAddress(State.myConfig.getHost(), PORT))).sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// UDPClientHandler will close the DatagramChannel when a
			// response is received. If the channel is not closed within 5
			// seconds,
			// print an error message and quit.
			try {
				if (!ch.closeFuture().await(5000)) {
					System.err.println("request timed out.");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			group.shutdownGracefully();
		}

		// TODO: Write the same code as given in the main method of UdpClient

		// TODO: After writing that code here change handler from
		// UdpClientHandler to EdgeDiscoveryHandler over here
	}

	public static void displayEdgeStatus() {
		// gash.router.server.edge.EdgeInfo.availableNodes contains the id of
		// nodes which are active
		// TODO: from gash.router.server.state.State.myConfig for each nodeId
		// available in RoutingEntry
		// array list check if node id is present in
		// gash.router.server.edge.EdgeInfo.availableNodes
		// and if present print that node as active otherwise inactive

		for (RoutingEntry i : State.myConfig.getRouting()) {
			if (EdgeInfo.availableNodes.contains(i.getId())) {
				System.out.println("Node active:" + i);
			} else {
				System.out.println("Node inactive:" + i);
			}
		}

	}

	

	class DiscoverTask extends TimerTask {

		@Override
		public void run() {
			discoverEdges();

		}
	}

	class DisplayEdgeTask extends TimerTask {

		@Override
		public void run() {
			displayEdgeStatus();
		}
	}

	

}
