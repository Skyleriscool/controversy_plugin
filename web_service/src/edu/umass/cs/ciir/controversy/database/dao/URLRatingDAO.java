package edu.umass.cs.ciir.controversy.database.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import utils.db.SqlUtils;
import edu.umass.cs.ciir.controversy.Settings;
import edu.umass.cs.ciir.controversy.database.ConnectionPool;
import edu.umass.cs.ciir.controversy.database.entity.URLRating;

public class URLRatingDAO {
	
	public static final String TABLE = "url_rating";
	public static final String COLUME_ID = "autoid";
	public static final String COLUME_REQUESTID = RequestDAO.COLUME_ID;
	public static final String COLUME_TIME = "timestamp";
	public static final String COLUME_RATING = "rating";
	
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
				+ "  `" + COLUME_REQUESTID + "` BIGINT(64) UNSIGNED NOT NULL," + "\n"
				+ "  `" + COLUME_TIME + "` BIGINT(64) UNSIGNED NOT NULL," + "\n"
				+ "  `" + COLUME_RATING + "` INT NOT NULL," + "\n"
				+ "  PRIMARY KEY (`" + COLUME_ID + "`)," + "\n"
				+ "  INDEX `" + COLUME_REQUESTID + "` USING BTREE (`" + COLUME_REQUESTID + "` ASC))" + "\n"
				+ "ENGINE = MyISAM" + "\n"
				+ "DEFAULT CHARACTER SET = utf8;";
	}
	
	public static void add( URLRating rating ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		st.executeUpdate( addSql( rating ) );
		st.close();
		conn.close();
	}
	
	private static String addSql( URLRating rating ) {
		return "INSERT INTO `" + TABLE + "`(" + COLUME_REQUESTID + "," + COLUME_TIME + "," + COLUME_RATING + ") VALUES(" + rating.getRequestid() + "," + rating.getTimestamp() + "," + rating.getRating() + ")";
	}
	
	public static Integer searchLastRating( String userid, String url ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		String sql_search = "SELECT " + URLRatingDAO.COLUME_RATING + " \n"
				+ "FROM " + RequestDAO.TABLE + ", " + URLRatingDAO.TABLE + " \n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_REQUESTID + " \n"
				+ "AND " + RequestDAO.COLUME_USER + " = '" + SqlUtils.format( userid ) + "' AND " + RequestDAO.COLUME_URL + " = '" + SqlUtils.format( url ) + "' \n"
				+ "ORDER BY " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_TIME + " DESC" + "\n"
				+ "LIMIT 1";
		ResultSet rs = st.executeQuery( sql_search );
		Integer rating = null;
		if ( rs.next() ) {
			rating = rs.getInt( 1 );
		}
		rs.close();
		st.close();
		conn.close();
		return rating;
	}
	
	public static Integer searchLastRating( String userid, String url, String text ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		String sql_search = "SELECT " + RequestDAO.COLUME_TEXT + ", " + URLRatingDAO.COLUME_RATING + " \n"
				+ "FROM " + RequestDAO.TABLE + ", " + URLRatingDAO.TABLE + " \n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_REQUESTID + " \n"
				+ "AND " + RequestDAO.COLUME_USER + " = '" + SqlUtils.format( userid ) + "' AND " + RequestDAO.COLUME_URL + " = '" + SqlUtils.format( url ) + "' AND " + RequestDAO.COLUME_TEXTHASH + " = " + text.hashCode() + " \n"
				+ "ORDER BY " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_TIME + " DESC";
		ResultSet rs = st.executeQuery( sql_search );
		Integer rating = null;
		while ( rs.next() ) {
			if ( text.equals( rs.getString( 1 ) ) ) {
				rating = rs.getInt( 2 );
				break;
			}
		}
		rs.close();
		st.close();
		conn.close();
		return rating;
	}
	
	/**
	 * Dump the latest rating of users for each URL.
	 */
	public static List<Object[]> dumpLastestRatings() throws SQLException {
		Set<String> processed = new TreeSet<String>();
		List<Object[]> ratings = new ArrayList<Object[]>();
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery( "SELECT " + RequestDAO.COLUME_USER + ", " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_TIME + ", " + RequestDAO.COLUME_URL + ", " + URLRatingDAO.COLUME_RATING + ", " + RequestDAO.COLUME_TEXT + " " + "\n"
				+ "FROM " + RequestDAO.TABLE + ", " + URLRatingDAO.TABLE + " " + "\n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_REQUESTID + " " + "\n"
				+ "ORDER BY " + URLRatingDAO.TABLE + "." + URLRatingDAO.COLUME_TIME + " DESC;" );
		while ( rs.next() ) {
			String user = rs.getString( 1 );
			long timestamp = rs.getLong( 2 );
			String url = rs.getString( 3 );
			int rating = rs.getInt( 4 );
			String text = rs.getString( 5 );
			String key = user + url + text;
			if ( !processed.contains( key ) ) {
				// only dump the latest rating for each URL
				processed.add( key );
				ratings.add( new Object[] { user, Settings.TIME_FORMAT_MS.format( new Date( timestamp ) ), url, rating, text } );
			}
		}
		rs.close();
		st.close();
		conn.close();
		return ratings;
	}
	
}
