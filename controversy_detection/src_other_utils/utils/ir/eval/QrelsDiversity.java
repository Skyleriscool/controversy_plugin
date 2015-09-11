package utils.ir.eval;

import java.io.*;
import java.util.*;

import utils.*;

/**
 * QrelsDiversity stores TREC style diversified search topic qrels. Each topic will be diversified into several
 * subtopics. Documents will be assessed for its relevance with each of the subtopic. When using the diversity qrels as
 * adhoc qrels, a document's score will be the maximum score of the document among each of the subtopic.
 * 
 * @author Jiepu Jiang
 * @version Mar 2, 2013
 */
public class QrelsDiversity implements QrelsInfo {
	
	/** Topic id (optional). */
	protected String topicid;
	
	/** Key is subtopic id; value is the subtopic's qrels. */
	protected Map<String, Qrels> subtopic_qrels;
	
	/**
	 * Constructor.
	 */
	public QrelsDiversity( String topicid ) {
		this.topicid = topicid;
		this.subtopic_qrels = new TreeMap<String, Qrels>();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param subtopic_qrels
	 */
	public QrelsDiversity( String topicid, Map<String, Qrels> subtopic_qrels ) {
		this.topicid = topicid;
		this.subtopic_qrels = subtopic_qrels;
	}
	
	/**
	 * @return
	 */
	public String topicid() {
		return this.topicid;
	}
	
	/**
	 * @param topicid
	 * @return
	 */
	public QrelsDiversity setTopicid( String topicid ) {
		this.topicid = topicid;
		return this;
	}
	
	/**
	 * Get the subtopic's qrels.
	 * 
	 * @param subtopic
	 * @return
	 */
	public Qrels getQrels( String subtopic ) {
		return subtopic_qrels.get( subtopic );
	}
	
	/**
	 * Set a subtopic's qrels.
	 * 
	 * @param subtopic
	 * @param qrels
	 */
	public void putQrels( String subtopic, Qrels qrels ) {
		subtopic_qrels.put( subtopic, qrels );
	}
	
	/**
	 * @return The number of subtopics for this topic.
	 */
	public int numSubtopics() {
		return subtopic_qrels.size();
	}
	
	/**
	 * @return A set of the subtopics for this topic.
	 */
	public Set<String> subtopics() {
		return subtopic_qrels.keySet();
	}
	
	/**
	 * Get the document's relevance score for the specified subtopic. The score of unjudged document will be 0.
	 * 
	 * @param subtopic
	 * @param docno
	 * @return
	 */
	public double relevance( String subtopic, String docno ) {
		return subtopic_qrels.get( subtopic ).relevance( docno );
	}
	
	/**
	 * Whether the document has been judged for relevance to the subtopic.
	 * 
	 * @param subtopic
	 * @param docno
	 * @return
	 */
	public boolean hasBeenJudged( String subtopic, String docno ) {
		return subtopic_qrels.get( subtopic ).hasBeenJudged( docno );
	}
	
	public double relevance( String docno ) {
		Double score = null;
		for ( String sub : subtopic_qrels.keySet() ) {
			Qrels qrel = subtopic_qrels.get( sub );
			if ( score == null ) {
				score = qrel.relevance( docno );
			} else {
				double sc = qrel.relevance( docno );
				if ( sc > score ) {
					score = sc;
				}
			}
		}
		return score == null ? 0 : score;
	}
	
	public Collection<String> relevantDocuments() {
		Set<String> docs = new TreeSet<String>();
		for ( String sub : subtopic_qrels.keySet() ) {
			Qrels qrel = subtopic_qrels.get( sub );
			docs.addAll( qrel.rels.keySet() );
		}
		return docs;
	}
	
	/**
	 * Parse the diversity qrels stored in a file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Map<String, QrelsDiversity> parse( String path ) throws IOException {
		return parse( new File( path ) );
	}
	
	/**
	 * Parse the diversity qrels stored in a file.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Map<String, QrelsDiversity> parse( File f ) throws IOException {
		InputStream instream = new FileInputStream( f );
		Map<String, QrelsDiversity> qrels = parse( instream );
		instream.close();
		return qrels;
	}
	
	/**
	 * Parse the diversity qrels stored in a file.
	 * 
	 * @param instream
	 * @return
	 * @throws IOException
	 */
	public static Map<String, QrelsDiversity> parse( InputStream instream ) throws IOException {
		
		Set<String> topics = new TreeSet<String>();
		Map<String, Set<String>> subtopics = new TreeMap<String, Set<String>>();
		Map<String, Map<String, Map<String, Double>>> topic_sub_rels = new TreeMap<String, Map<String, Map<String, Double>>>();
		Map<String, Map<String, Map<String, Double>>> topic_sub_nrels = new TreeMap<String, Map<String, Map<String, Double>>>();
		
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine();
		while ( line != null ) {
			String[] matches = StringUtils.extractFirst( line, "([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)", 1, 2, 3, 4 );
			String topic = matches[0].trim();
			String subtopic = matches[1].trim();
			String docno = matches[2].trim();
			double score = Double.parseDouble( matches[3] );
			topics.add( topic );
			if ( !subtopics.containsKey( topic ) ) {
				subtopics.put( topic, new TreeSet<String>() );
			}
			subtopics.get( topic ).add( subtopic );
			Map<String, Map<String, Map<String, Double>>> target = score > 0 ? topic_sub_rels : topic_sub_nrels;
			if ( !target.containsKey( topic ) ) {
				target.put( topic, new TreeMap<String, Map<String, Double>>() );
			}
			if ( !target.get( topic ).containsKey( subtopic ) ) {
				target.get( topic ).put( subtopic, new TreeMap<String, Double>() );
			}
			target.get( topic ).get( subtopic ).put( docno, score );
			line = reader.readLine();
		}
		reader.close();
		
		Map<String, QrelsDiversity> qrels = new TreeMap<String, QrelsDiversity>();
		for ( String topic : topics ) {
			QrelsDiversity all_qrels = new QrelsDiversity( topic );
			for ( String subtopic : subtopics.get( topic ) ) {
				Map<String, Double> rels = topic_sub_rels.get( topic ).get( subtopic );
				Map<String, Double> nrels = topic_sub_nrels.get( topic ).get( subtopic );
				if ( rels == null ) {
					rels = new TreeMap<String, Double>();
				}
				if ( nrels == null ) {
					nrels = new TreeMap<String, Double>();
				}
				Qrels sub_qrels = new Qrels( rels, nrels );
				all_qrels.subtopic_qrels.put( subtopic, sub_qrels );
			}
			qrels.put( topic, all_qrels );
		}
		return qrels;
		
	}
	
}
