package gash.router.server.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.edge.EdgeMonitor;

public class LeaderState extends State implements Runnable {
    //making it singleton
	public static Logger logger=LoggerFactory.getLogger("server");
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
		logger.info("-----------------------LEADER SERVICE STARTED ----------------------------");
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
					//sendHeartBeat();
				}
		    }
		 };

		heartBt.start();
		//ServerQueueService.getInstance().createQueue();
	}

	private void initLatestTimeStampOnUpdate() {

		//NodeState.setTimeStampOnLatestUpdate(DatabaseService.getInstance().getDb().getCurrentTimeStamp());

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
