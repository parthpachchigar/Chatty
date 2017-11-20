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

import routing.MsgInterface.Route;

/**
 * processes requests of message passing - demonstration
 * 
 * @author gash
 * 
 */
public class ReplicateResource implements RouteResource {
	protected static Logger logger = LoggerFactory.getLogger("message");

	@Override
	public String getPath() {
		return "/message";
	}

	@Override
	public Route process(Route body) {
		logger.info(body.toString());
		if(body.hasMessage()) {
			
		}
		if(body.hasUser()) {
			
		}
		return body;
	}

}
