package utils.ir.analysis;

import java.util.List;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import utils.ArrayUtils;
import utils.ir.IRSettings;
import utils.ir.lm.unigram.TreeMapSample;

/**
 * Utility functions related to text analysis.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class AnalyzerUtils {
	
	/**
	 * Process the input text using the provided text analyzer and return result string.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static String analyze( String input, Analyzer analyzer ) throws IOException {
		return analyze( input, analyzer, true );
	}
	
	/**
	 * Process the input text using the provided text analyzer and return result string (each token is separated using a whitespace).
	 * 
	 * @param input
	 * @param analyzer
	 * @param keepOOV
	 * @return
	 * @throws IOException
	 */
	public static String analyze( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		StringBuilder sb = new StringBuilder();
		TokenStream ts = analyzer.tokenStream( "", new StringReader( input ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				if ( keepOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
					sb.append( attr.toString() );
				}
			}
			ts.end();
		} finally {
			ts.close();
		}
		return sb.toString();
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return the result tokens. By default, [OOV] will also be stored in the output.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static List<String> tokenize( String input, Analyzer analyzer ) throws IOException {
		return tokenize( input, analyzer, true );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return a list of tokens.
	 * 
	 * @param input
	 * @param analyzer
	 * @param keepOOV
	 * @return
	 * @throws IOException
	 */
	public static List<String> tokenize( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		List<String> tokens = new ArrayList<String>();
		TokenStream ts = analyzer.tokenStream( "", new StringReader( input ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				if ( keepOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
					tokens.add( attr.toString() );
				}
			}
			ts.end();
		} finally {
			ts.close();
		}
		return tokens;
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return an array of tokens. By default, [OOV] will also be stored in the output.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static String[] tokenizeAsArray( String input, Analyzer analyzer ) throws IOException {
		return tokenizeAsArray( input, analyzer, true );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return an array of tokens.
	 * 
	 * @param input
	 * @param analyzer
	 * @param keepOOV
	 * @return
	 * @throws IOException
	 */
	public static String[] tokenizeAsArray( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		return ArrayUtils.toStringArray( tokenize( input, analyzer, keepOOV ) );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return the result tokens. By default, [OOV] will also be stored in the output.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> tokenizeAsHashSet( String input, Analyzer analyzer ) throws IOException {
		return tokenizeAsHashSet( input, analyzer, true );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return the result tokens.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> tokenizeAsHashSet( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		HashSet<String> tokens = new HashSet<String>();
		TokenStream ts = analyzer.tokenStream( "", new StringReader( input ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				if ( keepOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
					tokens.add( attr.toString() );
				}
			}
			ts.end();
		} finally {
			ts.close();
		}
		return tokens;
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return the result tokens. By default, [OOV] will also be stored in the output.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static TreeSet<String> tokenizeAsTreeSet( String input, Analyzer analyzer ) throws IOException {
		return tokenizeAsTreeSet( input, analyzer, true );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and return the result tokens.
	 * 
	 * @param input
	 * @param analyzer
	 * @param keepOOV
	 * @return
	 * @throws IOException
	 */
	public static TreeSet<String> tokenizeAsTreeSet( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		TreeSet<String> tokens = new TreeSet<String>();
		TokenStream ts = analyzer.tokenStream( "", new StringReader( input ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				if ( keepOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
					tokens.add( attr.toString() );
				}
			}
			ts.end();
		} finally {
			ts.close();
		}
		return tokens;
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and store as a unigram sample.
	 * 
	 * @param input
	 * @param analyzer
	 * @return
	 * @throws IOException
	 */
	public static TreeMapSample tokenizeAsUnigramSample( String input, Analyzer analyzer ) throws IOException {
		return tokenizeAsUnigramSample( input, analyzer, true );
	}
	
	/**
	 * Tokenize the input text using the provided text analyzer and store as a unigram sample.
	 * 
	 * @param input
	 * @param analyzer
	 * @param keepOOV
	 * @return
	 * @throws IOException
	 */
	public static TreeMapSample tokenizeAsUnigramSample( String input, Analyzer analyzer, boolean keepOOV ) throws IOException {
		TreeMapSample sample = new TreeMapSample();
		TokenStream ts = analyzer.tokenStream( "", new StringReader( input ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				if ( keepOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
					sample.update( attr.toString(), 1.0 );
				}
			}
			ts.end();
		} finally {
			ts.close();
		}
		sample.length();
		return sample;
	}
	
}
