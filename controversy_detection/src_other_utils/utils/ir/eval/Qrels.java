package utils.ir.eval;

import java.io.*;
import java.util.*;

import utils.*;

/**
 * <p>
 * Qrels provides utilities for loading and processing TREC-style qrels. A topic's "qrels" stores users' relevance judgments results. In general, the assessment
 * status of a document can be either: relevant, non-relevant, or unjudged. Here we assume the relevance judgments scores mean:
 * </p>
 * <ul>
 * <li>score > 0 means the document is relevant;</li>
 * <li>score <= 0 means the document is non-relevant;</li>
 * <li>if you cannot find a document's relevance score, it means that the document has not been judged (in most of the evaluation metrics, this documet is
 * considered as non-relevant).</li>
 * </ul>
 * 
 * @author Jiepu Jiang
 * @version Mar 2, 2013
 */
public class Qrels implements QrelsInfo {
	
	/** Topic id (optional). */
	protected String topicid;
	
	/** Judged relevant documents and the relevance scores. */
	protected Map<String, Double> rels;
	
	/**
	 * Judged non-relevant documents and the relevance scores (note that sometimes non-relevant documents will have different scores, e.g. "-1" for spams).
	 */
	protected Map<String, Double> nrels;
	
	/**
	 * Constructor. Initiate an empty qrels.
	 */
	public Qrels() {
		this.rels = new TreeMap<String, Double>();
		this.nrels = new TreeMap<String, Double>();
	}
	
	/**
	 * Constructor. Initiate an empty qrels.
	 * 
	 * @param topicid
	 */
	public Qrels( String topicid ) {
		this();
		this.topicid = topicid;
	}
	
	/**
	 * Constructor. Document-relevance scores stored in the "scores" map will be automatically classified into rels and nrels (score>0 will be considered as
	 * "relevant" and score<=0 as "non-relevant").
	 * 
	 * @param scores
	 */
	public Qrels( Map<String, Double> scores ) {
		this( (String) null, scores );
	}
	
	/**
	 * Constructor. Document-relevance scores stored in the "scores" map will be automatically classified into rels and nrels (score>0 will be considered as
	 * "relevant" and score<=0 as "non-relevant").
	 * 
	 * @param topicid
	 * @param scores
	 */
	public Qrels( String topicid, Map<String, Double> scores ) {
		this( topicid );
		for ( String doc : scores.keySet() ) {
			if ( scores.get( doc ) > 0 ) {
				rels.put( doc, scores.get( doc ) );
			} else {
				nrels.put( doc, scores.get( doc ) );
			}
		}
	}
	
	/**
	 * Constructor by directly specifying rels and nrels. Note that this is the way to construct a qrel object if score>0 is not to be used as the decision
	 * rules for "relevant" and "non-relevant".
	 * 
	 * @param rels
	 * @param nrels
	 */
	public Qrels( Map<String, Double> rels, Map<String, Double> nrels ) {
		this( (String) null, rels, nrels );
	}
	
	/**
	 * Constructor by directly specifying rels and nrels. Note that this is the way to construct a qrel object if score>0 is not to be used as the decision
	 * rules for "relevant" and "non-relevant".
	 * 
	 * @param topicid
	 * @param rels
	 * @param nrels
	 */
	public Qrels( String topicid, Map<String, Double> rels, Map<String, Double> nrels ) {
		this.topicid = topicid;
		this.rels = rels;
		this.nrels = nrels;
	}
	
	/**
	 * Get the topic id for this qrels.
	 * 
	 * @return
	 */
	public String topicid() {
		return this.topicid;
	}
	
	/**
	 * Set a topic id for this qrels.
	 * 
	 * @param topicid
	 * @return
	 */
	public Qrels setTopicid( String topicid ) {
		this.topicid = topicid;
		return this;
	}
	
