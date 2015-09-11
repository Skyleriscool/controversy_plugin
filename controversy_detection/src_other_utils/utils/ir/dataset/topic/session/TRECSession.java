package utils.ir.dataset.topic.session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import lemurproject.indri.DocumentVector;
import lemurproject.indri.QueryEnvironment;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.apache.lucene.analysis.Analyzer;
import org.apache.commons.lang3.StringEscapeUtils;

import utils.IOUtils;
import utils.StringUtils;
import utils.ir.eval.Qrels;
import utils.ir.indri.IndriUtils;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.analysis.AnalyzerUtils;

/**
 * <p>
 * SessionTopic stores information of a search session for TREC session search task (only support since TREC session track 2011). Note that the details of a
 * session's information is changing every year:
 * </p>
 * <ul>
 * <li>
 * In TREC 2010, only pseudo-session involving two search queries were provided. Relevance judgments for each query were provided. All the data (e.g.
 * pseudo-queries, relevance judgments) came from those in TREC web track. No real system and user studies were involved into the process of making sessions.</li>
 * <li>
 * In TREC 2011, user studies based on a real system were conducted to collect session information related to a topic. A session can be relevant to one topic
 * and some of the topic's subtopics. A session can involve arbitrary numbers of queries. Users' clicked results were collected. Relevance ju dgments one each
 * topic and its subtopics were provided.</li>
 * <li>
 * In TREC 2012, an experiment similar to that of TREC 2011 were used. However, relevance judgments were made only on each session's topic (no subtopic
 * information). But the search tasks (according to Nicholas Belkin's presentation in TREC 2012) are very different from those in 2011. Four task types were
 * defined and classified. Besides, sessions in TREC 2011 were mostly known-item search tasks.</li>
 * </ul>
 * <p>
 * TRECSessionTopic is designed to store and process data for TREC session track later than 2011. Session track 2010 data (the pseudo-session data) is not
 * supported since I assume it will be rarely used considering we have later years "real session" data.
 * </p>
 * <p>
 * Read <a href="http://ir.cis.udel.edu/sessions/guidelines.html">TREC session track guidelines</a> to know more details.
 * </p>
 * 
 * @author Jiepu Jiang
 * @version Feb 11, 2015
 */
public class TRECSession {
	
	protected String user_id;
	protected String session_id;
	protected TRECSessionTaskType task_type;
	protected List<TRECSessionQuery> queries;
	
	protected String topic;
	protected Set<String> subtopics;
	
	protected String title;
	protected String description;
	protected String narrative;
	
	protected Qrels qrels;
	protected double time_start;
	
	protected QueryEnvironment index;
	
