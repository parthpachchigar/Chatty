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

import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.state.State;
import io.netty.channel.ChannelFuture;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
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
		logger.info(body.toString());
		if (State.getStatus() == State.Status.FOLLOWER) {
			for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {

				if (ei.getPort() == State.leaderport && ei.getHost() == State.leaderaddress) {
					if (ei.getChannel() != null && ei.isActive()) {
						ChannelFuture cf = ei.getChannel().writeAndFlush(body);
						if (cf.isDone() && !cf.isSuccess()) {
							logger.debug("failed to send replication message to leader");
						}
					} else {
						logger.debug("leader channel not active");
					}

				}
			}
		}else if(State.getStatus() == State.Status.LEADER){
			State.getState().handleMessageEntries(body);
		}
		return body;

	}

}
