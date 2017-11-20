package gash.router.server.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.client.CommInit;
import gash.router.server.state.State;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import routing.MsgInterface.Route;

public class EdgeDiscoveryHandler extends SimpleChannelInboundHandler<Route> {
	protected static Logger logger = LoggerFactory.getLogger("server");
	static EventLoopGroup group = new NioEventLoopGroup();
	public static EdgeList outbound=new EdgeList();

	public static void init(EdgeInfo ei) {
		logger.info("Trying to connect to host ! " + ei.getHost());
		try {
			CommInit si = new CommInit(false);
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(si);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.option(ChannelOption.SO_KEEPALIVE, true);

			// Make the connection attempt.
			ChannelFuture cf = b.connect(ei.getHost(), (int) ei.getPort()).syncUninterruptibly();

			// want to monitor the connection to the server s.t. if we loose the
			// connection, we can try to re-establish it.
			// ClientClosedListener ccl = new ClientClosedListener(this);
			// channel.channel().closeFuture().addListener(ccl);
			ei.setChannel(cf.channel());
			ei.setActive(true);

			System.out.println(cf.channel().localAddress() + " -> open: " + cf.channel().isOpen() + ", write: "
					+ cf.channel().isWritable() + ", reg: " + cf.channel().isRegistered());

		} catch (Throwable ex) {
			System.out.println("failed to initialize the client connection " + ex.toString());
			ex.printStackTrace();
		}

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
		logger.info("Message Arrived");
		
		String host = msg.getNetworkDiscoveryPacket().getNodeAddress();//"127.0.0.1";//msg.getNetworkDiscoveryPacket().getNodeAddress();
		long port = msg.getNetworkDiscoveryPacket().getNodePort();
		if (msg.getNetworkDiscoveryPacket().hasNodeId()) {
			
		String nodeId=msg.getNetworkDiscoveryPacket().getNodeId();
		logger.info("In outbound: "+outbound.getMap().containsKey(Integer.parseInt(nodeId)));
			if (!outbound.getMap().containsKey(Integer.parseInt(nodeId))) {
				logger.info("Before init");
				//outbound.addNode(Integer.parseInt(nodeId), host, port);
				init(outbound.addNode(Integer.parseInt(nodeId), host, port));
				if(State.myConfig.getRouting().contains(new gash.router.container.RoutingConf.RoutingEntry(Integer.parseInt(nodeId), host, (int)port) )) {
					EdgeInfo.availableNodes.add(Integer.parseInt(nodeId));
				}
			}
			// Create Connection to host and port and write task to the channel
			
		}
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
