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
public class PingResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("ping");

	@Override
	public String getPath() {
		return "/ping";
	}

	@Override
	public Route process(Route body) {
		Route.Builder voteMessage = Route.newBuilder();
		voteMessage.setId(112);
		voteMessage.setPath(Path.PING);
		NetworkDiscoveryPacket.Builder ndpReq = NetworkDiscoveryPacket.newBuilder();
        ndpReq.setMode(Mode.RESPONSE);
        ndpReq.setSender(Sender.INTERNAL_SERVER_NODE);
        ndpReq.setNodeAddress(State.myConfig.getHost());//State.myConfig.getHost()
        ndpReq.setNodePort(State.myConfig.getWorkPort());//State.myConfig.getWorkPort()
        ndpReq.setNodeId(""+State.myConfig.getNodeId());
        
        ndpReq.setSecret("secret");
        voteMessage.setNetworkDiscoveryPacket(ndpReq.build());
		Route voteResponse = voteMessage.build();
		logger.info(body.toString());
		if (!body.getNetworkDiscoveryPacket().getNodeAddress().equals(State.myConfig.getHost())) {
			if (Integer.parseInt(body.getNetworkDiscoveryPacket().getNodeId())>State.myConfig.getNodeId()) {
				State.setStatus(State.Status.FOLLOWER);
				voteResponse=null;

			}
		}
		System.out.println(body);
		System.out.println(voteResponse);
		
		

		return voteResponse;
	}

}
