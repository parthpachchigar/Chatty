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

import gash.router.server.state.CandidateState;
import gash.router.server.state.State;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;

/**
 * responds to request for pinging the service
 * 
 * @author gash
 * 
 */
public class VoteRequestResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("ping");

	@Override
	public String getPath() {
		return "/ping";
	}

	@Override
	public Route process(Route body) {
		logger.info("In Ping Resource");
		Route reply = null;
		Route.Builder voteReply = Route.newBuilder();
		if (body.hasNetworkDiscoveryPacket()) {
			if(State.getStatus()==State.Status.CANDIDATE && Integer.parseInt(body.getNetworkDiscoveryPacket().getNodeId())>State.myConfig.getNodeId()) {
				State.setStatus(State.Status.FOLLOWER);
			}
			voteReply.setId(112);
			voteReply.setPath(Path.PING);
			NetworkDiscoveryPacket.Builder ndp=NetworkDiscoveryPacket.newBuilder();
			ndp.setSender(Sender.INTERNAL_SERVER_NODE);
			ndp.setMode(Mode.RESPONSE);
			ndp.setNodeId(""+State.myConfig.getNodeId());
			ndp.setNodeAddress(State.myConfig.getHost());
			ndp.setNodePort(State.myConfig.getWorkPort());
			ndp.setSecret("secret");
			voteReply.setNetworkDiscoveryPacket(ndp.build());
			reply=voteReply.build();
		}
		return reply;
	}

}
