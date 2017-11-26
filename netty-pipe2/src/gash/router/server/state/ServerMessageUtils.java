package gash.router.server.state;

import com.google.protobuf.ByteString;

import routing.MsgInterface;
import routing.MsgInterface.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMessageUtils {

	public static Route prepareMessagesResponseBuilder(String uname, String destination_id, List<HashMap> list) {
		MsgInterface.MessagesResponse.Builder msgResp = MsgInterface.MessagesResponse.newBuilder();
		list.forEach(item-> {
			MsgInterface.Message  m = prepareMessageBuilder(item);
			msgResp.addMessages(m);
		});
		msgResp.setType(MsgInterface.MessagesResponse.Type.USER); //TODO: if group cond.
		msgResp.setId(uname);

		MsgInterface.Message.Builder msg = MsgInterface.Message.newBuilder();
		msg.setType(MsgInterface.Message.Type.SINGLE);
		msg.setSenderId(uname);
		msg.setReceiverId(destination_id);
		msg.setTimestamp("10:01");
		msg.setAction(MsgInterface.Message.ActionType.POST);
		msg.setPayload("");

		Route.Builder route = MsgInterface.Route.newBuilder();
		route.setId(123);
		route.setPath(Route.Path.MESSAGES_RESPONSE);

		route.setMessage(msg);
		route.setMessagesResponse(msgResp);
		return route.build();
	}
	public static MsgInterface.Message prepareMessageBuilder(HashMap message) {
		MsgInterface.Message.Builder msg = MsgInterface.Message.newBuilder();
		msg.setType(MsgInterface.Message.Type.SINGLE); //TODO: if group cond.
		msg.setSenderId(message.get("from_id").toString());
		msg.setReceiverId(message.get("to_id").toString());
		msg.setTimestamp(message.get("created").toString());
		msg.setAction(MsgInterface.Message.ActionType.POST);
		msg.setPayload(message.get("message").toString());
		return msg.build();
	}


	
	
}
