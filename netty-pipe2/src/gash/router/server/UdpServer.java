package gash.router.server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.AttributeKey;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;

public final class UdpServer implements Runnable{

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));
    static AttributeKey<String> attkey = AttributeKey.valueOf("clientid");

   

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
                             pipeline.addLast("protobufDecoder", new MyDatagramPacketDecoder(new ProtobufDecoder(Route.getDefaultInstance())));
                             pipeline.addLast("protobufEncoder", new ProtobufEncoder());
                             pipeline.addLast("handler", new UdpServerHandler());

                        }});




            b.bind(PORT).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            group.shutdownGracefully();
        }

	}
    public static void main(String[]args){
        Thread discoveryThread = new Thread(new UdpServer());
        discoveryThread.start();
    }
}
