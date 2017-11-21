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
package gash.router.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import gash.router.client.CommListener;
import gash.router.client.MessageClient;
import routing.MsgInterface.Message;
import routing.MsgInterface.Route;

public class ConnectApp implements CommListener {
	private MessageClient mc;
	static String uname;
	static String destination_id;

	public ConnectApp(MessageClient mc) {
		init(mc);
	}

	private void init(MessageClient mc) {
		this.mc = mc;
		this.mc.addListener(this);
	}
	
	Message.Builder msg = Message.newBuilder();

	public Route sendMessage(String message){
		msg.setType(Message.Type.SINGLE);
		msg.setSenderId(uname);
		msg.setPayload(message);
		msg.setReceiverId(destination_id);
		msg.setTimestamp("systemTime");
		msg.setAction(Message.ActionType.POST);

		Route.Builder route = Route.newBuilder();
		route.setId(123);
		route.setPath(Route.Path.MESSAGE);
		route.setMessage(msg);
		return route.build();
	}
	public void continuePing() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Username: ");
		for (;;) {
			String line = in.readLine();
			if (line == null) {
				break;
			} else if(uname == null){
				uname = line;
				System.out.print("destination_id: ");
			} else if(destination_id == null){
				destination_id = line;
			} else {
				mc.sendMessage(sendMessage(line));
			}
		}
	}

	@Override
	public String getListenerID() {
		return "demo";
	}

	@Override
	public void onMessage(Route msg) {
		// TODO Auto-generated method stub
		
	}

}
