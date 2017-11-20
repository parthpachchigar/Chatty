package gash.router.client;

import gash.router.app.MyConstants;
import gash.router.server.state.State;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route;
import routing.MsgInterface.Route.Path;

public final class UdpClient {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));

    public static void main(String[] args) throws Exception {

        Route.Builder routebuilder=Route.newBuilder();
        routebuilder.setId(999);
        routebuilder.setPath(Path.NETWORK_DISCOVERY);
        NetworkDiscoveryPacket.Builder ndpReq = NetworkDiscoveryPacket.newBuilder();
        ndpReq.setMode(Mode.REQUEST);
        ndpReq.setSender(Sender.END_USER_CLIENT);
        ndpReq.setNodeAddress("10.0.0.31");//State.myConfig.getHost()
        ndpReq.setNodePort(4167);//State.myConfig.getWorkPort()
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
                            pipeline.addLast(new UdpClientHandler());
                            
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
        } finally {
            group.shutdownGracefully();
        }
        public static void main(String []args) throws Exception{
            Thread responseThread = new Thread(new UdpClient());
            responseThread.start();
            UdpClient.broadcast();
        }
    }
}
