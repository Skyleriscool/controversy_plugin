package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * DirichletSmoothing estimates a unigram language model using Dirichlet smoothing.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class DirichletSmoothing implements UnigramSampleEstimator {
	
	private UnigramModel bgmodel;
	private double miu;
	
	public DirichletSmoothing( UnigramModel bgmodel, double miu ) {
		this.bgmodel = bgmodel;
		this.miu = miu;
	}
	
	public double probability( UnigramSample sample, String word ) throws IOException {
		double tf = sample.frequency( word );
		double len = sample.length();
		double pbc = miu == 0 ? 0 : bgmodel.probability( word );
		double tf_smoothed = tf + pbc * miu;
		double len_smoothed = len + miu;
		return tf_smoothed / len_smoothed;
	}
	
}
