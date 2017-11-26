package gash.router.server.resources;

import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.state.State;
import routing.MsgInterface.Message;
import routing.MsgInterface.Route;

public class MessageResponseResource implements RouteResource{

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "/message_response";
	}

	@Override
	public Route process(Route body) {
		// TODO Auto-generated method stub
		if (State.getStatus() == State.Status.LEADER) {
			DatabaseService dbs = DatabaseService.getInstance();
			dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
			DatabaseClient dbc = dbs.getDb();
			for (int i = 0; i < body.getMessagesResponse().getMessagesCount(); i++) {
				Message m = body.getMessagesResponse().getMessages(i);
				dbc.postMessage(m.getPayload(), m.getSenderId(), m.getReceiverId());
			}
			
			for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
				MessageServer.logger.debug("I have started contacting");
				
				if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
					// Create route message for replication using replicate as path
					

					Route.Builder route = Route.newBuilder();
					route.setId(555);
					route.setPath(Route.Path.REPLICATE);
					route.setMessagesResponse(body.getMessagesResponse());
					

					ei.getComm().write(route.build());
				}
			}
			
			MessageServer.logger.info("Sent message for replication"+body.getGroup());

		}
		return null;
	}

}
