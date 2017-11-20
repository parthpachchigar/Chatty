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
	public byte[] get(String key) {
		Statement stmt = null;
		byte[] image=null;
		System.out.println(key);
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select image FROM testtable WHERE \"key\" LIKE '"+key+"'");

			while (rs.next()) {
				image=rs.getBytes(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return image;
	}

	@Override
	public List<Record> getNewEntries(long staleTimestamp) {
		Statement stmt = null;
		List<Record> list = new ArrayList<Record>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select key, image, timestamp FROM testtable where timestamp > " + staleTimestamp);

			while (rs.next()) {
				list.add(new Record(rs.getString(1), rs.getBytes(2), rs.getLong(3)));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return list;
	}


	@Override
	public List<Record> getAllEntries() {
		Statement stmt = null;
		List<Record> list = new ArrayList<Record>();
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select key, image, timestamp FROM testtable");

			while (rs.next()) {
				list.add(new Record(rs.getString(1), rs.getBytes(2), rs.getLong(3)));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return list;
	}


	@Override
	public long getCurrentTimeStamp() {
		Statement stmt = null;
		long timestamp = 0;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select max(timestamp) FROM testtable");

			while (rs.next()) {
				timestamp = rs.getLong(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return timestamp;
	}


	@Override
	public String post(byte[] image, long timestamp){
		String key = UUID.randomUUID().toString();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO testtable VALUES ( ?, ?, ?)");
			ps.setString(1, key);
			ps.setBytes(2, image);
			ps.setLong(3, timestamp);
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

		return key;
	}


	@Override
	public void post(String key, byte[] image, long timestamp){
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO testtable VALUES ( ?, ?, ?)");
			ps.setString(1, key);
			ps.setBytes(2, image);
			ps.setLong(3, timestamp);
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
	public void put(String key, byte[] image, long timestamp){
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE testtable SET image= ? , timestamp = ?  WHERE key LIKE ?");
			ps.setBytes(1, image);
			ps.setLong(2, timestamp);
			ps.setString(3, key);
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
	public void putEntries(List<Record> list){
		for (Record record : list) {
			put(record.getKey(), record.getImage(), record.getTimestamp());
		}
	}


	@Override
	public void delete(String key){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM messages WHERE key LIKE '"+key+"';");
			stmt.executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// initiate new everytime
		}
	}
	@Override
	public ResultSetMetaData getMessage(String key) {
		ResultSetMetaData rsmd = null;
		Statement stmt = null;
		System.out.println("getMessage: " + key);
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * FROM messages WHERE \"id\" ='"+key+"';") ;
			rsmd=rs.getMetaData();

			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return rsmd;
	}
	@Override
	public List getMessages(String fromId, String destId) {
		List list = null;
		Statement stmt = null;
		System.out.println("getMessage: " + fromId);
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * FROM messages WHERE \"from_id\" ='"+fromId+"';") ;
			list = resultSetToArrayList(rs);
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return list;
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

}
