package gash.router.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.state.CandidateState;
import gash.router.server.state.FollowerState;
import gash.router.server.state.State;
import routing.MsgInterface.Route;

public class UserResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("message");

	@Override
	public String getPath() {
		return "/header";
	}

	@Override
	public Route process(Route body) {
		//logger.info(body.toString());
		
		return null;
	}

}
