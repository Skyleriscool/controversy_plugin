package utils.ir.analysis;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

import utils.ir.IRSettings;

/**
 * StopwordsFilter filters a token stream and replace any stopwords into [OOV] (the replacement token is defined by <code>IRSettings.TOKEN_STOPWORDS</code>).
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class StopwordsFilter extends TokenFilter {
	
	private static char[] chars_stopword = IRSettings.TOKEN_STOPWORDS.toCharArray();
	
	private final CharArraySet stopwords;
	private final CharTermAttribute term_attr = addAttribute( CharTermAttribute.class );
	
	/**
	 * Create a StopwordsFilter by specifying a set of stopwords.
	 * 
	 * @param input
	 * @param stopwords
	 */
	public StopwordsFilter( TokenStream input, Set<?> stopwords ) {
		super( input );
		this.stopwords = stopwords instanceof CharArraySet ? (CharArraySet) stopwords : new CharArraySet( stopwords, false );
	}
	
	public final boolean incrementToken() throws IOException {
		while ( input.incrementToken() ) {
			if ( isStopword() ) {
				term_attr.copyBuffer( chars_stopword, 0, chars_stopword.length );
				term_attr.setLength( chars_stopword.length );
			}
			return true;
		}
		return false;
	}
	
	protected boolean isStopword() throws IOException {
		return stopwords.contains( term_attr.buffer(), 0, term_attr.length() );
	}
	
}
