package gash.router.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PostgreSQL implements DatabaseClient {
	Connection conn = null;

	public PostgreSQL(String url, String username, String password, String ssl) throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("ssl", ssl);
		conn = DriverManager.getConnection(url, props);
	}

		


	@Override
	public void delete(String key){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE messages set archived=now() WHERE key LIKE '"+key+"';");
			stmt.executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// initiate new everytime
		}
	}
		//@Override
	public ResultSet getMessages(String forWhom) {
		List list = null;
		Statement stmt = null;
		ResultSet rs=null;
		System.out.println("getMessage: " + forWhom);
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("Select distinct * FROM messages WHERE \"to_id\" ='"+forWhom+"' and archived=NULL;") ;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return rs;
	}
	@Override
	public void postMessage(String message, String toId,String fromId){
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO messages (message, to_id, from_id) VALUES ( ?, ?, ?)");
			ps.setString(1, message);
			ps.setString(2, toId);
			ps.setString(3, fromId);
			ResultSet set = ps.executeQuery();

		} catch (SQLException e) {

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void createDb(){
		Statement stmt = null;
		try {
			MigrationsPostgreSQL migrationSql = new MigrationsPostgreSQL();
			stmt = conn.createStatement();

			stmt.executeUpdate(migrationSql.seqMessageTable());
			stmt.executeUpdate(migrationSql.createMessageTable());

			stmt.executeUpdate(migrationSql.seqGroupTable());
			stmt.executeUpdate(migrationSql.createGroupTable());

			stmt.executeUpdate(migrationSql.seqUserTable());
			stmt.executeUpdate(migrationSql.createUserTable());

			stmt.executeUpdate(migrationSql.seqUserInGroupTable());
			stmt.executeUpdate(migrationSql.createUserInGroupTable());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// initiate new everytime
		}
	}
	public List<HashMap> resultSetToArrayList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		ArrayList list = new ArrayList();
		while (rs.next()){
			HashMap row = new HashMap(columns);
			for(int i=1; i<=columns; ++i){
				row.put(md.getColumnName(i),rs.getObject(i));
			}
			list.add(row);
		}

		return list;
	}




	



	@Override
	public void registerUser(String userName) {
		// TODO Auto-generated method stub
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO users (username) VALUES ( ?)");
			ps.setString(1, userName);
			
			ResultSet set = ps.executeQuery();

		} catch (SQLException e) {

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
	}




	@Override
	public void createGroup(String groupName) {
		// TODO Auto-generated method stub
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO groups (gname) VALUES ( ?)");
			ps.setString(1, groupName);
			ResultSet set = ps.executeQuery();

		} catch (SQLException e) {

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}




	@Override
	public void deleteGroup(String groupName) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE groups set archived=now() WHERE gname='"+groupName+"';");
			stmt.executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// initiate new everytime
		}

	}




	@Override
	public void addUserToGroup(String userName, String groupName) {
		// TODO Auto-generated method stub
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO usergroups (gid,uid) VALUES ((select gid from groups where gname='"+groupName+"'),(select id from users where username='"+userName+"') )");
			
			ResultSet set = ps.executeQuery();

		} catch (SQLException e) {

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
