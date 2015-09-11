package utils.ir.dataset.topic.session;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * SessionQuery stores a query's information in the context of a search session in the TREC session track datasets.
 * 
 * @author Jiepu Jiang
 * @version Nov 18, 2014
 */
public class TRECSessionQuery {
	
	protected TRECSession session;
	
	protected int qix;
	protected String query;
	protected List<String> terms_unstemmed;
	protected List<TRECResultSummary> results;
	protected List<TRECResultClick> clicks;
	protected Map<String, TRECResultSummary> url_results;
	protected Map<String, TRECResultSummary> docno_results;
	
	protected double time_start;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "q[" + qix + "]: " + query + "\n" );
		sb.append( "# results = " + results.size() + ", # clicks = " + clicks.size() + "\n" );
		for ( int ix = 0 ; ix < results.size() ; ix++ ) {
			sb.append( " >> result[" + ( ix + 1 ) + "], click = " + results.get( ix ).clicked + ": " + results.get( ix ).docno + ", " + results.get( ix ).url + "\n" );
			sb.append( "    " + results.get( ix ).title + "\n" );
			sb.append( "    " + results.get( ix ).snippet + "\n" );
		}
		return sb.toString();
	}
	
	protected TRECSessionQuery() {
		this.results = new ArrayList<TRECResultSummary>();
		this.clicks = new ArrayList<TRECResultClick>();
		this.terms_unstemmed = new ArrayList<String>();
		this.url_results = new TreeMap<String, TRECResultSummary>();
		this.docno_results = new TreeMap<String, TRECResultSummary>();
	}
	
	/**
	 * Get the session information.
	 */
	public TRECSession getSession() {
		return this.session;
	}
	
	/**
	 * Get query ID, which is the session id plus the query's sequence in the session.
	 */
	public String getQueryID() {
		return session.getSessionID() + "_" + this.qix;
	}
	
	/**
	 * Get the sequence of the query in the session (starts from 0).
	 */
	public int getQuerySequence() {
		return this.qix;
	}
	
	/**
	 * Get the query text.
	 */
	public String getQueryText() {
		return this.query;
	}
	
	/**
	 * Get unstemmed query terms.
	 */
	public List<String> getQueryTermsUnstemmed() {
		return this.terms_unstemmed;
	}
	
	/**
	 * Get the list of results for the query.
	 */
	public List<TRECResultSummary> getResults() {
		return this.results;
	}
	
	/**
	 * Get the ix th result for the query.
	 */
	public TRECResultSummary getResult( int ix ) {
		return this.results.get( ix );
	}
	
	/**
	 * Look up the position of the specified URL in the result list. Position starts from 1.
	 */
	public int getResultPositionByURL( String url ) {
		url = url.trim().toLowerCase();
		if ( url_results.containsKey( url ) ) {
			return url_results.get( url ).getPosition();
		}
		return -1;
	}
	
	/**
	 * Look up the position of the specified docno in the result list. Position starts from 1.
	 */
	public int getResultPositionByDocno( String docno ) {
		docno = docno.trim().toLowerCase();
		if ( docno_results.containsKey( docno ) ) {
			return docno_results.get( docno ).getPosition();
		}
		return -1;
	}
	
	/**
	 * Get a list of clicking information by its sequence.
	 */
	public List<TRECResultClick> getClicks() {
		return this.clicks;
	}
	
	/**
	 * Get the number of clicks for the query.
	 */
	public int numClicks() {
		return clicks.size();
	}
	
	/**
	 * Get whether there are any clicks for the query.
	 */
	public boolean hasClicks() {
		return numClicks() > 0;
	}
	
	/**
	 * Check whether the query's submit time recorded in the log is valid or not.
	 */
	public boolean hasValidQueryTime() {
		if ( this.time_start >= 0 ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the time of submitting the query.
	 */
	public double getTime() {
		return this.time_start;
	}
	
	/**
	 * Get the time interval to the submission of the next query (if any), or negative values either if it is the last query in the session or .
	 */
	public double getTimeToNextQuery() {
		if ( session != null && qix + 1 < session.queries.size() ) {
			double time_this = getTime();
			double time_next = session.queries.get( qix + 1 ).getTime();
			if ( time_this >= 0 && time_next >= 0 && time_next > time_this ) {
				return time_next - time_this;
			}
		}
		return -1;
	}
}