	/**
	 * @return A map of docno-score (String to Double map) for the "relevant" documents.
	 */
	public Map<String, Double> rels() {
		return rels;
	}
	
	/**
	 * Set up the "rels" map that stores doc-score for "relevant" documents. Note that all documents stored in the map will be considered as "relevant" in the
	 * binary case regardless of the actual relevance scores.
	 * 
	 * @param rels
	 *            A map of docno-score (String to Double map) for the "relevant" documents.
	 * @return
	 */
	public Qrels setRels( Map<String, Double> rels ) {
		this.rels = rels;
		return this;
	}
	
	/**
	 * @return A map of docno-score (String to Double map) for the judged "non-relevant" documents.
	 */
	public Map<String, Double> nrels() {
		return nrels;
	}
	
	/**
	 * Set up the "nrels" map that stores doc-score for the "non-relevant" documents. Note that all documents stored in the map will be considered as
	 * "non-relevant" in the binary case regardless of the actual relevance scores.
	 * 
	 * @param nrels
	 *            A map of docno-score (String to Double map) for the judged "non-relevant" documents.
	 * @return
	 */
	public Qrels setNrels( Map<String, Double> nrels ) {
		this.nrels = nrels;
		return this;
	}
	
	/**
	 * Get a map storing all judged documents and their relevance grades.
	 */
	public Map<String, Double> getMap() {
		Map<String, Double> qrels = new TreeMap<String, Double>();
		for ( String docno : rels.keySet() ) {
			qrels.put( docno, rels.get( docno ) );
		}
		for ( String docno : nrels.keySet() ) {
			qrels.put( docno, nrels.get( docno ) );
		}
		return qrels;
	}
	
	/**
	 * @return A set of judged relevant documents.
	 */
	public Set<String> relevantDocuments() {
		return rels.keySet();
	}
	
	/**
	 * @return Total number of relevant documents.
	 */
	public int numrels() {
		return rels.size();
	}
	
	/**
	 * Get the relevance score of the document (if the document had not been unjudged, the relevance score will be 0).
	 * 
	 * @param docno
	 * @return
	 */
	public double relevance( String docno ) {
		double rel = 0;
		if ( rels.containsKey( docno ) ) {
			rel = rels.get( docno );
		} else if ( nrels.containsKey( docno ) ) {
			rel = nrels.get( docno );
		}
		return rel;
	}
	
	/**
	 * Set relevance score for a document, which will be automatically classified as "relevant" or "non-relevant" by score>0. Previously set relevance scores
	 * will be overridden.
	 * 
	 * @param docno
	 * @param relevance
	 * @return
	 */
	public Qrels collectJudgments( String docno, double relevance ) {
		Double prev_relevance = null;
		if ( rels.containsKey( docno ) ) {
			prev_relevance = rels.get( docno );
		} else if ( nrels.containsKey( docno ) ) {
			prev_relevance = nrels.get( docno );
		}
		if ( prev_relevance == null || prev_relevance < relevance ) {
			// collect the new relevance score if there is no previous score
			// or the previous relevance score is less than the new relevance score
			rels.remove( docno );
			nrels.remove( docno );
			if ( relevance > 0 ) {
				rels.put( docno, relevance );
			} else {
				nrels.put( docno, relevance );
			}
		}
		return this;
	}
	
	/**
	 * Whether the document had been judged (no matter it is relevant or non-relevant).
	 * 
	 * @param docno
	 * @return
	 */
	public boolean hasBeenJudged( String docno ) {
		return rels.containsKey( docno ) || nrels.containsKey( docno );
	}
	
