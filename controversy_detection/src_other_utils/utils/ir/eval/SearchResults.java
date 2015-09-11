package utils.ir.eval;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;

import org.apache.lucene.index.IndexReader;

import utils.IOUtils;
import lemurproject.indri.QueryEnvironment;

/**
 * SearchResults is a fixed size sorted set that stores search results. It is used as holders when collecting search results. The results are not randomly
 * accessible. One can only iteratively visit each element of the set.
 * 
 * @author Jiepu Jiang
 * @version Feb 17, 2015
 */
public class SearchResults implements Iterable<SearchResult> {
	
	protected int maxSize;
	protected TreeSet<SearchResult> resultset;
	
	/**
	 * Constructor with unlimited set size.
	 */
	public SearchResults() {
		this( 0 );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param maxSize
	 *            Maximum size of the result set
	 */
	public SearchResults( int maxSize ) {
		this.maxSize = maxSize;
		this.resultset = new TreeSet<SearchResult>();
	}
	
	/**
	 * @return Maximum size of the result set
	 */
	public int maxSize() {
		return this.maxSize;
	}
	
	/**
	 * @return Current actual size of the result set
	 */
	public int size() {
		return resultset.size();
	}
	
	/**
	 * @return A ranked iteration of search result in current result set
	 */
	public Iterator<SearchResult> iterator() {
		return resultset.iterator();
	}
	
	/**
	 * @param result
	 *            The search result to be added into current result list
	 * @return Whether the result has been added to the current list. Note that failing to add a result can be resulted by multiple reasons. (1) the list
	 *         exceeds its max length and the result to be added has lower relevance score than any existing ones; (2) the list already contains a completely
	 *         identical search result item.
	 */
	public boolean add( SearchResult result ) {
		boolean added = resultset.add( result );
		if ( added && ( maxSize > 0 && resultset.size() > maxSize ) ) {
			SearchResult smallest = resultset.last();
			resultset.remove( smallest );
			if ( smallest == result ) {
				added = false;
			}
		}
		return added;
	}
	
	/**
	 * @return The ranked set of results
	 */
	public Set<SearchResult> getResultSet() {
		return resultset;
	}
	
	/**
	 * @return A list of the stored search results
	 */
	public List<SearchResult> getResultList() {
		List<SearchResult> list = new ArrayList<SearchResult>( resultset.size() );
		list.addAll( resultset );
		return list;
	}
	
	/**
	 * Get the top n result as a list.
	 * 
	 * @param top
	 * @return
	 */
	public List<SearchResult> getResultList( int top ) {
		return getResultList( 0, top );
	}
	
	/**
	 * Get a list involving the xth (include) to the yth (exclude) element, in which: x = from, y = end, and the element index starts from 0.
	 * 
	 * @param from
	 * @param end
	 * @return
	 */
	public List<SearchResult> getResultList( int from, int end ) {
		List<SearchResult> list = new ArrayList<SearchResult>();
		int ix = 0;
		for ( SearchResult result : resultset ) {
			if ( ix >= end ) {
				break;
			}
			if ( from <= ix && ix < end ) {
				list.add( result );
			}
			ix++;
		}
		return list;
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @param resetAll
	 * @throws Exception
	 */
	public void setResultsDocno( QueryEnvironment index, String field, Map<Integer, String> reuse, boolean resetAll ) throws Exception {
		for ( SearchResult result : resultset ) {
			if ( resetAll || result.getDocno() == null ) {
				result.setDocno( index, field, reuse );
			}
		}
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @throws Exception
	 */
	public void setResultsDocno( QueryEnvironment index, String field, Map<Integer, String> reuse ) throws Exception {
		setResultsDocno( index, field, reuse, false );
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @param resetAll
	 * @throws Exception
	 */
	public void setResultsDocno( IndexReader index, String field, Map<Integer, String> reuse, boolean resetAll ) throws IOException {
		for ( SearchResult result : resultset ) {
			if ( resetAll || result.getDocno() == null ) {
				result.setDocno( index, field, reuse );
			}
		}
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @throws Exception
	 */
	public void setResultsDocno( IndexReader index, String field, Map<Integer, String> reuse ) throws IOException {
		setResultsDocno( index, field, reuse, false );
	}
	
	/**
	 * Set docid for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @param resetAll
	 * @throws Exception
	 */
	public void setResultsDocid( QueryEnvironment index, String field, Map<String, Integer> reuse, boolean resetAll ) throws Exception {
		for ( SearchResult result : resultset ) {
			if ( resetAll || result.getDocid() < 0 ) {
				result.setDocid( index, field, reuse );
			}
		}
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @throws Exception
	 */
	public void setResultsDocid( QueryEnvironment index, String field, Map<String, Integer> reuse ) throws Exception {
		setResultsDocid( index, field, reuse, false );
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @param resetAll
	 * @throws Exception
	 */
	public void setResultsDocid( IndexReader index, String field, Map<String, Integer> reuse, boolean resetAll ) throws Exception {
		for ( SearchResult result : resultset ) {
			if ( resetAll || result.getDocid() < 0 ) {
				result.setDocid( index, field, reuse );
			}
		}
	}
	
	/**
	 * Set docno for results in the result list.
	 * 
	 * @param index
	 * @param field
	 * @param reuse
	 * @throws Exception
	 */
	public void setResultsDocid( IndexReader index, String field, Map<String, Integer> reuse ) throws Exception {
		setResultsDocid( index, field, reuse, false );
	}
	
	public String toString() {
		return toString( -1, null );
	}
	
	public String toString( int top ) {
		return toString( top, null );
	}
	
	public String toString( int top, Set<String> paras ) {
		StringBuilder sb = new StringBuilder();
		int rank = 1;
		for ( SearchResult result : resultset ) {
			if ( top > 0 && rank > top ) {
				break;
			}
			result.toString( sb, rank, paras );
			sb.append( "\n" );
			rank++;
		}
		return sb.toString();
	}
	
	/**
	 * Output the results in TREC format.
	 * 
	 * @param useAssignedRank
	 *            Whether to use the assigned rank of result
	 * @param qid
	 *            Topic id
	 * @param runname
	 *            Run name
	 * @param top
	 *            Top number of result to be outputted
	 * @param separator
	 *            A separator (by default "Q0")
	 * @return
	 */
	public String toStringTrecFormat( String qid, String runname, int top, String separator ) {
		StringBuilder sb = new StringBuilder();
		int rank = 1;
		for ( SearchResult result : resultset ) {
			if ( top > 0 && rank > top ) {
				break;
			}
			sb.append( result.toStringTrecFormat( qid, runname, rank, separator ) );
			sb.append( "\n" );
			rank++;
		}
		return sb.toString();
	}
	
	/**
	 * Output the results in TREC format.
	 * 
	 * @param qid
	 *            Topic id
	 * @param runname
	 *            Run name
	 * @param top
	 *            Top number of result to be outputted
	 * @return
	 */
	public String toStringTrecFormat( String qid, String runname, int top ) {
		return toStringTrecFormat( qid, runname, top, SearchResult.DEFAULT_TREC_RESULT_SEPARATOR );
	}
	
	/**
	 * Output the results in TREC format.
	 * 
	 * @param qid
	 *            Topic id
	 * @param runname
	 *            Run name
	 * @param separator
	 *            A separator (by default "Q0")
	 * @return
	 */
	public String toStringTrecFormat( String qid, String runname, String separator ) {
		return toStringTrecFormat( qid, runname, 0, separator );
	}
	
	/**
	 * Output the results in TREC format.
	 * 
	 * @param qid
	 *            Topic id
	 * @param runname
	 *            Run name
	 * @return
	 */
	public String toStringTrecFormat( String qid, String runname ) {
		return toStringTrecFormat( qid, runname, 0, SearchResult.DEFAULT_TREC_RESULT_SEPARATOR );
	}
	
	/**
	 * Load from a TREC format result file.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<SearchResult>> loadTrecFormat( Reader reader, int maxrank ) throws IOException {
		Map<String, List<SearchResult>> results = new TreeMap<String, List<SearchResult>>();
		BufferedReader r = new BufferedReader( reader );
		String line = r.readLine();
		while ( line != null ) {
			String[] splits = line.trim().split( "\\s+" );
			if ( splits.length != 6 ) {
				if ( line.trim().length() > 0 ) {
					// output only when the line is not an empty line and cannot be parsed
					// System.err.println(" >> cannot parse the result file: "+line);
				} else {
					// empty lines will be skipped without any notification
				}
			} else {
				String topic = splits[0].trim();
				String docno = splits[2].trim();
				double score = Double.parseDouble( splits[4].trim() );
				SearchResult result = new SearchResult( docno, score );
				if ( !results.containsKey( topic ) ) {
					results.put( topic, new ArrayList<SearchResult>() );
				}
				if ( maxrank <= 0 || results.get( topic ).size() < maxrank ) {
					results.get( topic ).add( result );
				}
			}
			line = r.readLine();
		}
		r.close();
		return results;
	}
	
	public static Map<String, List<SearchResult>> loadTrecFormat( Reader reader ) throws IOException {
		return loadTrecFormat( reader, 0 );
	}
	
	public static Map<String, List<SearchResult>> loadTrecFormat( File f, int maxrank ) throws IOException {
		BufferedReader reader = IOUtils.getBufferedReader( f );
		Map<String, List<SearchResult>> results = loadTrecFormat( reader, maxrank );
		reader.close();
		return results;
	}
	
	public static Map<String, List<SearchResult>> loadTrecFormat( File f ) throws IOException {
		BufferedReader reader = IOUtils.getBufferedReader( f );
		Map<String, List<SearchResult>> results = loadTrecFormat( reader );
		reader.close();
		return results;
	}
	
	public static Map<String, List<SearchResult>> loadTrecFormat( String path, int maxrank ) throws IOException {
		return loadTrecFormat( new File( path ), maxrank );
	}
	
	public static Map<String, List<SearchResult>> loadTrecFormat( String path ) throws IOException {
		return loadTrecFormat( new File( path ) );
	}
	
	public static List<String> toDocnoList( List<SearchResult> results ) {
		List<String> docnos = new ArrayList<String>();
		for ( SearchResult result : results ) {
			docnos.add( result.getDocno() );
		}
		return docnos;
	}
	
}
