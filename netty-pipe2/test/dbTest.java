import java.sql.ResultSetMetaData;

import org.junit.Test;

import gash.router.database.DatabaseClient;
import gash.router.database.DatabaseService;
import gash.router.database.*;

public class dbTest {


	//TODO: Move to Migration script

	public void createDb() throws Exception{
		DatabaseService dbs= DatabaseService.getInstance();
		dbs.dbConfiguration("postgresql","jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
		DatabaseClient dbc= dbs.getDb();
		dbc.createDb();
		System.out.println("Finished");
	}

	@Test
	public void testmyDb() throws Exception{
		DatabaseService dbs= DatabaseService.getInstance();
		dbs.dbConfiguration("postgresql","jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pup");
		DatabaseClient dbc= dbs.getDb();
		dbc.postMessage("Hello World","1","2");
		ResultSetMetaData rsmd= dbc.getMessage("1");
		System.out.println(rsmd.getColumnCount());
	}
	
}

