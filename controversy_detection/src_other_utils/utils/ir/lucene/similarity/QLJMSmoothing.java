package utils.ir.lucene.similarity;

import java.util.Locale;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

import utils.ir.lucene.LuceneUtils;
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lm.unigram.EstimatedModel;

/**
 * Lucene similarity function for query likelihood language model with JM smoothing.
 * 
 * @author Jiepu Jiang
 * @version Feb 18, 2015
 */
public class QLJMSmoothing extends LMSimilarity {
	
	private double lambda;
	
	public QLJMSmoothing( CollectionModel collectionModel, double lambda ) {
		super( collectionModel );
		this.lambda = lambda;
	}
	
	public QLJMSmoothing( double lambda ) {
		this.lambda = lambda;
	}
	
	protected float score( BasicStats stats, float freq, float docLen ) {
		double weight = stats.getTotalBoost();
		double pwml = freq / docLen;
		double pwc = ( (LMStats) stats ).getCollectionProbability();
		if ( Double.isNaN( pwml ) || Double.isFinite( pwml ) ) {
			pwml = 0;
		}
		if ( Double.isNaN( pwc ) || Double.isFinite( pwc ) ) {
			pwc = 0;
		}
		double pwd = pwml * ( 1 - lambda ) + pwc * lambda;
		double factor = 0;
		if ( pwd > 0 && pwc > 0 ) {
			factor = Math.log( pwd / pwc );
			if ( factor < 0 ) {
				factor = 0;
			}
		}
		return (float) ( weight * factor );
	}
	
	/**
	 * Rescale the score of lucene results to the standard query likelihood model.
	 * 
	 * @param score
	 * @param index
	 * @param field
	 * @param terms
	 * @return
	 * @throws IOException
	 */
	public static double rescaleScore( double score, IndexReader index, String field, String[] terms ) throws IOException {
		return rescaleScore( score, index, field, terms, null );
	}
	
	/**
	 * Rescale the score of lucene results to the standard query likelihood model.
	 * 
	 * @param score
	 * @param index
	 * @param field
	 * @param terms
	 * @param weights
	 * @return
	 * @throws IOException
	 */
	public static double rescaleScore( double score, IndexReader index, String field, String[] terms, double[] weights ) throws IOException {
		UnigramModel corpus_model = EstimatedModel.MLE( LuceneUtils.getCorpusSample( index, field, false ) );
		double sum = 0;
		for ( int ix = 0 ; ix < terms.length ; ix++ ) {
			String term = terms[ix];
			double weight = ( weights == null ? 1 : weights[ix] );
			double prob = corpus_model.probability( term );
			if ( prob > 0 ) {
				sum += weight * Math.log( prob );
			}
		}
		return score + sum;
	}
	
	protected void explain( Explanation expl, BasicStats stats, int doc, float freq, float docLen ) {
		// do nothing
	}
	
	public double getLambda() {
		return lambda;
	}
	
	public String getName() {
		return String.format( Locale.ROOT, "JM(%f)", getLambda() );
	}
	
}