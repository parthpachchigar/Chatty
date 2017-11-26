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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import gash.router.client.CommListener;
import gash.router.client.MessageClient;
import routing.MsgInterface.Group;
import routing.MsgInterface.Message;
import routing.MsgInterface.MessagesRequest;
import routing.MsgInterface.Route;
import routing.MsgInterface.User;

public class ConnectApp implements CommListener {
	private MessageClient mc;
	static String uname;
	static String destination_id;
	public static ArrayList<Nodes> nodes = new ArrayList<Nodes>();
	private int id = -1;
	public ConnectApp(MessageClient mc) {
		init(mc);
	}
	private int nextId() {
		id++;
		if(id == 111 ||id == 222 ||id == 333 ||id == 444 ||id == 555 ||id == 666 ) {
			id+=2;
		}
		return id;
	}
	private void init(MessageClient mc) {
		this.mc = mc;
		this.mc.addListener(this);
	}

	

	public void continuePing() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for (;;) {
			Route.Builder route = Route.newBuilder();
			route.setId(nextId());
			System.out.println(
					"\n\n========== Menu ==========\n1. Post Message\n2. Get Messages\n3. Register User\n4. Create Group\n5. Delete Group\n6. Add User to Group\n7. Exit\nEnter your choice:");
			int choice = Integer.parseInt(in.readLine());
			switch (choice) {
			case 1:
				route.setPath(Route.Path.MESSAGE);
				Message.Builder msg = Message.newBuilder();
				msg.setType(Message.Type.SINGLE);
				System.out.println("Enter Username: ");
				msg.setSenderId(in.readLine());
				System.out.println("Enter Receipient: ");
				msg.setReceiverId(in.readLine());
				System.out.println("Enter Message: ");
				msg.setPayload(in.readLine());
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
				msg.setTimestamp(dateFormat.format(cal));
				route.setMessage(msg.build());
				CommConnection.getInstance().write(route.build());
				break;
			case 2:
				route.setPath(Route.Path.MESSAGES_REQUEST);
				MessagesRequest.Builder mr=MessagesRequest.newBuilder();
				mr.setType(MessagesRequest.Type.USER);
				System.out.println("Enter Name for whom you want messages (user or group name): ");

				mr.setId(in.readLine());
				route.setMessagesRequest(mr.build());
				CommConnection.getInstance().write(route.build());
				break;
			case 3:
				route.setPath(Route.Path.USER);
				User.Builder u=User.newBuilder();
				u.setAction(User.ActionType.REGISTER);
				System.out.println("Enter Username to be Registered: ");
				u.setUname(in.readLine());
				route.setUser(u.build());
				CommConnection.getInstance().write(route.build());
				break;
			case 4:
				route.setPath(Route.Path.GROUP);
				Group.Builder g=Group.newBuilder();
				g.setAction(Group.ActionType.CREATE);
				System.out.println("Enter Username to be Created: ");
				g.setGname(in.readLine());
				route.setGroup(g.build());
				CommConnection.getInstance().write(route.build());
				break;
			case 5:
				route.setPath(Route.Path.GROUP);
				g=Group.newBuilder();
				g.setAction(Group.ActionType.DELETE);
				System.out.println("Enter Username to be Deleted: ");
				g.setGname(in.readLine());
				route.setGroup(g.build());
				
				CommConnection.getInstance().write(route.build());
				break;
			case 6:
				route.setPath(Route.Path.GROUP);
				g=Group.newBuilder();
				g.setAction(Group.ActionType.ADDUSER);
				System.out.println("Enter Group Name: ");
				g.setGname(in.readLine());
				System.out.println("Enter Username to be Added: ");
				g.setUsername(in.readLine());
				route.setGroup(g.build());
				
				CommConnection.getInstance().write(route.build());
				break;
			case 7:
				System.exit(0);
				break;
			default:
				System.out.println("Invalid Choice");
			}
		}
	}

	@Override
	public String getListenerID() {
		return "demo";
	}

	@Override
	public void onMessage(Route msg) {
		if (msg != null) {
			System.out.println("--> got incoming message" + msg.toString());
		}

	}

	public static void main(String a[]) {
		
		try {
			UdpClient.find();
			if(ConnectApp.nodes.size()>0) {
				MessageClient mc = new MessageClient(ConnectApp.nodes.get(0).host, (int)ConnectApp.nodes.get(0).port);
				ConnectApp ca = new ConnectApp(mc);
				ca.continuePing();
			}else {
				System.out.println("No active servers found .. try later");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
