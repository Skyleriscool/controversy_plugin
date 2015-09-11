package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * Maximum likelihood estimator for unigram language models.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class MaximumLikelihood implements UnigramSampleEstimator {
	
	public double probability( UnigramSample sample, String word ) throws IOException {
		return sample.frequency( word ) / sample.length();
	}
	
}
