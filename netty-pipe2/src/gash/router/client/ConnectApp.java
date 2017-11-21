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
	public void pingMessage(int N, String line) {
		long[] dt = new long[N];
		long st = System.currentTimeMillis(), ft = 0;
		for (int n = 0; n < N; n++) {
			if(n % 1400 == 0){
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {

				}

			}
			mc.sendMessage(sendMessage(line + " : " + n ));
			System.out.println("sent: "+ line + " : " + n);
			ft = System.currentTimeMillis();
			dt[n] = ft - st;
			st = ft;
		}
	}
	public void pingMessageWithUsers(String line) {
		final int n = 2000;
		pingMessage(n, line);

		uname = uname + "1";
		pingMessage(n, line);

		uname = uname + "2";
		pingMessage(n, line);

		uname = uname + "3";
		pingMessage(n, line);

		uname = uname + "4";
		pingMessage(n, line);

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
				System.out.print("message: ");
			} else {
				mc.sendMessage(sendMessage(line));
				//pingMessageWithUsers(line);
				Thread.sleep(1 * 1000);
				System.out.print("message: ");
			}
		}
	}

	@Override
	public String getListenerID() {
		return "demo";
	}

	@Override
	public void onMessage(Route msg) {
		System.out.println("--> got incoming message" + msg.toString());
		// TODO Auto-generated method stub
		
	}

}
