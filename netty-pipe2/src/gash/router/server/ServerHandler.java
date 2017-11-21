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
package gash.router.server;

import java.beans.Beans;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.container.MessageRoutingConf;
import gash.router.server.resources.RouteResource;
import gash.router.server.state.State;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;
import routing.Pipe.MessageRoute;

/**
 * The message handler processes json messages that are delimited by a 'newline'
 * 
 * TODO replace println with logging!
 * 
 * @author gash
 * 
 */
public class ServerHandler extends SimpleChannelInboundHandler<Route> {
	protected static Logger logger = LoggerFactory.getLogger("connect");

	private HashMap<String, String> routing;

	public ServerHandler(MessageRoutingConf conf) {
		if (conf != null)
			routing = conf.asHashMap();
	}

	/**
	 * override this method to provide processing behavior. This implementation
	 * mimics the routing we see in annotating classes to support a RESTful-like
	 * behavior (e.g., jax-rs).
	 * 
	 * @param msg
	 */
	public void handleMessage(Route msg, Channel channel) {
		if (msg == null) {
			// TODO add logging
			System.out.println("ERROR: Unexpected content - " + msg);
			return;
		}
		if(msg.getPath()==Path.PING) {
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
			logger.info(msg.toString());
			if (!msg.getNetworkDiscoveryPacket().getNodeAddress().equals(State.myConfig.getHost())) {
				if (Integer.parseInt(msg.getNetworkDiscoveryPacket().getNodeId())>State.myConfig.getNodeId()) {
					State.setStatus(State.Status.FOLLOWER);
					voteResponse=null;

				}
			}
			System.out.println(msg);
			System.out.println(voteResponse);
			if (voteResponse != null) {
				ChannelFuture cf=channel.writeAndFlush(voteResponse);
				if (cf.isDone() && !cf.isSuccess()) {
					logger.error("failed to send message to server - " + msg);
					
				}
			}
		}
		System.out.println("---> " + msg.getId() + ": " + msg.getPath().name() );
		System.out.println("------------");
		try {
			String clazz = routing.get("/" + msg.getPath().name().toLowerCase());
			if (clazz != null) {
				RouteResource rsc = (RouteResource) Beans.instantiate(RouteResource.class.getClassLoader(), clazz);
				try {
					Route reply = rsc.process(msg);
					System.out.println("---> reply: " + reply);
					if (reply != null) {
						ChannelFuture cf = channel.writeAndFlush(reply);
						if (cf.isDone() && !cf.isSuccess()) {
							logger.error("failed to send message to server - " + msg);
						}
					}
				} catch (Exception e) {
					// TODO: add logging
				}
			} else {
				// TODO: add logging
				System.out.println("ERROR: unknown path - " + msg.getPath().name().toLowerCase());
			}
		} catch (Exception ex) {
			// TODO: add logging
			System.out.println("ERROR: processing request - " + ex.getMessage());
		}

		System.out.flush();
	}

	/**
	 * a message was received from the server. Here we dispatch the message to
	 * the client's thread pool to minimize the time it takes to process other
	 * messages.
	 * 
	 * @param ctx
	 *            The channel the message was received from
	 * @param msg
	 *            The message
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Route msg) throws Exception {
		System.out.println("------------");
		handleMessage(msg, ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("Unexpected exception from downstream.", cause);
		ctx.close();
	}

}