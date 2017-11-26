package gash.router.client;

import routing.MsgInterface.Route;

public class ListenerWorker extends Thread{
	CommListener commListener;
	Route message;
	public ListenerWorker(CommListener cl, Route msg) {
		commListener=cl;
		message=msg;
	}
	public void run() {
		commListener.onMessage(message);
	}
}
