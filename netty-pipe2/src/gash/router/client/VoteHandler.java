package gash.router.client;

import gash.router.server.MessageServer;
import gash.router.server.state.CandidateState;
import routing.MsgInterface.Route;
import routing.MsgInterface.Route.Path;

public class VoteHandler implements CommListener{
	protected static VoteHandler instance;
	private VoteHandler() {
		
	}
	public static VoteHandler getInstance() {
		if(instance==null) {
			instance=new VoteHandler();
		}
		return instance;
	}
	@Override
	public String getListenerID() {
		// TODO Auto-generated method stub
		return "VoteHandler";
	}

	@Override
	public void onMessage(Route msg) {
		// TODO Auto-generated method stub
		if(msg.getPath()==Path.PING && msg.hasNetworkDiscoveryPacket()) {
			MessageServer.logger.info("Got Vote");
			CandidateState.getInstance().handleVote();
		}
	}

}
