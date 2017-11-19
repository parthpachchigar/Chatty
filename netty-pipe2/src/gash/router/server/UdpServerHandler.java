package gash.router.server;

package server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.Random;

import io.netty.util.internal.SocketUtils;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;


public class UdpServerHandler extends SimpleChannelInboundHandler<Route> {

    private static final Random random = new Random();


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route request) throws Exception {
        System.err.println("In channel read");

        System.err.println(request.getGroup());
        System.err.println(request.getId());

//
        System.err.println(ctx.channel().attr(UdpServer.attkey).get());
        String clientIpPort = ctx.channel().attr(UdpServer.attkey).get();
        String clientIp = clientIpPort.split(":")[0];
        String clientPort = clientIpPort.split(":")[1];

        try {
            Route.Builder toSend = Route.newBuilder();
            
            Route myResponse = toSend.build();
            ByteBuf buf = Unpooled.copiedBuffer(myResponse.toByteArray());
            ctx.writeAndFlush(new DatagramPacket(buf, SocketUtils.socketAddress(clientIp.substring(1, clientIp.length()), Integer.parseInt(clientPort)))).sync();
        } catch (Exception e) {
            System.err.println("Exception received");
            e.printStackTrace();
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
        // We don't close the channel because we can keep serving requests.
    }
}
