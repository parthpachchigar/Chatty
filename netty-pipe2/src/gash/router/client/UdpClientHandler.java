package gash.router.client;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.Route;
import routing.MsgInterface.Route.Path;

public class UdpClientHandler extends SimpleChannelInboundHandler<Route> {
	protected static Logger logger = LoggerFactory.getLogger("client");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
        
        if(msg.getPath()==Path.NETWORK_DISCOVERY) {
        	if(msg.hasNetworkDiscoveryPacket() && msg.getNetworkDiscoveryPacket().getMode()==Mode.RESPONSE) {
        		ConnectApp.nodes.add(new Nodes(msg.getNetworkDiscoveryPacket().getNodeAddress(),msg.getNetworkDiscoveryPacket().getNodePort()));
        	}else {
        		logger.error("Invalid Network Discovery Package Received :"+msg);
        	}
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
