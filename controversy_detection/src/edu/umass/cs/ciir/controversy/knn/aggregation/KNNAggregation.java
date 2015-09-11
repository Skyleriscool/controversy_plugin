package edu.umass.cs.ciir.controversy.knn.aggregation;

import java.util.Map;
import java.util.List;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

public interface KNNAggregation {
	
	/**
	 * Aggregate a list of KNN entries' similarity scores and controversy scores into a final controversy score.
	 * 
	 * @param entries
	 * @param scores
	 * @param info
	 *            Optional and can be null (in that case you should skip it). You can store processing details in this info map.
	 * @return
	 */
	public double getAggregationScore( List<EntryValue> entries, List<EntryValue> scores, Map<String, Object> info );
	
}
