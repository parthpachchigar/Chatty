package gash.router.server.resources;

import gash.router.client.CommConnection;
import gash.router.server.state.FollowerState;
import gash.router.server.state.State;
import routing.MsgInterface.Route;

public class HeartbeatResource implements RouteResource {

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "/header";
	}

	@Override
	public Route process(Route body) {
		// TODO Auto-generated method stub
		if (body.hasNetworkDiscoveryPacket() && State.getStatus() == State.Status.FOLLOWER) {
			FollowerState.timeout = State.myConfig.getHeartbeatDt();
			if (!State.leaderConnection.getHost().equals(body.getNetworkDiscoveryPacket().getNodeAddress())) {
				State.leaderConnection = CommConnection.initConnection(
						body.getNetworkDiscoveryPacket().getNodeAddress(),
						(int) body.getNetworkDiscoveryPacket().getNodePort());
			}
		}
		if (body.hasNetworkDiscoveryPacket() && State.getStatus() == State.Status.CANDIDATE) {
			State.setStatus(State.Status.FOLLOWER);
		}
		return null;
	}

}