	/**
	 * Load qrels from a trec format qrels file. Sometimes people may want to map the original relevance judgments labels into other relevance scores (to better
	 * calculate metrics such as nDCG). The map relscore_mapping is a map for this purpose. If relscore_mapping is null, document's relevance scores will be
	 * stored as it is labeled in the file.
	 * 
	 * @param instream
	 *            An input stream for the qrels file.
	 * @param rel_mapping
	 *            Mapping between original relevance labels (as Strings) and new relevance scores.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( InputStream instream, Map<String, Double> rel_mapping ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( instream, "UTF-8" ) );
		Map<String, Qrels> qrels = loadQrels( reader, rel_mapping );
		reader.close();
		return qrels;
	}
	
	/**
	 * Load qrels from a trec format qrels file. Sometimes people may want to map the original relevance judgments labels into other relevance scores (to better
	 * calculate metrics such as nDCG). The map relscore_mapping is a map for this purpose. If relscore_mapping is null, document's relevance scores will be
	 * stored as it is labeled in the file.
	 * 
	 * @param r
	 *            A reader for the qrels file.
	 * @param rel_mapping
	 *            Mapping between original relevance labels (as Strings) and new relevance scores.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( Reader r, Map<String, Double> rel_mapping ) throws IOException {
		Map<String, Qrels> qrels = new TreeMap<String, Qrels>();
		BufferedReader reader = new BufferedReader( r );
		String line = reader.readLine();
		while ( line != null ) {
			String[] matched = line.trim().split( "\\s+" );
			if ( matched == null || matched.length != 4 ) {
				System.err.println( " >> unrecognized line in qrel file: " + line );
			} else {
				String qid = matched[0].trim();
				String docno = matched[2].trim();
				String label = matched[3].trim();
				double relevance = 0;
				if ( rel_mapping == null ) {
					relevance = Double.parseDouble( label );
				} else {
					relevance = rel_mapping.get( label );
				}
				if ( !qrels.containsKey( qid ) ) {
					qrels.put( qid, new Qrels() );
				}
				qrels.get( qid ).collectJudgments( docno, relevance );
			}
			line = reader.readLine();
		}
		reader.close();
		return qrels;
	}
	
	/**
	 * Load qrels from a trec format qrels file. Sometimes people may want to map the original relevance judgments labels into other relevance scores (to better
	 * calculate metrics such as nDCG). The map relscore_mapping is a map for this purpose. If relscore_mapping is null, document's relevance scores will be
	 * stored as it is labeled in the file.
	 * 
	 * @param fqrel
	 *            A TREC format qrels file.
	 * @param rel_mapping
	 *            Mapping between original relevance labels and new relevance scores.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( File fqrel, Map<String, Double> rel_mapping ) throws IOException {
		return loadQrels( IOUtils.getBufferedReader( fqrel ), rel_mapping );
	}
	
	/**
	 * Load qrels from a trec format qrels file. Sometimes people may want to map the original relevance judgments labels into other relevance scores (to better
	 * calculate metrics such as nDCG). The map relscore_mapping is a map for this purpose. If relscore_mapping is null, document's relevance scores will be
	 * stored as it is labeled in the file.
	 * 
	 * @param path
	 *            A TREC format qrels file.
	 * @param rel_mapping
	 *            Mapping between original relevance labels and new relevance scores.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( String path, Map<String, Double> rel_mapping ) throws IOException {
		return loadQrels( new File( path ), rel_mapping );
	}
	
	/**
	 * Load qrels from a trec format qrels file. The original relevance label in the qrels file will be used as the documents' relevance scores.
	 * 
	 * @param instream
	 *            An input stream for the qrels file.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( InputStream instream ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( instream, "UTF-8" ) );
		Map<String, Qrels> qrels = loadQrels( reader, null );
		reader.close();
		return qrels;
	}
	
	/**
	 * Load qrels from a trec format qrels file. The original relevance label in the qrels file will be used as the documents' relevance scores.
	 * 
	 * @param r
	 *            A reader for the qrels file.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( Reader r ) throws IOException {
		return loadQrels( r, null );
	}
	
	/**
	 * Load qrels from a trec format qrels file. The original relevance label in the qrels file will be used as the documents' relevance scores.
	 * 
	 * @param fqrel
	 *            A TREC format qrels file.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( File fqrel ) throws IOException {
		return loadQrels( fqrel, null );
	}
	
	/**
	 * Load qrels from a trec format qrels file. The original relevance label in the qrels file will be used as the documents' relevance scores.
	 * 
	 * @param path
	 *            A TREC format qrels file.
	 * @return A topicid (String) to Qrels map storing qrels of many topics.
	 * @throws IOException
	 */
	public static Map<String, Qrels> loadQrels( String path ) throws IOException {
		return loadQrels( path, null );
	}
	
