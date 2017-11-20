package gash.router.server.state;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gash.router.server.edge.EdgeDiscoveryHandler;
import gash.router.server.edge.EdgeInfo;
import io.netty.channel.ChannelFuture;
import routing.MsgInterface.Route;
import routing.MsgInterface.User;
import routing.MsgInterface.Group;
import routing.MsgInterface.Message;





public class FollowerState extends State implements Runnable{
	public static Boolean isHeartBeatRecieved = Boolean.FALSE;
	public static int timeout=State.myConfig.getHeartbeatDt();
	Logger logger=LoggerFactory.getLogger("server");
	Timer timer;
	
	private static FollowerState INSTANCE = null;
	Thread fThread = null;
	Thread tThread = null;
	private FollowerState() {
		// TODO Auto-generated constructor stub
	}
	
	public static FollowerState getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FollowerState();

		}
		return INSTANCE;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("-----------------------FOLLOWER SERVICE STARTED ----------------------------");
		timeout=State.myConfig.getHeartbeatDt();
		initFollower();
		
		/*
		 * What should happen to existing thread of execution after the node state changes
		 * to CANDIDATE?
		 * 
		 * Why do we need to check here for the value of node state with FOLLOWER?
		 */
		fThread = new Thread(){
		    public void run(){
				
		    	while (running) {
					while (gash.router.server.state.State.getStatus() == gash.router.server.state.State.Status.FOLLOWER) {
					}
				}

		    }
		 };

		fThread.start();
//		ServerQueueService.getInstance().createGetQueue();
		
		
		
		
	}
	
	private void initFollower() {
		// TODO Auto-generated method stub
		
		tThread=new Thread(){
		    public void run(){
				
		    	while (running) {
		    		//System.out.println("Timeout:"+timeout);
					timeout=timeout-100;
					if(timeout==0) {
						System.out.println("Timeout:"+timeout);
						gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.CANDIDATE);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

		    }
		 };
		
		tThread.start();
		/*timer = new Timer();
         
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Timeout:"+timeout);
				timeout=timeout-100;
				if(timeout==0) {
					System.out.println("Timeout:"+timeout);
					gash.router.server.state.State.setStatus(gash.router.server.state.State.Status.CANDIDATE);

					
				}
			}
		}, 100);*/

	}

	
	
	public void onReceivingHeartBeatPacket() {
		timeout=State.myConfig.getHeartbeatDt();
	}

   
	@Override
	public void startService(State state) {

		running = Boolean.TRUE;
		cthread = new Thread((FollowerState) state);
		cthread.start();

	}

	@Override
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
	}
	

}
