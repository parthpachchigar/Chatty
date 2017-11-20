package gash.router.client;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import routing.MsgInterface.Route;

public class UdpClientHandler extends SimpleChannelInboundHandler<Route> {
	protected static Logger logger = LoggerFactory.getLogger("client");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
        
        logger.info(msg.toString());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
