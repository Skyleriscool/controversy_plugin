package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * JelinekMercerSmoothing estimates a unigram language model using Jelinek-Mercer smoothing.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class JelinekMercerSmoothing implements UnigramSampleEstimator {
	
	private UnigramModel bgmodel;
	private double lambda;
	
	/**
	 * Create an estimator using maximum likelihood with Jelinek-Mercer smoothing.
	 * 
	 * @param bgmodel
	 * @param lambda
	 */
	public JelinekMercerSmoothing( UnigramModel bgmodel, double lambda ) {
		this.bgmodel = bgmodel;
		this.lambda = lambda;
	}
	
	public double probability( UnigramSample sample, String word ) throws IOException {
		double tf = sample.frequency( word );
		double length = sample.length();
		double prob = tf / length;
		double prob_bg = bgmodel.probability( word );
		return prob * ( 1 - lambda ) + prob_bg * lambda;
	}
	
}
