package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * UnigramModel is an interface for unigram language model.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public interface UnigramModel extends Iterable<String> {
	
	/**
	 * The probability of the specified word.
	 * 
	 * @param word
	 * @return
	 * @throws IOException
	 */
	public double probability( String word ) throws IOException;
	
	/**
	 * Size of the vocabulary.
	 * 
	 * @return
	 * @throws IOException
	 */
	public long sizeVocabulary() throws IOException;
	
}
