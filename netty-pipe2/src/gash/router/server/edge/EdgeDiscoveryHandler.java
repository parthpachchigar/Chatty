package gash.router.server.edge;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import routing.MsgInterface.Route;

public class EdgeDiscoveryHandler extends SimpleChannelInboundHandler<DatagramPacket>  {

	 @Override
	    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
	        Route response=Route.parseFrom(msg.content().array());
	        System.out.println(response);
	        ctx.close();
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        cause.printStackTrace();
	        ctx.close();
	    }
	
}
