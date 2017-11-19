package gash.router.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.protobuf.ByteString;

import routing.MsgInterface.Route;
public class DiscoveryThread implements Runnable {

	DatagramSocket socket;

	  @Override
	  public void run() {
	    try {
	      //Keep a socket open to listen to all the UDP trafic that is destined for this port
	      socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
	      socket.setBroadcast(true);

	      while (true) {
	        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

	        //Receive a packet
	        byte[] recvBuf = new byte[47];
	        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
	        socket.receive(packet);

	        //Packet received
	        System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
	        //System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

	        //See if the packet holds the right command (message)
	        String message = new String(packet.getData()).trim();
	        ByteString bs=ByteString.copyFrom(packet.getData());
	        /*if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
	          byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

	          //Send a response
	          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
	          socket.send(sendPacket);

	          System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
	        }*/
	        Route route=Route.parseFrom(bs);
	        System.out.println(getClass().getName() + ">>>Packet received; data: " + route);
	        System.out.println("Length="+packet.getData().length);
	        
	      }
	    } catch (IOException ex) {
	      Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
	    }
	  }
	
	public static DiscoveryThread getInstance() {
	    return DiscoveryThreadHolder.INSTANCE;
	  }

	  private static class DiscoveryThreadHolder {

	    private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	  }
	  

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
		discoveryThread.start();
	}

}
