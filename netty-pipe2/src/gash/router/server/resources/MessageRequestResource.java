package gash.router.server.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.state.State;
import routing.MsgInterface.Message;
import routing.MsgInterface.MessagesResponse;
import routing.MsgInterface.Route;
import routing.MsgInterface.Group.ActionType;

public class MessageRequestResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("message");
	String myNodes = "10.0.0.31 10.0.0.32 10.0.0.33 10.0.0.34 10.0.0.35 10.0.0.36 10.0.0.37 10.0.0.38 10.0.0.39 10.0.0.40";

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return "/message_request";
	}

	@Override
	public Route process(Route body) {
		// TODO Auto-generated method stub
		Route.Builder mr =null;
		if (State.getStatus() == State.Status.LEADER) {
			DatabaseService dbs = DatabaseService.getInstance();
			dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
			DatabaseClient dbc = dbs.getDb();
			ResultSet rs = dbc.getMessages(body.getMessagesRequest().getId());

			mr = Route.newBuilder();
			mr.setId(444);
			mr.setPath(Route.Path.MESSAGES_RESPONSE);
			MessagesResponse.Builder messageres = MessagesResponse.newBuilder();
			messageres.setType(MessagesResponse.Type.USER);
			if (rs != null) {
				try {
					rs.first();
					while (rs.next()) {
						Message.Builder msg = Message.newBuilder();
						msg.setAction(routing.MsgInterface.Message.ActionType.POST);
						msg.setSenderId(rs.getString("from_id"));
						msg.setReceiverId(rs.getString("to_id"));
						msg.setPayload(rs.getString("message"));
						messageres.addMessages(msg.build());
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
				MessageServer.logger.debug("I have started contacting");
				if (myNodes.contains(ei.getHost())) {
					continue;
				} else {
					if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
						// Create route message for replication using replicate as path

						Route.Builder route = Route.newBuilder();
						route.setId(555);
						route.setPath(Route.Path.MESSAGES_REQUEST);
						route.setMessagesRequest(body.getMessagesRequest());

						ei.getComm().write(route.build());
					}
				}
			}

			logger.info("Sent message for replication" + body.getMessage());

		} else {
			State.leaderConnection.write(body);
			DatabaseService dbs = DatabaseService.getInstance();
			dbs.dbConfiguration("postgresql", "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
			DatabaseClient dbc = dbs.getDb();
			ResultSet rs = dbc.getMessages(body.getMessagesRequest().getId());

			mr = Route.newBuilder();
			mr.setId(444);
			mr.setPath(Route.Path.MESSAGES_RESPONSE);
			MessagesResponse.Builder messageres = MessagesResponse.newBuilder();
			messageres.setType(MessagesResponse.Type.USER);
			if (rs != null) {
				try {
					rs.first();
					while (rs.next()) {
						Message.Builder msg = Message.newBuilder();
						msg.setAction(routing.MsgInterface.Message.ActionType.POST);
						msg.setSenderId(rs.getString("from_id"));
						msg.setReceiverId(rs.getString("to_id"));
						msg.setPayload(rs.getString("message"));
						messageres.addMessages(msg.build());
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return mr.build();
	}

}
