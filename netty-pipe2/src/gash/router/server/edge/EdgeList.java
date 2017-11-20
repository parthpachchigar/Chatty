package gash.router.server.edge;

import java.util.HashMap;

public class EdgeList {
	protected HashMap<Integer, EdgeInfo> map = new HashMap<Integer, EdgeInfo>();

	public HashMap<Integer, EdgeInfo> getMap() {
		return map;
	}

	public void setMap(HashMap<Integer, EdgeInfo> map) {
		this.map = map;
	}

	public EdgeList() {
	}

	public EdgeInfo createIfNew(int ref, String host, int port) {
		if (hasNode(ref))
			return getNode(ref);
		else
			return addNode(ref, host, port);
	}

	public EdgeInfo addNode(int ref, String host, long port) {
		if (!verify(ref, host, port)) {
			// TODO log error
			throw new RuntimeException("Invalid node info");
		}

		if (!hasNode(ref)) {
			EdgeInfo ei = new EdgeInfo(ref, host, port);
			map.put(ref, ei);
			return ei;
		} else
			return null;
	}

	private boolean verify(int ref, String host, long port) {
		if (ref < 0 || host == null || port < 1024)
			return false;
		else
			return true;
	}

	public boolean hasNode(int ref) {
		return map.containsKey(ref);

	}

	public EdgeInfo getNode(int ref) {
		return map.get(ref);
	}

	public void removeNode(int ref) {
		map.remove(ref);
	}

	public void clear() {
		map.clear();
	}
}
