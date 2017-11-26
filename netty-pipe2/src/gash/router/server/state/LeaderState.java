package gash.router.server.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.MessageServer;
import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.edge.EdgeMonitor;
import io.netty.channel.ChannelFuture;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route;
import routing.MsgInterface.Route.Path;

public class LeaderState extends State implements Runnable {
    //making it singleton
	private static LeaderState INSTANCE = null;
	Thread heartBt = null;
	Thread discover=null;
	Thread display=null;
	
	private LeaderState() {
		// TODO Auto-generated constructor stub

	}

	public static LeaderState getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LeaderState();
		}
		return INSTANCE;
	}

	@Override
	public void run() {
		MessageServer.logger.info("=========== LEADER SERVICE STARTED ===========");
//		NodeState.currentTerm++;
	//	initLatestTimeStampOnUpdate();
		discover=new Thread() {
			@Override
			public void run() {
				while(running) {
					EdgeMonitor.discoverEdges();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		discover.start();
		display=new Thread() {
			@Override
			public void run() {
				while(running) {
					EdgeMonitor.displayEdgeStatus();;
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		display.start();
		heartBt = new Thread(){
		    public void run(){
				while (running) {
					try {
						Thread.sleep(gash.router.server.state.State.myConfig.getHeartbeatDt());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendHeartBeat();
				}
		    }
		 };

		heartBt.start();
		//ServerQueueService.getInstance().createQueue();
	}

	private void initLatestTimeStampOnUpdate() {

		//NodeState.setTimeStampOnLatestUpdate(DatabaseService.getInstance().getDb().getCurrentTimeStamp());

	}
	
	
	public void sendHeartBeat() {
		for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
			MessageServer.logger.debug("I have started contacting");
			
			if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
				// Create route message for heartbeat using header as path
				Route.Builder heartbeatMessage = Route.newBuilder();
				heartbeatMessage.setId(222);
				heartbeatMessage.setPath(Path.HEADER);
				NetworkDiscoveryPacket.Builder ndp = NetworkDiscoveryPacket.newBuilder();
				ndp.setNodeAddress(State.myConfig.getHost());
				ndp.setNodePort(State.myConfig.getWorkPort());
				ndp.setMode(Mode.REQUEST);
				ndp.setSender(Sender.INTERNAL_SERVER_NODE);
				ndp.setSecret("secret");
				heartbeatMessage.setNetworkDiscoveryPacket(ndp.build());
				Route heartbeat = heartbeatMessage.build();
				MessageServer.logger.debug("Sent heartbeat to " + ei.getRef());
				ei.getComm().write(heartbeat);
			}
		}

	}
	
	
	
	public void startService(State state) {
		running = Boolean.TRUE;
		cthread = new Thread((LeaderState) state);
		cthread.start();
	}

	public void stopService() {
		running = Boolean.FALSE;
		if (cthread != null) {
            try {
                cthread.join();
            } catch (InterruptedException e) {
            	MessageServer.logger.error("Exception", e);
            }
            MessageServer.logger.info("cthread successfully stopped.");
        } 
		if (display != null) {
			try {
				display.join();
			} catch (InterruptedException e) {
				MessageServer.logger.error("Exception", e);
			}
			MessageServer.logger.info("display successfully stopped.");
		}
		if (discover != null) {
			try {
				discover.join();
			} catch (InterruptedException e) {
				MessageServer.logger.error("Exception", e);
			}
			MessageServer.logger.info("discover successfully stopped.");
		}

	}

}
