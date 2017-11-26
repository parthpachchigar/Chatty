package gash.router.server.state;

import java.util.Timer;
import java.util.TimerTask;

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

public class CandidateState extends State implements Runnable {
	private static CandidateState INSTANCE = null;
	private int majorityNumber;
	private int TotalResponses;
	Timer timer = new Timer();
	Timer timer2 = new Timer();
	Thread discover = null;
	Thread display = null;

	private CandidateState() {
		// TODO Auto-generated constructor stub
	}

	public static CandidateState getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CandidateState();
		}
		return INSTANCE;
	}

	@Override
	public void run() {

		MessageServer.logger.info("=========== CANDIDATE SERVICE STARTED ===========");
		discover = new Thread() {
			@Override
			public void run() {
				while (running) {
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
		display = new Thread() {
			@Override
			public void run() {
				while (running) {
					EdgeMonitor.displayEdgeStatus();
					;
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
		startElection();
		while (running) {

		}
	}

	public void handleVote() {
		TotalResponses++;
	}

	private void startElection() {
		majorityNumber = 0;
		TotalResponses = 0;

		for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {
			MessageServer.logger.debug("I have started contacting");
			System.out.println(ei.isActive());
			System.out.println(ei.getChannel());

			if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
				// Create route message for vote using ping as path
				// set network discovery packet in request mode as a payload
				// vote request message id is 111
				Route.Builder voteMessage = Route.newBuilder();
				voteMessage.setId(111);
				voteMessage.setPath(Path.PING);
				NetworkDiscoveryPacket.Builder ndp = NetworkDiscoveryPacket.newBuilder();
				ndp.setSender(Sender.INTERNAL_SERVER_NODE);
				ndp.setMode(Mode.REQUEST);
				ndp.setNodeId("" + State.myConfig.getNodeId());
				ndp.setNodeAddress(State.myConfig.getHost());
				ndp.setNodePort(State.myConfig.getWorkPort());
				ndp.setSecret("secret");
				voteMessage.setNetworkDiscoveryPacket(ndp.build());
				Route vote = voteMessage.build();
				MessageServer.logger.debug("Sent VoteRequestRPC to " + ei.getRef());
				ei.getComm().write(vote);
			}
		}
		new Thread() {
			@Override
			public void run() {
				MessageServer.logger.info("In decision");
				while (running) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (EdgeInfo.availableNodes.size() > 4) {
						majorityNumber = (4 + 1) / 2;
					} else {
						majorityNumber = (EdgeInfo.availableNodes.size() + 1) / 2;
					}
					if ((TotalResponses) >= (majorityNumber)) {
						if (isWinner()) {
							MessageServer.logger.info(
									gash.router.server.state.State.myConfig.getNodeId() + " has won the election.");
							gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.LEADER);

						} else {
							MessageServer.logger.info(
									gash.router.server.state.State.myConfig.getNodeId() + " has lost the election.");
							gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.FOLLOWER);

						}
					}
				}
			}

			private Boolean isWinner() {

				MessageServer.logger.debug("Total number of responses = " + TotalResponses);

				if ((TotalResponses) >= (majorityNumber)) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;

			}
		}.start();

	}

	@Override
	public void startService(State state) {

		running = Boolean.TRUE;
		cthread = new Thread((CandidateState) state);
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
