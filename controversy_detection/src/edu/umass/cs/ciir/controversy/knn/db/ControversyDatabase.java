package edu.umass.cs.ciir.controversy.knn.db;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

/**
 * ControversyDatabase is a general interface for something that can retrive the controversy score of a Wikipedia entry.
 * 
 * @author Jiepu Jiang
 * @version May 23, 2015
 */
public abstract class ControversyDatabase {
	
	/**
	 * Get a score indicating the degree of controversy for the specified Wikipedia entry.
	 * 
	 * @param entry
	 * @return
	 */
	public abstract EntryValue getControversyScore( EntryValue entry );
	
	/**
	 * Close all resources.
	 * 
	 * @throws IOException
	 */
	public abstract void close() throws IOException;
	
	/**
	 * Get scores for a list of entries. Subclasses can replace this method if more efficiency one exists.
	 * 
	 * @param entries
	 * @return
	 */
	public List<EntryValue> getControversyScores( List<EntryValue> entries, Map<String, Object> info ) {
		long timestamp = System.currentTimeMillis();
		List<EntryValue> scores = new ArrayList<EntryValue>();
		for ( EntryValue entry : entries ) {
			scores.add( getControversyScore( entry ) );
		}
		if ( info != null ) {
			info.put( "top_wikientries_controversy", scores );
			info.put( "time_retrieve_entry_controversy_scores", ( System.currentTimeMillis() - timestamp ) / 1000.0 );
		}
		return scores;
	}
	
}
