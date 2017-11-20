package gash.router.database;

import java.sql.ResultSetMetaData;
import java.util.List;

public interface DatabaseClient {
	
	byte[] get(String key);
	
	String post(byte[] image, long timestamp);
	
	public void put(String key, byte[] image, long timestamp);
	
	public void delete(String key);

	long getCurrentTimeStamp();

	List<Record> getNewEntries(long staleTimestamp);

	void putEntries(List<Record> list);

	List<Record> getAllEntries();

	void post(String key, byte[] image, long timestamp);
	public List getMessages(String fromId, String destId);

	ResultSetMetaData getMessage(String key);
	void postMessage(String message, String toId,String fromId);

	void createDb();
	
}
