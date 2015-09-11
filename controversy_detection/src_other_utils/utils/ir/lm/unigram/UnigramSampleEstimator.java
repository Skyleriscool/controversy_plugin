package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * Interface for different estimators of unigram language model.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public interface UnigramSampleEstimator {
	
	public double probability( UnigramSample sample, String word ) throws IOException;
	
}