	/**
	 * Get the discounted irel for the current qrels.
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A qrels object storing the discounting relevance scores.
	 */
	public Qrels irel( String[][] prevResults, double pdown, double beta ) {
		return irel( prevResults, null, null, pdown, beta );
	}
	
	/**
	 * Get the discounted irel for the current qrels.
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param max_browsed_ranks
	 *            The rank of the lowest ranked results that were shown to the user in each of the past query's results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A qrels object storing the discounting relevance scores.
	 */
	public Qrels irel( String[][] prevResults, int[] max_browsed_ranks, double pdown, double beta ) {
		return irel( prevResults, null, max_browsed_ranks, pdown, beta );
	}
	
	/**
	 * Get the discounted irel for the current qrels.
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param max_clicked_ranks
	 *            The rank of the lowest ranked results that the user clicked in each of the past query's results.
	 * @param max_browsed_ranks
	 *            The rank of the lowest ranked results that were shown to the user in each of the past query's results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A qrels object storing the discounting relevance scores.
	 */
	public Qrels irel( String[][] prevResults, int[] max_clicked_ranks, int[] max_browsed_ranks, double pdown, double beta ) {
		Map<String, Double> rels = irel( this.rels, prevResults, max_clicked_ranks, max_browsed_ranks, pdown, beta );
		Qrels irel = new Qrels( rels, nrels );
		return irel;
	}
	
