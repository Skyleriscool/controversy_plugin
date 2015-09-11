package utils.ir.lm.unigram;

import java.io.IOException;

/**
 * UnigramSample defines an interface of accessing statistics of an observed linguistic sample.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public interface UnigramSample extends Iterable<String> {
	
	/**
	 * The frequency of the specified word.
	 * 
	 * @param word
	 * @return
	 * @throws IOException
	 */
	public double frequency( String word ) throws IOException;
	
	/**
	 * The size of the sample (summarizing all word frequencies).
	 * 
	 * @return
	 * @throws IOException
	 */
	public double length() throws IOException;
	
	/**
	 * Size of the vocabulary.
	 * 
	 * @return
	 * @throws IOException
	 */
	public long sizeVocabulary() throws IOException;
	
}
