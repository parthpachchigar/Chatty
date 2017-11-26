package gash.router.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public interface DatabaseClient {
	
	
	public void delete(String key);

	ResultSet getMessages(String forWhom);

	void postMessage(String message, String toId,String fromId);
	
	void registerUser(String userName);
	
	void createGroup(String groupName);
	
	void deleteGroup(String groupName);
	
	void addUserToGroup(String userName, String groupName);

	void createDb();
	
}
