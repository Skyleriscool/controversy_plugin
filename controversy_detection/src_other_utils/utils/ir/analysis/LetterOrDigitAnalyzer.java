package utils.ir.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;

/**
 * Analyzer that tokenizes at charaters that are neither letter nor digit.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class LetterOrDigitAnalyzer extends Analyzer {
	
	public LetterOrDigitAnalyzer() {
		// DO NOTHING?
		super();
	}
	
	protected TokenStreamComponents createComponents( String fieldName, Reader reader ) {
		return new TokenStreamComponents( new LetterOrDigitTokenizer( reader ) );
	}
	
}
