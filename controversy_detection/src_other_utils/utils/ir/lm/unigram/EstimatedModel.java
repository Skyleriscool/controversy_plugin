package utils.ir.lm.unigram;

import java.io.IOException;
import java.util.Iterator;

/**
 * EstimatedModel is an implementation of UnigramModel by online estimating models from samples.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class EstimatedModel implements UnigramModel {
	
	protected UnigramSample sample;
	protected UnigramSampleEstimator estimator;
	
	public EstimatedModel( UnigramSample sample, UnigramSampleEstimator estimator ) {
		this.sample = sample;
		this.estimator = estimator;
	}
	
	public Iterator<String> iterator() {
		return sample.iterator();
	}
	
	public double probability( String word ) throws IOException {
		return estimator.probability( sample, word );
	}
	
	public long sizeVocabulary() throws IOException {
		return sample.sizeVocabulary();
	}
	
	/**
	 * Estimate a model from the given sample using maximum likelihood estimation.
	 * 
	 * @param sample
	 * @return
	 */
	public static UnigramModel MLE( UnigramSample sample ) {
		return new EstimatedModel( sample, new MaximumLikelihood() );
	}
	
	/**
	 * Estimate a model from the given sample using maximum likelihood estimation with JM smoothing.
	 * 
	 * @param sample
	 * @param bgmodel
	 * @param lambda
	 * @return
	 */
	public static UnigramModel JMSmoothing( UnigramSample sample, UnigramModel bgmodel, double lambda ) {
		return new EstimatedModel( sample, new JelinekMercerSmoothing( bgmodel, lambda ) );
	}
	
	/**
	 * Estimate a model from the given sample using maximum likelihood estimation with Dirichlet smoothing.
	 * 
	 * @param sample
	 * @param bgmodel
	 * @param miu
	 * @return
	 */
	public static UnigramModel DirichletSmoothing( UnigramSample sample, UnigramModel bgmodel, double miu ) {
		return new EstimatedModel( sample, new DirichletSmoothing( bgmodel, miu ) );
	}
	
}
