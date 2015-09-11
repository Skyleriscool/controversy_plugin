package edu.umass.cs.ciir.controversy.database.dao;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.db.SqlUtils;
import utils.ir.lucene.LuceneUtils;
import edu.umass.cs.ciir.controversy.LuceneWikiIndexSettings;
import edu.umass.cs.ciir.controversy.Settings;
import edu.umass.cs.ciir.controversy.database.ConnectionPool;
import edu.umass.cs.ciir.controversy.database.entity.WikiEntryRating;

public class WikiEntryRatingDAO {
	
	public static final String TABLE = "wikientry_rating";
	public static final String COLUME_ID = "autoid";
	public static final String COLUME_REQUESTID = RequestDAO.COLUME_ID;
	public static final String COLUME_TIME = "timestamp";
	public static final String COLUME_ENTRY = "entry";
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
				+ "  `" + COLUME_ENTRY + "` VARCHAR(333) NOT NULL," + "\n"
				+ "  `" + COLUME_RATING + "` INT NOT NULL," + "\n"
				+ "  PRIMARY KEY (`" + COLUME_ID + "`)," + "\n"
				+ "  INDEX `" + COLUME_REQUESTID + "` USING BTREE (`" + COLUME_REQUESTID + "` ASC)," + "\n"
				+ "  INDEX `" + COLUME_ENTRY + "` USING BTREE (`" + COLUME_ENTRY + "` ASC))" + "\n"
				+ "ENGINE = MyISAM" + "\n"
				+ "DEFAULT CHARACTER SET = utf8;";
	}
	
	public static void add( WikiEntryRating rating ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		st.executeUpdate( addSql( rating ) );
		st.close();
		conn.close();
	}
	
	private static String addSql( WikiEntryRating rating ) {
		return "INSERT INTO `" + TABLE + "`(" + COLUME_REQUESTID + "," + COLUME_TIME + "," + COLUME_ENTRY + "," + COLUME_RATING + ") VALUES(" + rating.getRequestid() + "," + rating.getTimestamp() + ",'" + SqlUtils.format( rating.getEntry() ) + "'," + rating.getRating() + ")";
	}
	
	public static Map<String, Integer> searchLastRating( String userid, String url ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		String sql_search = "SELECT " + WikiEntryRatingDAO.COLUME_ENTRY + ", " + WikiEntryRatingDAO.COLUME_RATING + " \n"
				+ "FROM " + RequestDAO.TABLE + ", " + WikiEntryRatingDAO.TABLE + " \n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_REQUESTID + " \n"
				+ "AND " + RequestDAO.COLUME_USER + " = '" + SqlUtils.format( userid ) + "' AND " + RequestDAO.COLUME_URL + " = '" + SqlUtils.format( url ) + "' \n"
				+ "ORDER BY " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_TIME + " DESC";
		ResultSet rs = st.executeQuery( sql_search );
		Map<String, Integer> ratings = new TreeMap<String, Integer>();
		while ( rs.next() ) {
			String entry = rs.getString( 1 );
			int rating = rs.getInt( 2 );
			if ( !ratings.containsKey( entry ) ) {
				ratings.put( entry, rating );
			}
		}
		rs.close();
		st.close();
		conn.close();
		return ratings;
	}
	
	public static Map<String, Integer> searchLastRating( String userid, String url, String text ) throws SQLException {
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		String sql_search = "SELECT " + RequestDAO.COLUME_TEXT + ", " + WikiEntryRatingDAO.COLUME_ENTRY + ", " + WikiEntryRatingDAO.COLUME_RATING + " \n"
				+ "FROM " + RequestDAO.TABLE + ", " + WikiEntryRatingDAO.TABLE + " \n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_REQUESTID + " \n"
				+ "AND " + RequestDAO.COLUME_USER + " = '" + SqlUtils.format( userid ) + "' AND " + RequestDAO.COLUME_URL + " = '" + SqlUtils.format( url ) + "' AND " + RequestDAO.COLUME_TEXTHASH + " = " + text.hashCode() + " \n"
				+ "ORDER BY " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_TIME + " DESC";
		ResultSet rs = st.executeQuery( sql_search );
		Map<String, Integer> ratings = new TreeMap<String, Integer>();
		while ( rs.next() ) {
			if ( text.equals( rs.getString( 1 ) ) ) {
				String entry = rs.getString( 2 );
				int rating = rs.getInt( 3 );
				if ( !ratings.containsKey( entry ) ) {
					ratings.put( entry, rating );
				}
			}
		}
		rs.close();
		st.close();
		conn.close();
		return ratings;
	}
	
	/**
	 * Dump the latest rating of wikipedia page relevance.
	 */
	public static List<Object[]> dumpLastestRatings() throws SQLException, IOException {
		
		Directory dir = FSDirectory.open( new File( LuceneWikiIndexSettings.path_index ) );
		IndexReader index = DirectoryReader.open( dir );
		
		Set<String> processed = new TreeSet<String>();
		List<Object[]> ratings = new ArrayList<Object[]>();
		Connection conn = ConnectionPool.getConnectionUpdate();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery( "SELECT " + RequestDAO.COLUME_USER + ", " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_TIME + ", " + RequestDAO.COLUME_URL + ", " + RequestDAO.COLUME_TEXT + ", " + WikiEntryRatingDAO.COLUME_ENTRY + ", " + WikiEntryRatingDAO.COLUME_RATING + "\n"
				+ "FROM " + RequestDAO.TABLE + ", " + WikiEntryRatingDAO.TABLE + "\n"
				+ "WHERE " + RequestDAO.TABLE + "." + RequestDAO.COLUME_ID + " = " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_REQUESTID + "\n"
				+ "ORDER BY " + WikiEntryRatingDAO.TABLE + "." + WikiEntryRatingDAO.COLUME_TIME + " DESC;" );
		while ( rs.next() ) {
			String user = rs.getString( 1 );
			long timestamp = rs.getLong( 2 );
			String url = rs.getString( 3 );
			String text = rs.getString( 4 );
			String entry = rs.getString( 5 );
			int docid = LuceneUtils.find( index, LuceneWikiIndexSettings.field_key, entry );
			if ( docid >= 0 ) {
				entry = index.document( docid ).get( LuceneWikiIndexSettings.field_title );
			}
			int rating = rs.getInt( 6 );
			String key = user + url + text + entry;
			if ( !processed.contains( key ) ) {
				// only dump the latest rating for each URL
				processed.add( key );
				ratings.add( new Object[] { user, Settings.TIME_FORMAT_MS.format( new Date( timestamp ) ), url, text, entry, rating } );
			}
		}
		rs.close();
		st.close();
		conn.close();
		
		index.close();
		dir.close();
		
		return ratings;
		
	}
}
