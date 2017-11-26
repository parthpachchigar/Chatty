/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.server.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.state.State;
import routing.MsgInterface.Message;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;

/**
 * processes requests of message passing - demonstration
 * 
 * @author gash
 * 
 */
public class MessageResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("message");

	@Override
	public String getPath() {
		return "/message";
	}

	@Override
	public Route process(Route body) {
		if(State.getStatus()==State.Status.LEADER) {
			DatabaseService dbs= DatabaseService.getInstance();
			dbs.dbConfiguration("postgresql","jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
			DatabaseClient dbc= dbs.getDb();
			dbc.postMessage(body.getMessage().getPayload(),body.getMessage().getSenderId(),body.getMessage().getReceiverId());
			logger.info("Got message "+body.getMessage());
			
			for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
				MessageServer.logger.debug("I have started contacting");
				
				if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
					// Create route message for replication using replicate as path
					Message.Builder msg = Message.newBuilder();
					msg.setType(Message.Type.SINGLE);
					msg.setSenderId(body.getMessage().getSenderId());
					msg.setPayload(body.getMessage().getPayload());
					msg.setReceiverId(body.getMessage().getReceiverId());
					msg.setTimestamp("systemTime");
					msg.setAction(Message.ActionType.POST);

					Route.Builder route = Route.newBuilder();
					route.setId(555);
					route.setPath(Route.Path.REPLICATE);
					route.setMessage(msg);
					

					ei.getComm().write(route.build());
				}
			}
<<<<<<< HEAD
			
			logger.info("Sent message for replication"+body.getMessage());
			
		}else {
			State.leaderConnection.write(body);
=======
		} else if(State.getStatus() == State.Status.LEADER){
			State.getState().handleMessageEntries(body);
>>>>>>> 11b62728280c3728f7a5316782a347d85798de3d
		}
		return null;
	}

}
