package utils.ir.dataset.topic;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;

import utils.ir.eval.Qrels;

/**
 * Utilities of accessing data for TREC robust track.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class RobustTrack {
	
	public static void main( String[] args ) {
		try {
			
			System.out.println( loadTopics( Robust04 ).size() );
			System.out.println( loadQrels( Robust04 ).size() );
			
			System.out.println( loadTopics( Robust05 ).size() );
			System.out.println( loadQrels( Robust05 ).size() );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static final Map<String, String[]> dataset_files = new TreeMap<String, String[]>();
	
	public static final String Robust04 = "robust04";
	public static final String Robust05 = "robust05";
	
	static {
		dataset_files.put( Robust04, new String[] { "data/robust/robust04_topics", "data/robust/robust04_qrels" } );
		dataset_files.put( Robust05, new String[] { "data/robust/robust05_topics", "data/robust/robust05_qrels" } );
	}
	
	/**
	 * Load a specific year's TREC robust topics.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static List<TRECTopic> loadTopics( String queryset ) throws IOException {
		InputStream instream = new RobustTrack().getClass().getResourceAsStream( dataset_files.get( queryset )[0] );
		List<TRECTopic> topics = TRECTopic.parseTrecAdhoc( instream );
		instream.close();
		return topics;
	}
	
	/**
	 * Load a specific year's TREC robust topics.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, TRECTopic> loadTopicsMap( String queryset ) throws IOException {
		List<TRECTopic> topics = loadTopics( queryset );
		Map<Integer, TRECTopic> map = new TreeMap<Integer, TRECTopic>();
		for ( TRECTopic topic : topics ) {
			int topicid = Integer.parseInt( topic.get( "id" ) );
			map.put( topicid, topic );
		}
		return map;
	}
	
	/**
	 * Load qrels for a specific year's TREC robust topics.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<Integer, Qrels> loadQrels( String queryset ) throws IOException {
		InputStream instream = new RobustTrack().getClass().getResourceAsStream( dataset_files.get( queryset )[1] );
		Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream );
		instream.close();
		Map<Integer, Qrels> data = new TreeMap<Integer, Qrels>();
		for ( String id : topic_qrels.keySet() ) {
			int topicid = Integer.parseInt( id );
			data.put( topicid, topic_qrels.get( id ) );
		}
		return data;
	}
	
}
