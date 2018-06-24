/**
 * 
 */
package ncab.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author hm185049
 *
 */
public class DBConnectionRo {
	public Connection getConnection(){		
		Connection connection = null;
		
		try {
			String connectionUrl = "jdbc:mysql://ncabdb.cdfikpedkmtw.us-east-1.rds.amazonaws.com:1525/NCABDB";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionUrl, "ncabdbro", "Help123");
			System.out.println("connected to DB endpoint");
		}
		catch (InstantiationException e)	{e.printStackTrace();}
		catch (IllegalAccessException e)	{e.printStackTrace();}
		catch (ClassNotFoundException e)	{e.printStackTrace();}
		catch (SQLException e)				{e.printStackTrace();}
		
		return connection;
	}

}
