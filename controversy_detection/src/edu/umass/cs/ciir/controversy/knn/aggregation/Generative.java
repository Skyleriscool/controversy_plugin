package edu.umass.cs.ciir.controversy.knn.aggregation;

import java.util.Map;
import java.util.List;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

/**
 * <p>
 * A generative model aggregation function.
 * </p>
 * <p>
 * P(controversy|D) = SUM_i P(controversy|Wi)*P(Wi|D)
 * </p>
 * <p>
 * Under some assumption, P(Wi|D) is proportional to the QL score of Wi given the extracted query q.
 * </p>
 * 
 * @author Jiepu Jiang
 * @version May 24, 2015
 */
public class Generative implements KNNAggregation {
	
	public double getAggregationScore( List<EntryValue> entries, List<EntryValue> scores, Map<String, Object> info ) {
		long timestamp = System.currentTimeMillis();
		double sum = 0;
		double norm = 0;
		for ( int ix = 0 ; ix < entries.size() ; ix++ ) {
			EntryValue similarity = entries.get( ix );
			EntryValue cscore = scores.get( ix );
			double sim = similarity != null ? Math.pow( Math.E, similarity.getScore() ) : 0;
			double c = cscore != null ? cscore.getScore() : 0;
			sum = sum + sim * c;
			norm = norm + sim;
		}
		info.put( "time_aggregate_scores", ( System.currentTimeMillis() - timestamp ) / 1000.0 );
		return norm == 0 ? 0 : ( sum / norm );
	}
	
}
