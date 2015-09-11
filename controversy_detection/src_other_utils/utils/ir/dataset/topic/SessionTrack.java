package utils.ir.dataset.topic;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.io.IOException;
import java.io.InputStream;

import utils.ir.dataset.topic.session.TRECSession;

/**
 * Utilities of accessing data for TREC session track.
 * 
 * @author Jiepu Jiang
 * @version Feb 11, 2015
 */
public class SessionTrack {
	
	public static void main( String[] args ) {
		try {
			loadSessions( "2011" );
			loadSessions( "2012" );
			loadSessions( "2013" );
			loadSessions( "2014" );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static final String path_2011_sessions = "data/session/2011_sessions.xml";
	public static final String path_2011_topicmap = "data/session/2011_sessiontopicmap.txt";
	public static final String path_2011_qrels = "data/session/2011_qrels.txt";
	
	public static final String path_2012_sessions = "data/session/2012_sessions.xml";
	public static final String path_2012_topicmap = "data/session/2012_sessiontopicmap.txt";
	public static final String path_2012_qrels = "data/session/2012_qrels.txt";
	
	public static final String path_2013_sessions = "data/session/2013_sessions.xml";
	public static final String path_2013_topicmap = "data/session/2013_sessiontopicmap.txt";
	public static final String path_2013_qrels = "data/session/2013_qrels.txt";
	
	public static final String path_2014_sessions = "data/session/2014_sessions.xml";
	public static final String path_2014_topicmap = "data/session/2014_sessiontopicmap.txt";
	public static final String path_2014_qrels = "data/session/2014_qrels.txt";
	
	public static List<TRECSession> loadSessions( String queryset ) throws IOException {
		
		InputStream instream_sessions = null;
		InputStream instream_topicmap = null;
		InputStream instream_qrels = null;
		List<TRECSession> sessions = null;
		
		if ( queryset.equalsIgnoreCase( "2011" ) ) {
			
			instream_sessions = SessionTrack.class.getResourceAsStream( path_2011_sessions );
			instream_topicmap = SessionTrack.class.getResourceAsStream( path_2011_topicmap );
			instream_qrels = SessionTrack.class.getResourceAsStream( path_2011_qrels );
			sessions = TRECSession.loadSessionsTREC( instream_sessions, instream_topicmap, instream_qrels );
			
		} else if ( queryset.equalsIgnoreCase( "2012" ) ) {
			
			instream_sessions = SessionTrack.class.getResourceAsStream( path_2012_sessions );
			instream_topicmap = SessionTrack.class.getResourceAsStream( path_2012_topicmap );
			instream_qrels = SessionTrack.class.getResourceAsStream( path_2012_qrels );
			sessions = TRECSession.loadSessionsTREC2012( instream_sessions, instream_topicmap, instream_qrels );
			
		} else if ( queryset.equalsIgnoreCase( "2013" ) ) {
			
			instream_sessions = SessionTrack.class.getResourceAsStream( path_2013_sessions );
			instream_topicmap = SessionTrack.class.getResourceAsStream( path_2013_topicmap );
			instream_qrels = SessionTrack.class.getResourceAsStream( path_2013_qrels );
			sessions = TRECSession.loadSessionsTREC( instream_sessions, instream_topicmap, instream_qrels );
			
		} else if ( queryset.equalsIgnoreCase( "2014" ) ) {
			
			instream_sessions = SessionTrack.class.getResourceAsStream( path_2014_sessions );
			instream_topicmap = SessionTrack.class.getResourceAsStream( path_2014_topicmap );
			instream_qrels = SessionTrack.class.getResourceAsStream( path_2014_qrels );
			sessions = TRECSession.loadSessionsTREC( instream_sessions, instream_topicmap, instream_qrels );
			
		}
		
		if ( instream_sessions != null ) {
			instream_sessions.close();
		}
		if ( instream_topicmap != null ) {
			instream_topicmap.close();
		}
		if ( instream_qrels != null ) {
			instream_qrels.close();
		}
		return sessions;
		
	}
	
	public static Map<String, TRECSession> loadSessionsMap( String queryset ) throws IOException {
		Map<String, TRECSession> map = new TreeMap<String, TRECSession>();
		List<TRECSession> sessions = loadSessions( queryset );
		for ( TRECSession session : sessions ) {
			map.put( session.getSessionID(), session );
		}
		return map;
	}
	
}
