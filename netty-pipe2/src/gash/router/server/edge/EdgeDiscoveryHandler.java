package gash.router.server.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.client.VoteHandler;
import gash.router.client.CommConnection;
import gash.router.client.CommInit;
import gash.router.client.MessageClient;
import gash.router.server.MessageServer;
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
	static EventLoopGroup group = new NioEventLoopGroup();
	public static EdgeList outbound=new EdgeList();

	public static void init(EdgeInfo ei) {
		MessageServer.logger.info("Trying to connect to host ! " + ei.getHost());
		try {
			CommConnection connection=CommConnection.getInstance().initConnection(ei.getHost(), (int)ei.getPort());
			connection.addListener(VoteHandler.getInstance());
			ei.setChannel(connection.connect());
			ei.setActive(true);
			ei.setComm(connection);
			
		} catch (Throwable ex) {
			System.out.println("failed to initialize the client connection " + ex.toString());
			ex.printStackTrace();
		}

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
		MessageServer.logger.info("Message Arrived");
		
		String host = "127.0.0.1";//msg.getNetworkDiscoveryPacket().getNodeAddress();
		long port = msg.getNetworkDiscoveryPacket().getNodePort();
		if (msg.getNetworkDiscoveryPacket().hasNodeId()) {
			
		String nodeId=msg.getNetworkDiscoveryPacket().getNodeId();
		//MessageServer.logger.info("In outbound: "+outbound.getMap().containsKey(Integer.parseInt(nodeId)));
			if (!outbound.getMap().containsKey(Integer.parseInt(nodeId))) {
				MessageServer.logger.info("Before init");
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
