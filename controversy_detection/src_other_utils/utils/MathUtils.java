package utils;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Utilities related to mathematics.
 * 
 * @author Jiepu Jiang
 * @version Feb 28, 2013
 */
public class MathUtils {
	
	/**
	 * Calculate the Jaccard similarity of two collections.
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static double jaccard( Collection set1, Collection set2 ) {
		Collection union = CollectionUtils.union( set1, set2 );
		Collection overlap = CollectionUtils.intersection( set1, set2 );
		return overlap.size() == 0 ? 0.0 : ( 1.0 * overlap.size() / union.size() );
	}
	
}
