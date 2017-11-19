package gash.router.server.edge;

import gash.router.container.RoutingConf;
import gash.router.server.state.State;

public class EdgeMonitor {
	
	private static EdgeMonitor instance;
	private EdgeMonitor() {
		
	}
	public static EdgeMonitor getInstance() {
		if(instance==null) {
			instance=new EdgeMonitor();
		}
		return instance;
	}
}
