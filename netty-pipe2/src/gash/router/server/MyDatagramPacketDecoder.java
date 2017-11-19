package gash.router.server;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class MyDatagramPacketDecoder extends DatagramPacketDecoder {


    MyDatagramPacketDecoder(ProtobufDecoder m){
        super(m);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        
        ctx.channel().attr(UdpServer.attkey).set(msg.sender().toString());

        super.decode(ctx, msg, out);
        
    }

}
