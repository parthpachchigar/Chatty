package gash.router.server.state;

import gash.router.client.CommConnection;
import gash.router.container.RoutingConf;
import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeList;

public class State {
	public enum Status{
		FOLLOWER,
		CANDIDATE,
		LEADER	
	};
	private static Status currentState;
	public static RoutingConf myConfig;
	public static State state;
	public static CommConnection leaderConnection;
	
	public static Status getStatus() {
		return currentState;
	}
	public static void setStatus(Status status) {
		currentState=status;
		if (status == Status.FOLLOWER) {
			//if the new state recieved within that time is again Follower
			//than reset the follower state
			MessageServer.logger.info("In follower");
			
			if(CandidateState.getInstance()!=null){
				CandidateState.getInstance().stopService();
			}
			if(LeaderState.getInstance()!=null){
				LeaderState.getInstance().stopService();
			}
			FollowerState.getInstance().startService(FollowerState.getInstance());
		} else if (status == Status.LEADER) {
			//if the new state recieved within that time is Leader
			//than start the leader service
			MessageServer.logger.info("setting leader");
			CandidateState.getInstance().stopService();
			if(FollowerState.getInstance()!=null){
				FollowerState.getInstance().stopService();
			}
			if(CandidateState.getInstance()!=null){
				
				CandidateState.getInstance().stopService();
			}
						
			LeaderState.getInstance().startService(LeaderState.getInstance());

		} else if (status == Status.CANDIDATE) {
			//if the new state recieved within that time is Candidate
			//than start the candidate service
			MessageServer.logger.info("In candidate");
			if(FollowerState.getInstance()!=null){
				FollowerState.getInstance().stopService();
			}
			
			if(LeaderState.getInstance()!=null){
				LeaderState.getInstance().stopService();
			}
			CandidateState.getInstance().startService(CandidateState.getInstance());
			
		}
	}
	
	protected volatile Boolean running = Boolean.TRUE;
	static Thread cthread;
	
	public void startService(State state) {

	}

	public void stopService() {
	}

	
	
}
