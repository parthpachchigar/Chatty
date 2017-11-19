package gash.router.server.state;

import gash.router.container.RoutingConf;

public class State {
	public enum Status{
		FOLLOWER,
		CANDIDATE,
		LEADER	
	};
	private static Status currentState;
	public static RoutingConf myConfig;
	public static Status getStatus() {
		return currentState;
	}
	public static void setStatus(Status status) {
		currentState=status;
	}
}
