package gash.router.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.state.State;
import routing.MsgInterface.Group;
import routing.MsgInterface.Message;
import routing.MsgInterface.Route;

public class GroupResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("group");
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "/group";
	}

	@Override
	public Route process(Route body) {
		// TODO Auto-generated method stub
		if(State.getStatus()==State.Status.LEADER) {
			DatabaseService dbs = DatabaseService.getInstance();
			dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
			DatabaseClient dbc = dbs.getDb();
		
			if (body.getGroup().getAction() == Group.ActionType.CREATE) {
				dbc.createGroup(body.getGroup().getGname());
			}
			if (body.getGroup().getAction() == Group.ActionType.DELETE) {
				dbc.deleteGroup(body.getGroup().getGname());
			}
			if (body.getGroup().getAction() == Group.ActionType.ADDUSER) {
				dbc.addUserToGroup(body.getGroup().getUsername(), body.getGroup().getGname());
			}
			for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
				MessageServer.logger.debug("I have started contacting");
				
				if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
					// Create route message for replication using replicate as path
					

					Route.Builder route = Route.newBuilder();
					route.setId(555);
					route.setPath(Route.Path.REPLICATE);
					route.setGroup(body.getGroup());
					

					ei.getComm().write(route.build());
				}
			}
			
			logger.info("Sent message for replication"+body.getGroup());
			
		}else {
			State.leaderConnection.write(body);
		}

		return null;
	}

}
