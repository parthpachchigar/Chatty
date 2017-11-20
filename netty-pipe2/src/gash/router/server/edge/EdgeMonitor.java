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
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.internal.SocketUtils;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;
import gash.router.server.state.State;

public class EdgeMonitor {
	Timer timer;

	public EdgeMonitor() {
    //  new EdgeMonitor(); this should help to call the scheduled task .
		
		//timer = new Timer();
		//timer.schedule(new DiscoverTask(), 0, 5 * 1000);
		//timer.schedule(new DisplayEdgeTask(), 0, 4 * 1000);
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
		
		
        Route.Builder routebuilder=Route.newBuilder();
        routebuilder.setId(999);
        routebuilder.setPath(Path.NETWORK_DISCOVERY);
        NetworkDiscoveryPacket.Builder ndpReq = NetworkDiscoveryPacket.newBuilder();
        ndpReq.setMode(Mode.REQUEST);
        ndpReq.setSender(Sender.INTERNAL_SERVER_NODE);
        ndpReq.setNodeAddress("10.0.0.31");//State.myConfig.getHost()
        ndpReq.setNodePort(4167);//State.myConfig.getWorkPort()
        ndpReq.setNodeId(""+State.myConfig.getNodeId());
        
        ndpReq.setSecret("secret");
        routebuilder.setNetworkDiscoveryPacket(ndpReq.build());
        Route msg=routebuilder.build();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<DatagramChannel>(){
                        @Override
                        public void initChannel(DatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("protobufDecoder", new DatagramPacketDecoder(new ProtobufDecoder(Route.getDefaultInstance())));
                            pipeline.addLast("protobufEncoder", new ProtobufEncoder());
                            pipeline.addLast(new EdgeDiscoveryHandler());
                            
                        }
                    });


            Channel ch = b.bind(0).sync().channel();

            ByteBuf buf = Unpooled.copiedBuffer(msg.toByteArray());
            

            ch.writeAndFlush(new DatagramPacket(buf,SocketUtils.socketAddress("127.0.0.1", PORT))).sync();

            // UDPClientHandler will close the DatagramChannel when a
            // response is received.  If the channel is not closed within 5 seconds,
            // print an error message and quit.
            if (!ch.closeFuture().await(5000)) {
                System.out.println("request served.");
            }
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				System.out.println("Node active:" + i.getId());
			} else {
				System.out.println("Node inactive:" + i.getId());
			}
		}

	}

	

	class DiscoverTask extends TimerTask {
		public DiscoverTask() {
			
		}
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
