package utils.ir.eval;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Pooling includes utility functions for generate pooled results.
 * 
 * @author Jiepu Jiang
 * @version Mar 7, 2013
 */
public class Pooling {
	
	/**
	 * Get depth-k pooling from multiple resultlist.
	 * 
	 * @param depth
	 *            Pooling depth.
	 * @param multiResults
	 *            Multiple results lists used for pooling.
	 * @return
	 */
	public static Set<String> depthK( int depth, SearchResults... multiResults ) {
		Set<String> pool = new HashSet<String>();
		for ( SearchResults results : multiResults ) {
			int pos = 1;
			for ( SearchResult result : results.getResultSet() ) {
				pool.add( result.getDocno() );
				pos++;
				if ( pos > depth ) {
					break;
				}
			}
		}
		return pool;
	}
	
	/**
	 * Get depth-k pooling from multiple resultlist.
	 * 
	 * @param depth
	 *            Pooling depth.
	 * @param multiResults
	 *            Multiple results lists used for pooling.
	 * @return
	 */
	public static Set<String> depthK( int depth, Collection<SearchResults> multiResults ) {
		Set<String> pool = new HashSet<String>();
		for ( SearchResults results : multiResults ) {
			int pos = 1;
			for ( SearchResult result : results.getResultSet() ) {
				pool.add( result.getDocno() );
				pos++;
				if ( pos > depth ) {
					break;
				}
			}
		}
		return pool;
	}
	
}