	/**
	 * Discount the original users' relevance assessments scores (i.e. irel) when neither click through information nor turning of pages are available.
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> irel( Map<String, Double> relevance, String[][] resultlists, double pdown, double beta ) {
		return irel( relevance, resultlists, null, null, pdown, beta );
	}
	
	/**
	 * Discount the original users' relevance assessments scores (i.e. irel) when no click through information is available.
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param max_browsed_ranks
	 *            The rank of the lowest ranked results that were shown to the user in each of the past query's results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> irel( Map<String, Double> relevance, String[][] resultlists, int[] max_browsed_ranks, double pdown, double beta ) {
		return irel( relevance, resultlists, null, max_browsed_ranks, pdown, beta );
	}
	
	/**
	 * <p>
	 * Discount the original users' relevance assessments scores (i.e. irel). The method implemented here is an extension of the method used in [Jiang 2012]. In
	 * the original method, the probability of discounting a document depends on the ranking of the document in the result list. Here we further consider two
	 * types of information:
	 * </p>
	 * <ul>
	 * <li>First, let D be the lowest ranked documents that the user clicked, we assume that the user had examined all the documents ranked higher than D. Here
	 * we assume: 1) users are browsing and examining results one by one according to their ranks; 2) only after examining a result can the user click the
	 * result. According to the two assumptions, for any clicked result, the user should have examined the results ranked higher than the clicked one. Here
	 * $max_clicked_rank is the rank of the lowest ranked clicked result.</li>
	 * <li>Second, usually a search system only shows 10-20 results per page. Therefore, user's turning of pages can help us determine what results the user had
	 * possibly examined. Here $max_browsed_rank is the rank of the lowest ranked result shown to the user.</li>
	 * </ul>
	 * <p>
	 * According to these information, we don't need to estimate examining probability for each of the result in the list. Instead, results ranked higher to
	 * $max_clicked_pos will have the probability 1 of being examined, and the results ranked lower than $max_browsed_pos will have no chance of being examined.
	 * Examining probability estimation only happens for results ranked from $max_clicked_rank + 1 (include) to $max_browsed_rank (include).
	 * </p>
	 * <p>
	 * Finally, we assume the loss of attractiveness in each of the past query's results are independent with each other and calculate the expected total
	 * discounted relevance score among all the previous results.
	 * </p>
	 * <p>
	 * Reference:
	 * </p>
	 * <p>
	 * [Jiang 2012] Jiepu Jiang, Daqing He, Shuguang Han, Zhen Yue, and Chaoqun Ni. 2012. Contextual evaluation of query reformulations in a search session by
	 * user simulation. In Proceedings of the 21st ACM international conference on Information and knowledge management (CIKM '12), 2012: 2635-2638.
	 * </p>
	 * 
	 * @param relevance
	 *            A String to Double Users' original relevance judgments.
	 * @param resultlist
	 *            All of the past queries' search results.
	 * @param max_clicked_ranks
	 *            The rank of the lowest ranked results that the user clicked in each of the past query's results.
	 * @param max_browsed_ranks
	 *            The rank of the lowest ranked results that were shown to the user in each of the past query's results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> irel( Map<String, Double> relevance, String[][] resultlists, int[] max_clicked_ranks, int[] max_browsed_ranks, double pdown, double beta ) {
		
		Map<String, Double> irel = new TreeMap<String, Double>();
		for ( String doc : relevance.keySet() ) {
			irel.put( doc, relevance.get( doc ) );
		}
		
		for ( int ix = 0 ; ix < resultlists.length ; ix++ ) {
			String[] resultlist = resultlists[ix];
			int max_clicked_rank = max_clicked_ranks == null ? 1 : max_clicked_ranks[ix];
			int max_browsed_rank = max_browsed_ranks == null ? resultlist.length : max_browsed_ranks[ix];
			Map<String, Double> prob_discount = probDiscount( resultlist, max_clicked_rank, max_browsed_rank, pdown, beta );
			for ( String doc : irel.keySet() ) {
				double rel = irel.get( doc );
				double pb_discounted = prob_discount.containsKey( doc ) ? prob_discount.get( doc ) : 0;
				rel = rel * ( 1 - pb_discounted );
				irel.put( doc, rel );
			}
		}
		
		return irel;
		
	}
	
	/**
	 * This is the implementation of probDiscount( String[], int, int, double, double ) without knowing neither users' clicked results nor the turning of pages,
	 * i.e. $max_clicked_rank and $max_browsed_rank. The browsing model being used is exactly the original RBP browsing model.
	 * 
	 * @param resultlist
	 *            A past query's search results.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> probDiscount( String[] resultlist, double pdown, double beta ) {
		return probDiscount( resultlist, 1, resultlist.length, pdown, beta );
	}
	
	/**
	 * This is the implementation of probDiscount( String[], int, int, double, double ) without knowing users' clicked results, i.e. $max_clicked_rank. This is
	 * equivalent to believing the user has only for sure examined the top ranked result (the original assumption in RBP browsing model).
	 * 
	 * @param resultlist
	 *            A past query's search results.
	 * @param max_browsed_rank
	 *            The rank of the lowest ranked results that were shown to the user.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> probDiscount( String[] resultlist, int max_browsed_rank, double pdown, double beta ) {
		return probDiscount( resultlist, 1, max_browsed_rank, pdown, beta );
	}
	
	/**
	 * <p>
	 * Calculate the probabilities that the documents' relevance scores should be discounted based on users' behaviors related to one previous query's search
	 * results, including:
	 * </p>
	 * <ul>
	 * <li>the previous query's search results (given by $resultlist)</li>
	 * <li>clicked documents</li>
	 * <li>turning of pages</li>
	 * </ul>
	 * <p>
	 * The method implemented here is an extension of the method used in [Jiang 2012]. In the original method, the probability of discounting a document depends
	 * on the ranking of the document in the result list. Here we further consider two types of information:
	 * </p>
	 * <ul>
	 * <li>First, let D be the lowest ranked documents that the user clicked, we assume that the user had examined all the documents ranked higher than D. Here
	 * we assume: 1) users are browsing and examining results one by one according to their ranks; 2) only after examining a result can the user click the
	 * result. According to the two assumptions, for any clicked result, the user should have examined the results ranked higher than the clicked one. Here
	 * $max_clicked_rank is the rank of the lowest ranked clicked result.</li>
	 * <li>Second, usually a search system only shows 10-20 results per page. Therefore, user's turning of pages can help us determine what results the user had
	 * possibly examined. Here $max_browsed_rank is the rank of the lowest ranked result shown to the user.</li>
	 * </ul>
	 * <p>
	 * According to these information, we don't need to estimate examining probability for each of the result in the list. Instead, results ranked higher to
	 * $max_clicked_pos will have the probability 1 of being examined, and the results ranked lower than $max_browsed_pos will have no chance of being examined.
	 * Examining probability estimation only happens for results ranked from $max_clicked_rank + 1 (include) to $max_browsed_rank (include).
	 * </p>
	 * <p>
	 * Reference:
	 * </p>
	 * <p>
	 * [Jiang 2012] Jiepu Jiang, Daqing He, Shuguang Han, Zhen Yue, and Chaoqun Ni. 2012. Contextual evaluation of query reformulations in a search session by
	 * user simulation. In Proceedings of the 21st ACM international conference on Information and knowledge management (CIKM '12), 2012: 2635-2638.
	 * </p>
	 * 
	 * @param resultlist
	 *            A past query's search results.
	 * @param max_clicked_rank
	 *            The rank of the lowest ranked results that the user clicked.
	 * @param max_browsed_rank
	 *            The rank of the lowest ranked results that were shown to the user.
	 * @param pdown
	 *            The probability of continueing examing the next result.
	 * @param beta
	 *            The probability that a document will lose its attractiveness after it is examined once by the user.
	 * @return A String to Double map storing the probability that each document (by docno) will be discounted.
	 */
	public static Map<String, Double> probDiscount( String[] resultlist, int max_clicked_rank, int max_browsed_rank, double pdown, double beta ) {
		
		Map<String, Double> probs = new HashMap<String, Double>();
		
		for ( int ix = 0 ; ix < resultlist.length && ix < max_clicked_rank ; ix++ ) {
			// the user has for sure examined the document from rank 1 to $max_clicked_pos
			double prob_examine = 1.0;
			double prob_lose_attr = beta;
			double prob_discount = prob_examine * prob_lose_attr;
			probs.put( resultlist[ix], prob_discount );
		}
		
		for ( int ix = max_clicked_rank ; ix < resultlist.length && ix < max_browsed_rank ; ix++ ) {
			// the user has probably examined the document from rank $max_clicked_pos to $max_browsed_pos
			// the probably of examining will be estimated by the RBP-based browsing model
			// i.e.
			// [rank] [probability of examining]
			// $max_clicked_pos 1
			// $max_clicked_pos+1 pdown
			// $max_clicked_pos+2 pdown^2
			// ...
			// ...
			// until the document at rank $max_browsed_pos
			int rank = ix + 1;
			double prob_examine = Math.pow( pdown, rank - max_clicked_rank );
			double prob_lose_attr = beta;
			double prob_discount = prob_examine * prob_lose_attr;
			probs.put( resultlist[ix], prob_discount );
		}
		
		for ( int ix = max_browsed_rank ; ix < resultlist.length ; ix++ ) {
			// the user has for sure not exmined the document since rank $max_browsed_pos + 1
			// so, there will be no discount for these documents
		}
		
		return probs;
		
	}
	
}
