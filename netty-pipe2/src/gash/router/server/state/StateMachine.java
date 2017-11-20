package gash.router.server.state;

public class StateMachine implements Runnable {
	public static boolean running;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(State.getStatus()==State.Status.FOLLOWER && !running) {
				System.out.println("FS");
				FollowerState fs=FollowerState.getInstance();
				new Thread(fs).start();
			}else if(State.getStatus()==State.Status.CANDIDATE && !running) {
				System.out.println("CS");
				CandidateState cs=CandidateState.getInstance();
				new Thread(cs).start();
			}else if(State.getStatus()==State.Status.LEADER && !running) {
				System.out.println("LS");
				LeaderState ls=LeaderState.getInstance();
				new Thread(ls).start();
			}
			
		}
		
	}

}
