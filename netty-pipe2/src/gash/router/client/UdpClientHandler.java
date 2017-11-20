package gash.router.client;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket;

public class UdpClientHandler extends SimpleChannelInboundHandler<Route> {
	protected static Logger logger = LoggerFactory.getLogger("client");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Route route) throws Exception {
        System.out.println("Recieved a datagram packet " + route);
        System.out.println("***************lets begin test****************");
        if (route.getNetworkDiscoveryPacket().getMode() == NetworkDiscoveryPacket.Mode.RESPONSE) {
            String toConnectIP = route.getNetworkDiscoveryPacket().getNodeAddress();
            int toConnectPort = (int) route.getNetworkDiscoveryPacket().getNodePort();
            if(null != toConnectIP) {
                try {
                    MessageClient mc = new MessageClient(toConnectIP, toConnectPort);
                    ConnectApp ca = new ConnectApp(mc);
                    ca.continuePing();
                    System.out.println("\n** exiting in 10 seconds. **");
                    System.out.flush();
                    Thread.sleep(10 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CommConnection.getInstance().release();
                }
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
