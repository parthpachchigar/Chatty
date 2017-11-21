package gash.router.server.state;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import gash.router.server.edge.EdgeMonitor;
import io.netty.channel.ChannelFuture;
import routing.MsgInterface.NetworkDiscoveryPacket;
import routing.MsgInterface.Route;
import routing.MsgInterface.NetworkDiscoveryPacket.Mode;
import routing.MsgInterface.NetworkDiscoveryPacket.Sender;
import routing.MsgInterface.Route.Path;

public class CandidateState extends State implements Runnable {
	public static Logger logger = LoggerFactory.getLogger("server");
	private static CandidateState INSTANCE = null;
	private int numberOfYESResponses;
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

		logger.info("-----------------------CANDIDATE SERVICE STARTED ----------------------------");
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
		numberOfYESResponses = 0;
		TotalResponses = 0;

		for (EdgeInfo ei : EdgeDiscoveryHandler.outbound.getMap().values()) {

			logger.debug("I have started contacting");
			System.out.println(ei.isActive());
			System.out.println(ei.getChannel());

			if (ei.isActive() && ei.getChannel() != null && ei.getRef() != 0) {
				// Create route message for vote using ping as path
				Route.Builder voteMessage = Route.newBuilder();
				voteMessage.setId(111);
				voteMessage.setPath(Path.PING);
				NetworkDiscoveryPacket.Builder ndpReq = NetworkDiscoveryPacket.newBuilder();
		        ndpReq.setMode(Mode.REQUEST);
		        ndpReq.setSender(Sender.INTERNAL_SERVER_NODE);
		        ndpReq.setNodeAddress(State.myConfig.getHost());//State.myConfig.getHost()
		        ndpReq.setNodePort(State.myConfig.getWorkPort());//State.myConfig.getWorkPort()
		        ndpReq.setNodeId(""+State.myConfig.getNodeId());
		        
		        ndpReq.setSecret("secret");
		        voteMessage.setNetworkDiscoveryPacket(ndpReq.build());
				Route vote = voteMessage.build();
				logger.debug("Sent VoteRequestRPC to " + ei.getRef());
				ChannelFuture cf = ei.getChannel().writeAndFlush(vote);
				if (cf.isDone() && !cf.isSuccess()) {
					logger.debug("failed to send message (VoteRequest) to server");
				}
			}
		}
		new Thread() {
			@Override
			public void run() {
				
				while (running) {
					System.out.println("In decision");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (EdgeInfo.availableNodes.size() > gash.router.server.state.State.myConfig.getRouting().size()) {
						numberOfYESResponses = (gash.router.server.state.State.myConfig.getRouting().size() + 1) / 2;
					} else {
						numberOfYESResponses = (EdgeInfo.availableNodes.size() + 1) / 2;
					}
					if (TotalResponses >= numberOfYESResponses) {
						if (isWinner()) {
							System.out.println("In winner");
							logger.info(gash.router.server.state.State.myConfig.getNodeId() + " has won the election.");
							gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.LEADER);

						} else {
							System.out.println("In looser");
							logger.info(
									gash.router.server.state.State.myConfig.getNodeId() + " has lost the election.");
							gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.FOLLOWER);

						}
					}
				}
			}

			private Boolean isWinner() {

				logger.debug("Total number of responses = " + TotalResponses);

				if ((TotalResponses) > (EdgeInfo.availableNodes.size() + 1) / 2) {
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
				logger.error("Exception", e);
			}
			logger.error("cthread successfully stopped.");
		}
		if (display != null) {
			try {
				display.join();
			} catch (InterruptedException e) {
				logger.error("Exception", e);
			}
			logger.error("display successfully stopped.");
		}
		if (discover != null) {
			try {
				discover.join();
			} catch (InterruptedException e) {
				logger.error("Exception", e);
			}
			logger.error("discover successfully stopped.");
		}
	}

}

class DiscoverTask extends TimerTask {
	public DiscoverTask() {

	}

	@Override
	public void run() {
		EdgeMonitor.discoverEdges();

	}
}

class DisplayEdgeTask extends TimerTask {

	@Override
	public void run() {
		EdgeMonitor.displayEdgeStatus();
	}
}
