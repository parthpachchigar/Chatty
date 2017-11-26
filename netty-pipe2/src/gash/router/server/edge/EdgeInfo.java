package gash.router.server.edge;

import java.util.ArrayList;

import gash.router.client.CommConnection;
import io.netty.channel.Channel;

public class EdgeInfo {
	private int ref;
	private String host;
	private long port;
	private long lastHeartbeat = -1;
	private boolean active = false;
	private Channel channel;
	private CommConnection comm;
	public static ArrayList<Integer> availableNodes = new ArrayList<Integer>();
	EdgeInfo(int ref, String host, long port2) {
		this.ref = ref;
		this.host = host;
		this.port = port2;
	}
	public void setComm(CommConnection comm) {
		this.comm=comm;
	}
	public CommConnection getComm() {
		return comm;
	}
	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getLastHeartbeat() {
		return lastHeartbeat;
	}

	public void setLastHeartbeat(long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
