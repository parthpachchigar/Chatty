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
	public static void discoverEdges() {
		//TODO: Write the same code as given in the main method of UdpClient
		//TODO: After writing that code here change handler from UdpClientHandler to EdgeDiscoveryHandler over here
	}
	public static void displayEdgeStatus() {
		// gash.router.server.edge.EdgeInfo.availableNodes contains the id of nodes which are active
		// TODO: from gash.router.server.state.State.myConfig for each nodeId available in RoutingEntry 
		// array list check if node id is present in gash.router.server.edge.EdgeInfo.availableNodes
		// and if present print that node as active otherwise inactive
	}
	
	// TODO: Create two timer tasks
	// One will call discoverEdges every 5 sec
	// Another will call displayEdgeStatus every 4 sec 
	
}
