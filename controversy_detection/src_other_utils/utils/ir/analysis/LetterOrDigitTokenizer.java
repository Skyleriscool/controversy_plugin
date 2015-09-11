package utils.ir.analysis;

import java.io.Reader;

import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * Tokenizer that tokenizes at charaters that are neither letter nor digit.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class LetterOrDigitTokenizer extends CharTokenizer {
	
	public LetterOrDigitTokenizer( Reader in ) {
		super( in );
	}
	
	public LetterOrDigitTokenizer( AttributeFactory factory, Reader in ) {
		super( factory, in );
	}
	
	protected boolean isTokenChar( int c ) {
		return Character.isLetterOrDigit( c );
	}
	
}
