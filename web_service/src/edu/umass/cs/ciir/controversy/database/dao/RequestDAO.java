package edu.umass.cs.ciir.controversy.database.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

import utils.db.SqlUtils;
import edu.umass.cs.ciir.controversy.database.ConnectionPool;
import edu.umass.cs.ciir.controversy.database.entity.Request;

public class RequestDAO {
	
	public static final String TABLE = "request";
	
	public static final String COLUME_ID = "requestid";
	public static final String COLUME_TIME = "timestamp";
	public static final String COLUME_IP = "ip";
	public static final String COLUME_USER = "userid";
	public static final String COLUME_URL = "url";
	public static final String COLUME_TEXT = "text";
	public static final String COLUME_TEXTHASH = "texthash";
	public static final String COLUME_WEBPAGE = "webpage";
	public static final String COLUME_PARAM = "param";
	
	public static void dropTable() throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		st.execute( "DROP TABLE IF EXISTS `" + TABLE + "`;" );
		st.close();
		conn.close();
	}
	
	public static void createTable() throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		st.execute( createTableSql() );
		st.close();
		conn.close();
	}
	
	private static String createTableSql() {
		return "CREATE TABLE IF NOT EXISTS `" + TABLE + "` (" + "\n"
				+ "  `" + COLUME_ID + "` BIGINT(64) UNSIGNED NOT NULL AUTO_INCREMENT," + "\n"
				+ "  `" + COLUME_TIME + "` BIGINT(64) UNSIGNED NOT NULL," + "\n"
				+ "  `" + COLUME_IP + "` VARCHAR(100) NOT NULL," + "\n"
				+ "  `" + COLUME_USER + "` VARCHAR(100) NOT NULL," + "\n"
				+ "  `" + COLUME_URL + "` VARCHAR(333) NOT NULL," + "\n"
				+ "  `" + COLUME_TEXTHASH + "` INT NOT NULL," + "\n"
				+ "  `" + COLUME_TEXT + "` MEDIUMTEXT NOT NULL," + "\n"
				+ "  `" + COLUME_WEBPAGE + "` LONGTEXT NOT NULL," + "\n"
				+ "  `" + COLUME_PARAM + "` VARCHAR(500) NOT NULL," + "\n"
				+ "  PRIMARY KEY (`" + COLUME_ID + "`)," + "\n"
				+ "  INDEX `" + COLUME_USER + "` USING BTREE (`" + COLUME_USER + "` ASC)," + "\n"
				+ "  INDEX `" + COLUME_URL + "` USING BTREE (`" + COLUME_URL + "` ASC)," + "\n"
				+ "  INDEX `" + COLUME_TEXTHASH + "` USING BTREE (`" + COLUME_TEXTHASH + "` ASC))" + "\n"
				+ "ENGINE = MyISAM" + "\n"
				+ "DEFAULT CHARACTER SET = utf8;";
	}
	
	public static Request add( Request record ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		st.executeUpdate( addSql( record ), Statement.RETURN_GENERATED_KEYS );
		ResultSet rs = st.getGeneratedKeys();
		if ( rs.next() ) {
			record.setRequestid( rs.getLong( 1 ) );
		}
		rs.close();
		st.close();
		conn.close();
		return record;
	}
	
	private static String addSql( Request record ) {
		String url = SqlUtils.format( record.getUrl() );
		String param = SqlUtils.format( record.getParam() );
		if ( url.length() > 333 ) {
			url = url.substring( 0, 333 );
		}
		if ( param.length() > 500 ) {
			param = param.substring( 0, 500 );
		}
		
		return "INSERT INTO `" + TABLE + "`(" + COLUME_TIME + "," + COLUME_IP + "," + COLUME_USER + "," + COLUME_URL + "," + COLUME_TEXT + "," + COLUME_TEXTHASH + "," + COLUME_WEBPAGE + "," + COLUME_PARAM + ") " + "\n" +
				" VALUES(" + record.getTimestamp() + ",'" + SqlUtils.format( record.getIp() ) + "','" + SqlUtils.format( record.getUserid() ) + "','" + url + "','" + SqlUtils.format( record.getText() ) + "'," + record.getTextHash() + ",'" + SqlUtils.format( record.getWebpage() ) + "','" + param + "')";
		
	}
	
}
