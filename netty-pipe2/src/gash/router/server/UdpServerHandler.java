package gash.router.server;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.state.State;
import io.netty.util.internal.SocketUtils;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;


public class UdpServerHandler extends SimpleChannelInboundHandler<Route> {

    private static final Random random = new Random();
    protected static Logger logger = LoggerFactory.getLogger("server");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route request) throws Exception {
    	
        //System.err.println("In channel read");

        //System.err.println(request.getGroup());
        //System.err.println(request.getId());

//
        //System.err.println(ctx.channel().attr(UdpServer.attkey).get());
        String clientIpPort = ctx.channel().attr(UdpServer.attkey).get();
        String clientIp = clientIpPort.split(":")[0];
        String clientPort = clientIpPort.split(":")[1];

        try {
            Route.Builder toSend = Route.newBuilder();
            toSend.setId(999);
            toSend.setPath(Path.NETWORK_DISCOVERY);
            NetworkDiscoveryPacket.Builder ndpReq = NetworkDiscoveryPacket.newBuilder();
            ndpReq.setMode(Mode.RESPONSE);
            if(request.getNetworkDiscoveryPacket().getSender()!=Sender.valueOf(Sender.EXTERNAL_SERVER_NODE_VALUE)) {
            	ndpReq.setSender(Sender.INTERNAL_SERVER_NODE);
            }else {
            	ndpReq.setSender(Sender.EXTERNAL_SERVER_NODE);
            }
            ndpReq.setNodeAddress(State.myConfig.getHost());
            ndpReq.setNodePort(State.myConfig.getWorkPort());
            ndpReq.setNodeId(""+State.myConfig.getNodeId());
            ndpReq.setSecret("secret");
            toSend.setNetworkDiscoveryPacket(ndpReq.build());
            Route myResponse = toSend.build();
            ByteBuf buf = Unpooled.copiedBuffer(myResponse.toByteArray());
            ctx.writeAndFlush(new DatagramPacket(buf, SocketUtils.socketAddress(clientIp.substring(1, clientIp.length()), Integer.parseInt(clientPort)))).sync();
            logger.info("Discovery Packet Sent");
        } catch (Exception e) {
            logger.error("Exception received");
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
