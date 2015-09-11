package utils.ir.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.ArrayUtils;
import utils.ir.IRSettings;

/**
 * TextAnalysis options.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TextAnalysis {
	
	private static final TextAnalysis instance = new TextAnalysis();
	private static Pattern pattern_stopwords = Pattern.compile( ".*?<word>(.+)</word>.*" );
	
	/**
	 * Load stopwords from the input.
	 * 
	 * @param instream
	 * @return
	 */
	private static Set<String> loadStopWordsAsSet( InputStream instream ) {
		try {
			Set<String> stopwords = new HashSet<String>();
			BufferedReader reader = new BufferedReader( new InputStreamReader( instream, IRSettings.CHARSET ) );
			for ( String line = reader.readLine() ; line != null ; line = reader.readLine() ) {
				Matcher m = pattern_stopwords.matcher( line );
				if ( m.matches() ) {
					stopwords.add( m.group( 1 ) );
				}
			}
			reader.close();
			return stopwords;
		} catch ( IOException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** Indri standard stopwords as a set. */
	public static final Set<String> SET_STOPWORDS_INDRI = loadStopWordsAsSet( instance.getClass().getResourceAsStream( "resources/stopwords.indri" ) );
	/** Lucene standard stopwords as a set. */
	public static final Set<String> SET_STOPWORDS_LUCENE = loadStopWordsAsSet( instance.getClass().getResourceAsStream( "resources/stopwords.lucene" ) );
	
	/** Indri standard stopwords as an array. */
	public static final String[] ARRAY_STOPWORDS_INDRI = ArrayUtils.toStringArray( SET_STOPWORDS_INDRI );
	/** Lucene standard stopwords as an array. */
	public static final String[] ARRAY_STOPWORDS_LUCENE = ArrayUtils.toStringArray( SET_STOPWORDS_LUCENE );
	
	/** An interface for text analysis processing methods. */
	public interface TextAnalysisProcessing {
	}
	
	/**
	 * Text tokenization methods.
	 */
	public enum Tokenization implements TextAnalysisProcessing {
		/** Do not tokenize the input and treate it as a whole token. */
		NO,
		/** Tokenize by any whitespace character. */
		WHITE_SPACE,
		/** Tokenize by any character that is not a letter. */
		NON_LETTER,
		/** Tokenize by any character that is neither a letter nor a digit. */
		NON_LETTER_DIGIT,
		/** Tokenize by Lucene standard tokenizer (it will keep hyphens at certain cases in order to keep entities such as urls, abbreviations, etc.). */
		LUCENE_STANDARD
	}
	
	/**
	 * Letter case processing (note that lowercase processing is required for some other processings).
	 */
	public enum LetterCase implements TextAnalysisProcessing {
		/** Keep the casing. */
		KEEP_CASE,
		/** Change to lowercase letters. */
		LOWERCASE
	}
	
	/**
	 * Stopwords removal options.
	 */
	public enum StopWords implements TextAnalysisProcessing {
		
		/** Do not remove stopwords. */
		DO_NOT_REMOVE( null, false ),
		
		/** Replacing standard indri stopwords by [OOV]. */
		INDRI_STOPWORDS( SET_STOPWORDS_INDRI, true ),
		
		/** Replacing standard lucene stopwords by [OOV]. */
		LUCENE_STOPWORDS( SET_STOPWORDS_LUCENE, true ),
		
		/** Remove standard indri stopwords. */
		INDRI_STOPWORDS_NOREPLACE( SET_STOPWORDS_INDRI, false ),
		
		/** Remove standard lucene stopwords. */
		LUCENE_STOPWORDS_NOREPLACE( SET_STOPWORDS_LUCENE, false );
		
		public Set<String> stopwords;
		public boolean replace;
		
		StopWords( Set<String> stopwords, boolean replace ) {
			this.stopwords = stopwords;
			this.replace = replace;
		}
		
	}
	
	/**
	 * Stemming options.
	 */
	public enum Stemming implements TextAnalysisProcessing {
		/** Do not stem words. */
		NO_STEMMING,
		/** Use porter stemmer. */
		PORTER,
		/** Use the krovetz stemmer in lucene.contrib */
		KROVETZ,
	}
	
	/**
	 * Stores alias for text processing methods. Feel free to add more alias you would like to use.
	 */
	static Map<String, TextAnalysisProcessing> alias;
	static {
		alias = new HashMap<String, TextAnalysisProcessing>();
		// Tokenization.NO
		alias.put( "notk", Tokenization.NO );
		alias.put( "tkno", Tokenization.NO );
		alias.put( "no tk", Tokenization.NO );
		alias.put( "tk no", Tokenization.NO );
		alias.put( "no-tk", Tokenization.NO );
		alias.put( "tk-no", Tokenization.NO );
		// Tokenization.WHITE_SPACE
		alias.put( "white", Tokenization.WHITE_SPACE );
		alias.put( "whitespace", Tokenization.WHITE_SPACE );
		// Tokenization.NON_LETTER
		alias.put( "letter", Tokenization.NON_LETTER );
		alias.put( "no letter", Tokenization.NON_LETTER );
		alias.put( "no-letter", Tokenization.NON_LETTER );
		alias.put( "non letter", Tokenization.NON_LETTER );
		alias.put( "non-letter", Tokenization.NON_LETTER );
		// Tokenization.NON_LETTER_DIGIT
		alias.put( "alpha", Tokenization.NON_LETTER_DIGIT );
		alias.put( "no alpha", Tokenization.NON_LETTER_DIGIT );
		alias.put( "no-alpha", Tokenization.NON_LETTER_DIGIT );
		alias.put( "non alpha", Tokenization.NON_LETTER_DIGIT );
		alias.put( "non-alpha", Tokenization.NON_LETTER_DIGIT );
		alias.put( "letter digit", Tokenization.NON_LETTER_DIGIT );
		alias.put( "no letter digit", Tokenization.NON_LETTER_DIGIT );
		alias.put( "non letter digit", Tokenization.NON_LETTER_DIGIT );
		alias.put( "digit letter", Tokenization.NON_LETTER_DIGIT );
		alias.put( "no digit letter", Tokenization.NON_LETTER_DIGIT );
		alias.put( "non digit letter", Tokenization.NON_LETTER_DIGIT );
		// Tokenization.LUCENE_STANDARD
		alias.put( "stdtk", Tokenization.LUCENE_STANDARD );
		alias.put( "tkstd", Tokenization.LUCENE_STANDARD );
		alias.put( "tk std", Tokenization.LUCENE_STANDARD );
		alias.put( "tk-std", Tokenization.LUCENE_STANDARD );
		alias.put( "std tk", Tokenization.LUCENE_STANDARD );
		alias.put( "std-tk", Tokenization.LUCENE_STANDARD );
		// LetterCase.KEEP_CASE
		alias.put( "nocase", LetterCase.KEEP_CASE );
		alias.put( "no case", LetterCase.KEEP_CASE );
		alias.put( "no-case", LetterCase.KEEP_CASE );
		alias.put( "keepcase", LetterCase.KEEP_CASE );
		// LetterCase.LOWERCASE
		alias.put( "lc", LetterCase.LOWERCASE );
		alias.put( "lowercase", LetterCase.LOWERCASE );
		// StopWords.DO_NOT_REMOVE
		alias.put( "nostop", StopWords.DO_NOT_REMOVE );
		alias.put( "no stop", StopWords.DO_NOT_REMOVE );
		alias.put( "no-stop", StopWords.DO_NOT_REMOVE );
		alias.put( "non stop", StopWords.DO_NOT_REMOVE );
		alias.put( "non-stop", StopWords.DO_NOT_REMOVE );
		// StopWords.INDRI_STOPWORDS
		alias.put( "stop indri", StopWords.INDRI_STOPWORDS );
		alias.put( "stop-indri", StopWords.INDRI_STOPWORDS );
		alias.put( "indri stop", StopWords.INDRI_STOPWORDS );
		alias.put( "indri-stop", StopWords.INDRI_STOPWORDS );
		alias.put( "indri stopwords", StopWords.INDRI_STOPWORDS );
		alias.put( "stopwords indri", StopWords.INDRI_STOPWORDS );
		// StopWords.LUCENE_STOPWORDS
		alias.put( "stop luc", StopWords.LUCENE_STOPWORDS );
		alias.put( "stop-luc", StopWords.LUCENE_STOPWORDS );
		alias.put( "stop lucene", StopWords.LUCENE_STOPWORDS );
		alias.put( "stop-lucene", StopWords.LUCENE_STOPWORDS );
		alias.put( "luc stop", StopWords.LUCENE_STOPWORDS );
		alias.put( "luc-stop", StopWords.LUCENE_STOPWORDS );
		alias.put( "lucene stop", StopWords.LUCENE_STOPWORDS );
		alias.put( "lucene-stop", StopWords.LUCENE_STOPWORDS );
		alias.put( "luc stopwords", StopWords.LUCENE_STOPWORDS );
		alias.put( "lucene stopwords", StopWords.LUCENE_STOPWORDS );
		alias.put( "stopwords luc", StopWords.LUCENE_STOPWORDS );
		alias.put( "stopwords lucene", StopWords.LUCENE_STOPWORDS );
		// Stemming.NO_STEMMING
		alias.put( "nostem", Stemming.NO_STEMMING );
		alias.put( "stemno", Stemming.NO_STEMMING );
		alias.put( "no stem", Stemming.NO_STEMMING );
		alias.put( "stem no", Stemming.NO_STEMMING );
		// Stemming.PORTER
		alias.put( "porter", Stemming.PORTER );
		// Stemming.KROVETZ
		alias.put( "kstem", Stemming.KROVETZ );
	}
	
	/**
	 * Get a text processing method by its alias.
	 * 
	 * @param alias
	 * @return
	 */
	public static TextAnalysisProcessing get( String alias ) {
		return TextAnalysis.alias.get( alias );
	}
	
}
