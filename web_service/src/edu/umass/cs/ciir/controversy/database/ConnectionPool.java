package edu.umass.cs.ciir.controversy.database;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import edu.umass.cs.ciir.controversy.database.dao.RequestDAO;
import edu.umass.cs.ciir.controversy.database.dao.URLRatingDAO;
import edu.umass.cs.ciir.controversy.database.dao.WikiEntryRatingDAO;
import edu.umass.cs.ciir.controversy.ControversyScorerSettings;

/**
 * Managing pooled connections.
 * 
 * @author Jiepu Jiang
 * @version May 30, 2015
 */
public class ConnectionPool {
	
	public static final String path_config = "../../config_database";
	
	private static String host;
	private static String port;
	private static String db;
	private static String username;
	private static String password;
	
	public static BasicDataSource ds_search;
	public static BasicDataSource ds_update;
	
	static {
		try {
			
			URI uri = new ControversyScorerSettings().getClass().getResource( "/" ).toURI().resolve( path_config );
			File file = new File( uri.getPath() );
			Properties props = new Properties();
			InputStream instream = new FileInputStream( file );
			props.load( instream );
			instream.close();
			
			host = props.getProperty( "host" ).trim();
			port = props.getProperty( "port" ).trim();
			db = props.getProperty( "db" ).trim();
			username = props.getProperty( "username" ).trim();
			password = props.getProperty( "password" ).trim();
			
			int min_conn_search = Integer.parseInt( props.getProperty( "min_conn_search", "3" ) );
			int max_conn_search = Integer.parseInt( props.getProperty( "max_conn_search", "20" ) );
			int min_conn_update = Integer.parseInt( props.getProperty( "min_conn_update", "10" ) );
			int max_conn_update = Integer.parseInt( props.getProperty( "max_conn_update", "50" ) );
			
			ds_search = new BasicDataSource();
			ds_search.setDriverClassName( "com.mysql.jdbc.Driver" );
			ds_search.setUrl( "jdbc:mysql://" + host + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=UTF-8" );
			ds_search.setUsername( username );
			ds_search.setPassword( password );
			ds_search.setMinIdle( min_conn_search );
			ds_search.setMaxTotal( max_conn_search );
			
			ds_update = new BasicDataSource();
			ds_update.setDriverClassName( "com.mysql.jdbc.Driver" );
			ds_update.setUrl( "jdbc:mysql://" + host + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=UTF-8" );
			ds_update.setUsername( username );
			ds_update.setPassword( password );
			ds_update.setMinIdle( min_conn_update );
			ds_update.setMaxTotal( max_conn_update );
			
			// create tables if they are not in the database, existing tables will not be removed
			initDatabase();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnectionSearch() throws SQLException {
		return ds_search.getConnection();
	}
	
	public static Connection getConnectionUpdate() throws SQLException {
		return ds_update.getConnection();
	}
	
	private static void initDatabase() throws SQLException {
		RequestDAO.createTable();
		URLRatingDAO.createTable();
		WikiEntryRatingDAO.createTable();
	}
	
}