	/**
	 * Load information from an Indri index.
	 * 
	 * @param index
	 */
	public void loadIndex( QueryEnvironment index ) {
		this.index = index;
		for ( TRECSessionQuery query : queries ) {
			for ( TRECResultSummary result : query.results ) {
				int[] docid = null;
				try {
					docid = index.documentIDsFromMetadata( "docno", new String[] { result.docno } );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				if ( docid != null && docid.length > 0 ) {
					try {
						result.docsample = IndriUtils.getDocSample( index, docid[0], false );
						result.doctokens_index = new ArrayList<String>();
						DocumentVector[] vector = index.documentVectors( docid );
						if ( vector != null && vector.length > 0 ) {
							String[] stems = vector[0].stems;
							int[] positions = vector[0].positions;
							for ( int ix = 0 ; ix < positions.length ; ix++ ) {
								result.doctokens_index.add( stems[positions[ix]] );
							}
						}
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static final Analyzer analyzer = TextAnalyzer.get( "alpha", "lc", "kstem", "nostop", "no oov" );
	
	/**
	 * Load document information from an external zipfile.
	 * 
	 * @param zipfile
	 */
	public void loadDocsFromZipFile( ZipFile zipfile ) {
		for ( TRECSessionQuery query : queries ) {
			for ( TRECResultSummary result : query.results ) {
				String docno = result.getDocno();
				try {
					ZipEntry entry = zipfile.getEntry( docno.toLowerCase() );
					if ( entry != null ) {
						
						InputStream instream = zipfile.getInputStream( entry );
						byte[] bytes = IOUtils.readBytes( instream );
						instream.close();
						
						result.doccontent = new String( bytes, "UTF-8" );
						result.doctokens = AnalyzerUtils.tokenize( result.doccontent, analyzer );
						
						result.dochtml = Jsoup.parse( result.doccontent );
						HtmlToPlainText formatter = new HtmlToPlainText();
						result.doccontent_notag = formatter.getPlainText( result.dochtml );
						result.doccontent_notag = result.doccontent_notag.replaceAll( "<[^>]+>", " " );
						
						result.doctokens_notag = AnalyzerUtils.tokenize( result.doccontent_notag, analyzer );
						
					}
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Load document information from an external zipfile.
	 * 
	 * @param zipfile
	 * @throws IOException
	 */
	public void loadOtherInformationFromZipFile( ZipFile zipfile ) throws IOException {
		Map<String, List<TRECResultSummary>> results = new TreeMap<>();
		for ( TRECSessionQuery query : queries ) {
			for ( TRECResultSummary result : query.results ) {
				String docno = result.getDocno().toLowerCase();
				if ( !results.containsKey( docno ) ) {
					results.put( docno, new ArrayList<TRECResultSummary>() );
				}
				results.get( docno ).add( result );
			}
		}
		loadSpamrank( results, zipfile );
		loadPagerank( results, zipfile );
		loadAnchorText( results, zipfile );
	}
	
	private static void loadAnchorText( Map<String, List<TRECResultSummary>> results, ZipFile zipf ) throws IOException {
		ZipEntry entry = zipf.getEntry( "anchor" );
		InputStream instream = zipf.getInputStream( entry );
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine();
		while ( line != null ) {
			String[] splits = line.split( "\t" );
			String docno = splits[0].trim().toLowerCase();
			if ( results.containsKey( docno ) ) {
				for ( TRECResultSummary result : results.get( docno ) ) {
					result.anchors = new ArrayList<String>();
					for ( int ix = 2 ; ix < splits.length ; ix++ ) {
						String anchor = splits[ix].replaceAll( "\\s+", " " ).trim();
						if ( anchor.length() > 0 ) {
							result.anchors.add( anchor );
						}
					}
				}
			}
			line = reader.readLine();
		}
		reader.close();
		instream.close();
	}
	
	private static void loadSpamrank( Map<String, List<TRECResultSummary>> results, ZipFile zipf ) throws IOException {
		ZipEntry entry = zipf.getEntry( "spamrank" );
		InputStream instream = zipf.getInputStream( entry );
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine();
		while ( line != null ) {
			String[] splits = line.split( "\t" );
			String docno = splits[0].trim().toLowerCase();
			if ( results.containsKey( docno ) ) {
				for ( TRECResultSummary result : results.get( docno ) ) {
					result.spamrank = Integer.parseInt( splits[1].trim() );
				}
			}
			line = reader.readLine();
		}
		reader.close();
		instream.close();
	}
	
	private static void loadPagerank( Map<String, List<TRECResultSummary>> results, ZipFile zipf ) throws IOException {
		ZipEntry entry = zipf.getEntry( "pagerank" );
		InputStream instream = zipf.getInputStream( entry );
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine();
		while ( line != null ) {
			String[] splits = line.split( "\t" );
			String docno = splits[0].trim().toLowerCase();
			if ( results.containsKey( docno ) ) {
				for ( TRECResultSummary result : results.get( docno ) ) {
					result.pagerank = Double.parseDouble( splits[1].trim() );
				}
			}
			line = reader.readLine();
		}
		reader.close();
		instream.close();
	}
	
	public static void loadOtherInformationFromZipFile( List<TRECSession> sessions, ZipFile zipfile ) throws IOException {
		Map<String, List<TRECResultSummary>> results = new TreeMap<>();
		for ( TRECSession session : sessions ) {
			for ( TRECSessionQuery query : session.getQueries() ) {
				for ( TRECResultSummary result : query.results ) {
					String docno = result.getDocno().toLowerCase();
					if ( !results.containsKey( docno ) ) {
						results.put( docno, new ArrayList<TRECResultSummary>() );
					}
					results.get( docno ).add( result );
				}
			}
		}
		loadSpamrank( results, zipfile );
		loadPagerank( results, zipfile );
		loadAnchorText( results, zipfile );
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "session_id = " + session_id + ", #queries = " + queries.size() + ", task_type = " + task_type + ", user_id = " + user_id + ", time_start = " + time_start + "\n" );
		sb.append( "topic = " + topic + ", title: " + title + "\n" );
		sb.append( "description = " + description + "\n" );
		sb.append( "narrative = " + narrative + "\n" );
		for ( TRECSessionQuery query : queries ) {
			sb.append( query.toString() );
		}
		return sb.toString();
	}
	
	private TRECSession() {
		this.queries = new ArrayList<TRECSessionQuery>();
		this.subtopics = new TreeSet<String>();
	}
	
	public String getUserID() {
		return this.user_id;
	}
	
	public String getSessionID() {
		return this.session_id;
	}
	
	public TRECSessionTaskType getTaskType() {
		return this.task_type;
	}
	
	public TRECSessionTaskType.Product getTaskProduct() {
		return TRECSessionTaskType.getProduct( this.task_type );
	}
	
	public TRECSessionTaskType.Goal getTaskGoal() {
		return TRECSessionTaskType.getGoal( this.task_type );
	}
	
	public List<TRECSessionQuery> getQueries() {
		return this.queries;
	}
	
	public TRECSessionQuery getQuery( int ix ) {
		return this.queries.get( ix );
	}
	
	public TRECSessionQuery getCurrentQuery() {
		return this.queries.get( this.queries.size() - 1 );
	}
	
	public int numPastQueries() {
		return this.queries.size() - 1;
	}
	
	public String getTopic() {
		return this.topic;
	}
	
	public Set<String> getSubTopics() {
		return this.subtopics;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getNarrative() {
		return this.narrative;
	}
	
	public double getTimeStart() {
		return this.time_start;
	}
	
	public Qrels getQrels() {
		return (Qrels) this.qrels;
	}
	
	/**
	 * Get the docno of the queries' results except for the last query.
	 */
	public String[][] getQueryResultsDocno() {
		return getQueryResultsDocno( 0, queries.size() - 1 );
	}
	
	/**
	 * Get the docno of the queries' results from 0 (inclusive) to ix_ed (exclusive).
	 */
	public String[][] getQueryResultsDocno( int ix_ed ) {
		return getQueryResultsDocno( 0, ix_ed );
	}
	
	/**
	 * Get the docno of the queries' results from ix_bg (inclusive) to ix_ed (exclusive).
	 */
	public String[][] getQueryResultsDocno( int ix_bg, int ix_ed ) {
		String[][] results = new String[ix_ed - ix_bg][];
		for ( int i = 0 ; i < ix_ed - ix_bg ; i++ ) {
			TRECSessionQuery query = queries.get( i + ix_bg );
			results[i] = new String[query.results.size()];
			for ( int j = 0 ; j < results[i].length ; j++ ) {
				results[i][j] = query.getResults().get( j ).getDocno();
			}
		}
		return results;
	}
	
	/**
	 * Get the URL of the queries' results except for the last query.
	 */
	public String[][] getQueryResultsURL() {
		return getQueryResultsURL( 0, queries.size() - 1 );
	}
	
	/**
	 * Get the URL of the queries' results from 0 (inclusive) to ix_ed (exclusive).
	 */
	public String[][] getQueryResultsURL( int ix_ed ) {
		return getQueryResultsURL( 0, ix_ed );
	}
	
	/**
	 * Get the URL of the queries' results from ix_bg (inclusive) to ix_ed (exclusive).
	 */
	public String[][] getQueryResultsURL( int ix_bg, int ix_ed ) {
		String[][] results = new String[ix_ed - ix_bg][];
		for ( int i = 0 ; i < ix_ed - ix_bg ; i++ ) {
			TRECSessionQuery query = queries.get( i + ix_bg );
			results[i] = new String[query.results.size()];
			for ( int j = 0 ; j < results[i].length ; j++ ) {
				results[i][j] = query.getResults().get( j ).getURL();
			}
		}
		return results;
	}
	
	public QueryEnvironment getIndex() {
		return this.index;
	}
	
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( String path_sessions, String path_topicmapping, String path_qrels ) throws IOException {
		return loadSessionsTREC2011( path_sessions, path_topicmapping, path_qrels, true );
	}
	
	/**
	 * Load TREC session track 2011 dataset.
	 * 
	 * @param path_sessions
	 * @param path_topicmapping
	 * @param path_qrels
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( String path_sessions, String path_topicmapping, String path_qrels, boolean alltopics ) throws IOException {
		return loadSessionsTREC2011( new File( path_sessions ), new File( path_topicmapping ), new File( path_qrels ), alltopics );
	}
	
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( File file_sessions, File file_topicmapping, File file_qrels ) throws IOException {
		return loadSessionsTREC2011( file_sessions, file_topicmapping, file_qrels, true );
	}
	
	/**
	 * Load TREC session track 2011 dataset.
	 * 
	 * @param file_sessions
	 * @param file_topicmapping
	 * @param file_qrels
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( File file_sessions, File file_topicmapping, File file_qrels, boolean alltopics ) throws IOException {
		InputStream instream_sessions = new FileInputStream( file_sessions );
		InputStream instream_qrels = ( file_qrels != null && file_qrels.exists() ) ? new FileInputStream( file_qrels ) : null;
		InputStream instream_topicmapping = ( file_topicmapping != null && file_topicmapping.exists() ) ? new FileInputStream( file_topicmapping ) : null;
		List<TRECSession> sessions = loadSessionsTREC2011( instream_sessions, instream_topicmapping, instream_qrels, alltopics );
		instream_sessions.close();
		if ( instream_topicmapping != null ) {
			instream_topicmapping.close();
		}
		if ( instream_qrels != null ) {
			instream_qrels.close();
		}
		return sessions;
	}
	
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( InputStream instream_sessions, InputStream instream_topicmapping, InputStream instream_qrels ) throws IOException {
		return loadSessionsTREC2011( instream_sessions, instream_topicmapping, instream_qrels, true );
	}
	
	/**
	 * Load TREC session track 2011 dataset.
	 * 
	 * @param instream_sessions
	 * @param instream_topicmapping
	 * @param instream_qrels
	 * @param alltopics
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static List<TRECSession> loadSessionsTREC2011( InputStream instream_sessions, InputStream instream_topicmapping, InputStream instream_qrels, boolean alltopics ) throws IOException {
		List<TRECSession> sessions = loadSessions( instream_sessions );
		Map<String, TRECSession> sessionid_sessions = new TreeMap<>();
		for ( TRECSession session : sessions ) {
			sessionid_sessions.put( session.session_id, session );
		}
		if ( instream_qrels != null ) {
			// The instream_qrels is actually for diversity evaluation.
			// Here, load it as the normal qrels will use the maximum
			// relevance score of subtopics as the topic's relevance score.
			Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream_qrels );
			if ( instream_topicmapping != null ) {
				Map<String, String> mapping = new TreeMap<>();
				BufferedReader reader = IOUtils.getBufferedReader( instream_topicmapping );
				String line = reader.readLine();
				while ( line != null ) {
					String[] splits = line.split( "\\s+" );
					String sessionid = splits[0].trim();
					String topicid = splits[1].trim();
					// String subtopicid = splits[2].trim();
					mapping.put( sessionid, topicid );
					line = reader.readLine();
				}
				reader.close();
				for ( TRECSession session : sessions ) {
					session.qrels = topic_qrels.get( mapping.get( session.session_id ) );
				}
			}
		}
		return sessions;
	}
	
	/**
	 * Load TREC session track 2012 dataset.
	 * 
	 * @param path_sessions
	 * @param path_topicmapping
	 * @param path_qrels
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC2012( String path_sessions, String path_topicmapping, String path_qrels ) throws IOException {
		return loadSessionsTREC2012( new File( path_sessions ), new File( path_topicmapping ), new File( path_qrels ) );
	}
	
	/**
	 * Load TREC session track 2012 dataset.
	 * 
	 * @param file_sessions
	 * @param file_topicmapping
	 * @param file_qrels
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC2012( File file_sessions, File file_topicmapping, File file_qrels ) throws IOException {
		InputStream instream_sessions = new FileInputStream( file_sessions );
		InputStream instream_qrels = ( file_qrels != null && file_qrels.exists() ) ? new FileInputStream( file_qrels ) : null;
		InputStream instream_topicmapping = ( file_topicmapping != null && file_topicmapping.exists() ) ? new FileInputStream( file_topicmapping ) : null;
		List<TRECSession> sessions = loadSessionsTREC2012( instream_sessions, instream_topicmapping, instream_qrels );
		instream_sessions.close();
		if ( instream_topicmapping != null ) {
			instream_topicmapping.close();
		}
		if ( instream_qrels != null ) {
			instream_qrels.close();
		}
		return sessions;
	}
	
	/**
	 * Load TREC session track 2012 dataset.
	 * 
	 * @param instream_sessions
	 * @param instream_topicmapping
	 * @param instream_qrels
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC2012( InputStream instream_sessions, InputStream instream_topicmapping, InputStream instream_qrels ) throws IOException {
		List<TRECSession> sessions = loadSessions( instream_sessions );
		Map<String, TRECSession> sessionid_sessions = new TreeMap<>();
		for ( TRECSession session : sessions ) {
			sessionid_sessions.put( session.session_id, session );
		}
		if ( instream_qrels != null ) {
			Map<String, Double> mapping_label_relevance = new TreeMap<String, Double>();
			// mapping between the original qrels labels and relevance scores
			mapping_label_relevance.put( "-2", 0.0 );
			mapping_label_relevance.put( "0", 0.0 );
			mapping_label_relevance.put( "1", 1.0 );
			mapping_label_relevance.put( "4", 2.0 );
			mapping_label_relevance.put( "2", 3.0 );
			mapping_label_relevance.put( "3", 4.0 );
			Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream_qrels, mapping_label_relevance );
			if ( instream_topicmapping != null ) {
				BufferedReader reader = IOUtils.getBufferedReader( instream_topicmapping );
				reader.readLine();
				String line = reader.readLine();
				while ( line != null ) {
					String[] splits = line.split( "\t" );
					String session_id = splits[0];
					String topic_id = splits[1];
					String task_type = splits[5];
					TRECSession session = sessionid_sessions.get( session_id );
					session.topic = topic_id;
					session.qrels = topic_qrels.get( topic_id );
					session.task_type = TRECSessionTaskType.parse( task_type );
					line = reader.readLine();
				}
				reader.close();
			}
		}
		return sessions;
	}
	
	/**
	 * Load TREC session track data. This is applicable to session track 2011, 2013 and 2014 data.
	 * 
	 * @param path_sessions
	 * @param path_topicmapping
	 * @param path_qrels
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC( String path_sessions, String path_topicmapping, String path_qrels ) throws IOException {
		return loadSessionsTREC( new File( path_sessions ), new File( path_topicmapping ), new File( path_qrels ) );
	}
	
	/**
	 * Load TREC session track data. This is applicable to session track 2011, 2013 and 2014 data.
	 * 
	 * @param file_sessions
	 * @param file_topicmapping
	 * @param file_qrels
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC( File file_sessions, File file_topicmapping, File file_qrels ) throws IOException {
		InputStream instream_sessions = new FileInputStream( file_sessions );
		InputStream instream_qrels = ( file_qrels != null && file_qrels.exists() ) ? new FileInputStream( file_qrels ) : null;
		InputStream instream_topicmapping = ( file_topicmapping != null && file_topicmapping.exists() ) ? new FileInputStream( file_topicmapping ) : null;
		List<TRECSession> sessions = loadSessionsTREC( instream_sessions, instream_topicmapping, instream_qrels );
		instream_sessions.close();
		if ( instream_qrels != null ) {
			instream_qrels.close();
		}
		if ( instream_topicmapping != null ) {
			instream_topicmapping.close();
		}
		return sessions;
	}
	
	/**
	 * Load TREC session track data. This is applicable to session track 2011, 2013 and 2014 data.
	 * 
	 * @param instream_sessions
	 * @param instream_topicmapping
	 *            Optional and can be null
	 * @param instream_qrels
	 *            Optional and can be null
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessionsTREC( InputStream instream_sessions, InputStream instream_topicmapping, InputStream instream_qrels ) throws IOException {
		List<TRECSession> sessions = loadSessions( instream_sessions );
		Map<String, TRECSession> sessionid_sessions = new TreeMap<>();
		for ( TRECSession session : sessions ) {
			sessionid_sessions.put( session.session_id, session );
		}
		if ( instream_qrels != null ) {
			Map<String, Qrels> topic_qrels = Qrels.loadQrels( instream_qrels );
			if ( instream_topicmapping != null ) {
				BufferedReader reader = IOUtils.getBufferedReader( instream_topicmapping );
				reader.readLine();
				String line = reader.readLine();
				while ( line != null ) {
					String[] splits = line.split( "\t" );
					String session_id = splits[0];
					String topic_id = splits[1];
					String task_type = splits.length == 6 ? splits[5] : null;
					TRECSession session = sessionid_sessions.get( session_id );
					session.topic = topic_id;
					session.qrels = topic_qrels.get( topic_id );
					session.task_type = task_type == null ? null : TRECSessionTaskType.parse( task_type );
					line = reader.readLine();
				}
				reader.close();
			}
		}
		return sessions;
	}
	
	/**
	 * Load session information from TREC session log file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessions( String path ) throws IOException {
		return loadSessions( new File( path ) );
	}
	
	/**
	 * Load session information from TREC session log file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessions( File file ) throws IOException {
		InputStream instream = new FileInputStream( file );
		List<TRECSession> sessions = loadSessions( instream );
		instream.close();
		return sessions;
	}
	
	/**
	 * Load session information from TREC session log file.
	 * 
	 * @param instream
	 * @param max_sessionid
	 * @return
	 * @throws IOException
	 */
	public static List<TRECSession> loadSessions( InputStream instream ) throws IOException {
		List<TRECSession> sessions = new ArrayList<TRECSession>();
		String content = IOUtils.readContent( instream, "UTF-8" );
		List<String[]> matches = StringUtils.extract( content, "(<session num=.+?>)(.+?)</session>", 1, 2 );
		for ( String[] match : matches ) {
			TRECSession session = new TRECSession();
			session.topic = StringUtils.extractFirst( match[1], "<topic num=\"(\\d+)\">", 1 );
			session.parseSessionTag( match[0] );
			session.parseTopicTag( match[1] );
			List<String> query_matches = StringUtils.extract( match[1], "<interaction.+?>.+?</interaction>" );
			for ( String query_match : query_matches ) {
				TRECSessionQuery query = session.parseInteractionTag( query_match );
				query.qix = session.queries.size();
				session.queries.add( query );
			}
			TRECSessionQuery cq = session.parseCurrentQuery( match[1] );
			if ( cq != null ) {
				cq.qix = session.queries.size();
				session.queries.add( cq );
			}
			sessions.add( session );
		}
		return sessions;
	}
	
	private TRECSessionQuery parseCurrentQuery( String text ) throws IOException {
		TRECSessionQuery query = new TRECSessionQuery();
		String[] matches = StringUtils.extractFirst( text, "<currentquery starttime=\"(.+?)\">.+?<query>(.+?)</query>.+?</currentquery>", 1, 2 );
		if ( matches == null ) {
			return null;
		}
		query.session = this;
		query.time_start = parseTime( matches[0] );
		query.query = matches[1];
		query.terms_unstemmed = AnalyzerUtils.tokenize( query.query, analyzer_unstemmed );
		return query;
	}
	
	private static Analyzer analyzer_unstemmed = TextAnalyzer.get( "alpha", "lc", "nostem", "indri stop", "no oov" );
	
	private TRECSessionQuery parseInteractionTag( String text ) throws IOException {
		TRECSessionQuery query = new TRECSessionQuery();
		query.session = this;
		query.query = StringUtils.extractFirst( text, "<query>(.+?)</query>", 1 );
		if ( query.query != null ) {
			query.query = query.query.trim();
			query.terms_unstemmed = AnalyzerUtils.tokenize( query.query, analyzer_unstemmed );
		}
		// query start time
		String strqstarttime = StringUtils.extractFirst( text, "<interaction[^>]+?starttime=\"([^>\"]+)\"[^>]+?>", 1 );
		if ( strqstarttime != null ) {
			query.time_start = Double.parseDouble( strqstarttime );
		}
		// results
		List<String[]> matches = StringUtils.extract( text, "<result rank=\"(.*?)\">.+?<url>(.*?)</url>.+?<clueweb\\d\\did>(.*?)</clueweb\\d\\did>.+?<title>(.*?)</title>.+?<snippet>(.*?)</snippet>.+?</result>", 1, 2, 3, 4, 5 );
		Map<Integer, TRECResultSummary> rank_results = new TreeMap<Integer, TRECResultSummary>();
		for ( String[] match : matches ) {
			TRECResultSummary result = new TRECResultSummary();
			result.rank = Integer.parseInt( match[0] );
			result.position = query.results.size();
			result.url = match[1];
			result.docno = match[2];
			result.title = match[3];
			result.snippet = match[4];
			if ( result.title != null ) {
				// the data has been encoded twice
				result.title = StringEscapeUtils.unescapeXml( result.title );
				result.title = StringEscapeUtils.unescapeXml( result.title );
			}
			if ( result.snippet != null ) {
				// the data has been encoded twice
				result.snippet = StringEscapeUtils.unescapeXml( result.snippet );
				result.snippet = StringEscapeUtils.unescapeXml( result.snippet );
			}
			query.results.add( result );
			query.url_results.put( result.url.trim().toLowerCase(), result );
			query.docno_results.put( result.docno.trim().toLowerCase(), result );
			rank_results.put( result.rank, result );
		}
		// clicks
		matches = StringUtils.extract( text, "<click num=\"(.+?)\" starttime=\"(.+?)\" endtime=\"(.+?)\">.+?<rank>(.+?)</rank>.+?</click>", 1, 2, 3, 4 );
		for ( String[] match : matches ) {
			TRECResultClick click = new TRECResultClick();
			click.time_bg = parseTime( match[1] );
			click.time_ed = parseTime( match[2] );
			click.result = rank_results.get( Integer.parseInt( match[3].trim() ) );
			click.result.clicked = true;
			query.clicks.add( click );
			click.result.click = click;
		}
		return query;
	}
	
	private void parseTopicTag( String text ) {
		this.description = StringUtils.extractFirst( text, "<desc>(.+?)</desc>", 1 );
		if ( this.description != null ) {
			this.description = this.description.trim();
		}
		this.narrative = StringUtils.extractFirst( text, "<narr>(.+?)</narr>", 1 );
		if ( this.narrative != null ) {
			this.narrative = this.narrative.trim();
		}
	}
	
	private void parseSessionTag( String text ) {
		this.user_id = StringUtils.extractFirst( text, "userid=\"(.+?)\"", 1 );
		if ( this.user_id != null ) {
			this.user_id = this.user_id.trim();
		}
		this.session_id = StringUtils.extractFirst( text, "num=\"(.+?)\"", 1 );
		if ( this.session_id != null ) {
			this.session_id = this.session_id.trim();
		}
		String starttime = StringUtils.extractFirst( text, "starttime=\"(.+?)\"", 1 );
		this.time_start = parseTime( starttime );
	}
	
	private static double parseTime( String starttime ) {
		if ( starttime.contains( ":" ) ) {
			String[] splits = starttime.split( ":" );
			double time = Double.parseDouble( splits[2] ); // second
			time = time + 60 * Double.parseDouble( splits[1] ); // minute
			time = time + 60 * 60 * Double.parseDouble( splits[1] ); // hour
			return time;
		} else {
			double time = -1;
			try {
				time = Double.parseDouble( starttime );
			} catch ( Exception e ) {
			}
			return time;
		}
	}
	
}
