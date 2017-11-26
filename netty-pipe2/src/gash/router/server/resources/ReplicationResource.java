package gash.router.server.resources;

import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.server.MessageServer;
import gash.router.server.state.State;
import routing.MsgInterface.Group;
import routing.MsgInterface.Message;
import routing.MsgInterface.Route;

public class ReplicationResource implements RouteResource {

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "/replicate";
	}

	@Override
	public Route process(Route body) {
		// TODO Auto-generated method stub
		if (State.getStatus() != State.Status.LEADER) {
			if (body.hasMessage()) {

				DatabaseService dbs = DatabaseService.getInstance();
				dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
				DatabaseClient dbc = dbs.getDb();
				dbc.postMessage(body.getMessage().getPayload(), body.getMessage().getSenderId(),
						body.getMessage().getReceiverId());
				MessageServer.logger.info("Got message " + body.getMessage());
			} else if (body.hasMessagesResponse()) {
				DatabaseService dbs = DatabaseService.getInstance();
				dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
				DatabaseClient dbc = dbs.getDb();
				for (int i = 0; i < body.getMessagesResponse().getMessagesCount(); i++) {
					Message m = body.getMessagesResponse().getMessages(i);
					dbc.postMessage(m.getPayload(), m.getSenderId(), m.getReceiverId());
				}
			} else if (body.hasGroup()) {
				if (body.getGroup().getAction() == Group.ActionType.CREATE) {
					DatabaseService dbs = DatabaseService.getInstance();
					dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
					DatabaseClient dbc = dbs.getDb();
					dbc.createGroup(body.getGroup().getGname());
				}
				if (body.getGroup().getAction() == Group.ActionType.DELETE) {
					DatabaseService dbs = DatabaseService.getInstance();
					dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
					DatabaseClient dbc = dbs.getDb();
					dbc.deleteGroup(body.getGroup().getGname());
				}
				if (body.getGroup().getAction() == Group.ActionType.ADDUSER) {
					DatabaseService dbs = DatabaseService.getInstance();
					dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
					DatabaseClient dbc = dbs.getDb();
					dbc.addUserToGroup(body.getGroup().getUsername(), body.getGroup().getGname());
				}
			} else if (body.hasUser()) {
				DatabaseService dbs = DatabaseService.getInstance();
				dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
				DatabaseClient dbc = dbs.getDb();
				dbc.registerUser(body.getUser().getUname());
			}
		}
		return null;
	}

}
