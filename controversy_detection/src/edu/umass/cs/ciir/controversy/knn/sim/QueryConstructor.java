package edu.umass.cs.ciir.controversy.knn.sim;

import java.util.Map;

/**
 * A general interface for something that constructs a query based on a trunk of text.
 * 
 * @author Jiepu Jiang
 * @version May 23, 2015
 */
public interface QueryConstructor {
	
	/**
	 * Construct a query for a trunk of text. The query cannot exceed the specified number of words.
	 * 
	 * @param text
	 * @param topwords
	 * @param info
	 *            Optional and can be null (in that case you should skip it). You can store processing details in this info map.
	 * @return
	 */
	public Object constructQuery( String text, int topwords, Map<String, Object> info );
	
}
