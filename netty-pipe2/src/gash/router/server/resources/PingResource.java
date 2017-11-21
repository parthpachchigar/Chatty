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
import routing.MsgInterface.Route;

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
		Route reply = null;
		logger.info(body.toString());
		if (!reply.getNetworkDiscoveryPacket().getNodeAddress().equals(State.myConfig.getHost())) {
			if (State.getStatus() == State.Status.CANDIDATE) {
				State.setStatus(State.Status.FOLLOWER);

			}
		}
		reply = body;

		return reply;
	}

}
