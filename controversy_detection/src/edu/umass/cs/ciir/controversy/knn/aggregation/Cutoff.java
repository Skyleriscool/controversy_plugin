package edu.umass.cs.ciir.controversy.knn.aggregation;

import java.util.List;
import java.util.Map;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

public class Cutoff implements KNNAggregation {
	
	protected KNNAggregation aggregation;
	protected double cutoff;
	
	public Cutoff( KNNAggregation aggregation, double cutoff ) {
		this.aggregation = aggregation;
		this.cutoff = cutoff;
	}
	
	public double getAggregationScore( List<EntryValue> entries, List<EntryValue> scores, Map<String, Object> info ) {
		double score = aggregation.getAggregationScore( entries, scores, info );
		if ( info != null ) {
			info.put( "rawscore", scores );
			info.put( "cutoff", cutoff );
		}
		return score > cutoff ? 1 : 0;
	}
	
}
