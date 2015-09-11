package utils.ir.dataset.topic;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;

import utils.ir.eval.Qrels;

/**
 * Utilities of accessing data for TREC ad hoc track.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class AdhocTrack {
	
	public static void main( String[] args ) {
		try {
			
			loadTopics( TREC1 );
			loadTopics( TREC2 );
			loadTopics( TREC3 );
			loadTopics( TREC4 );
			loadTopics( TREC5 );
			loadTopics( TREC6 );
			loadTopics( TREC7 );
			loadTopics( TREC8 );
			
			loadQrels( TREC1 );
			loadQrels( TREC2 );
			loadQrels( TREC3 );
			loadQrels( TREC4 );
			loadQrels( TREC5 );
			loadQrels( TREC6 );
			loadQrels( TREC7 );
			loadQrels( TREC8 );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static final String path_topics = "data/adhoc/adhoc_topics";
	private static final String path_qrels = "data/adhoc/adhoc_qrels";
	
	private static final Map<String, int[]> topics_range = new TreeMap<String, int[]>();
	
	public static final String TREC1 = "trec1";
	public static final String TREC2 = "trec2";
	public static final String TREC3 = "trec3";
	public static final String TREC4 = "trec4";
	public static final String TREC5 = "trec5";
	public static final String TREC6 = "trec6";
	public static final String TREC7 = "trec7";
	public static final String TREC8 = "trec8";
	
	static {
		topics_range.put( TREC1, new int[] { 51, 100 } );
		topics_range.put( TREC2, new int[] { 101, 150 } );
		topics_range.put( TREC3, new int[] { 151, 200 } );
		topics_range.put( TREC4, new int[] { 201, 250 } );
		topics_range.put( TREC5, new int[] { 251, 300 } );
		topics_range.put( TREC6, new int[] { 301, 350 } );
		topics_range.put( TREC7, new int[] { 351, 400 } );
		topics_range.put( TREC8, new int[] { 401, 450 } );
	}
	
	/**
	 * Load all TREC ad hoc track topics (TREC 1-8, No. 51-450).
	 * 
	 * @return
	 * @throws IOException
	 */
	public static List<TRECTopic> loadAllTopics() throws IOException {
		InputStream instream = new AdhocTrack().getClass().getResourceAsStream( path_topics );
		List<TRECTopic> topics = TRECTopic.parseTrecAdhoc( instream );
		instream.close();
		return topics;
	}
	
	/**
	 * Load all TREC ad hoc track topics (TREC 1-8, No. 51-450).
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, TRECTopic> loadAllTopicsMap() throws IOException {
		List<TRECTopic> topics = loadAllTopics();
		Map<Integer, TRECTopic> map = new TreeMap<Integer, TRECTopic>();
		for ( TRECTopic topic : topics ) {
			map.put( Integer.parseInt( topic.get( "id" ) ), topic );
		}
		return map;
	}
	
	/**
	 * Load TREC ad hoc track topics for a specific year.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static List<TRECTopic> loadTopics( String queryset ) throws IOException {
		List<TRECTopic> topics = loadAllTopics();
		int[] range = topics_range.get( queryset );
		List<TRECTopic> selected_topics = new ArrayList<TRECTopic>();
		for ( TRECTopic topic : topics ) {
			int topicid = Integer.parseInt( topic.get( "id" ) );
			if ( topicid >= range[0] && topicid <= range[1] ) {
				selected_topics.add( topic );
			}
		}
		return selected_topics;
	}
	
	/**
	 * Load TREC ad hoc track topics for a specific year.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, TRECTopic> loadTopicsMap( String queryset ) throws IOException {
		List<TRECTopic> topics = loadAllTopics();
		int[] range = topics_range.get( queryset );
		Map<Integer, TRECTopic> map = new TreeMap<Integer, TRECTopic>();
		for ( TRECTopic topic : topics ) {
			int topicid = Integer.parseInt( topic.get( "id" ) );
			if ( topicid >= range[0] && topicid <= range[1] ) {
				map.put( topicid, topic );
			}
		}
		return map;
	}
	
	/**
	 * Load qrels for all TREC ad hoc track topics (TREC 1-8, No. 51-450).
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Qrels> loadAllQrels() throws IOException {
		InputStream instream = new AdhocTrack().getClass().getResourceAsStream( path_qrels );
		Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream );
		instream.close();
		Map<Integer, Qrels> data = new TreeMap<Integer, Qrels>();
		for ( String id : topic_qrels.keySet() ) {
			data.put( Integer.parseInt( id ), topic_qrels.get( id ) );
		}
		return data;
	}
	
	/**
	 * Load qrels for all TREC ad hoc track topics (TREC 1-8, No. 51-450).
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Qrels> loadQrels( String queryset ) throws IOException {
		InputStream instream = new AdhocTrack().getClass().getResourceAsStream( path_qrels );
		Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream );
		instream.close();
		int[] range = topics_range.get( queryset );
		Map<Integer, Qrels> data = new TreeMap<Integer, Qrels>();
		for ( String id : topic_qrels.keySet() ) {
			int topicid = Integer.parseInt( id );
			if ( topicid >= range[0] && topicid <= range[1] ) {
				data.put( topicid, topic_qrels.get( id ) );
			}
		}
		return data;
	}
	
}
