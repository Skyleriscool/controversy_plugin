package edu.umass.cs.ciir.controversy.knn;

import java.io.IOException;
import java.util.List;

import edu.umass.cs.ciir.controversy.knn.aggregation.KNNAggregation;
import edu.umass.cs.ciir.controversy.knn.db.ControversyDatabase;
import edu.umass.cs.ciir.controversy.knn.sim.IndexSearcher;
import edu.umass.cs.ciir.controversy.knn.sim.QueryConstructor;

public class KNNScorer {
	
	protected QueryConstructor qc;
	protected IndexSearcher index;
	protected ControversyDatabase database;
	protected KNNAggregation aggregation;
	
	public KNNScorer( QueryConstructor qc, IndexSearcher index, ControversyDatabase database, KNNAggregation aggregation ) {
		this.qc = qc;
		this.index = index;
		this.database = database;
		this.aggregation = aggregation;
	}
	
	public ObjectInfo<Double> getScore( String text, int topwords, int topentries ) {
		ObjectInfo<Double> score = new ObjectInfo<Double>();
		Object query = qc.constructQuery( text, topwords, score.info );
		List<EntryValue> entries = index.search( query, topentries, score.info );
		List<EntryValue> scores = database.getControversyScores( entries, score.info );
		score.setObject( aggregation.getAggregationScore( entries, scores, score.info ) );
		return score;
	}
	
	public void close() throws IOException {
		index.close();
		database.close();
	}
	
}
