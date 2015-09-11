package utils.ir.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import utils.ArrayUtils;
import utils.ir.analysis.TextAnalysis.Stemming;
import utils.ir.analysis.TextAnalysis.StopWords;
import utils.ir.analysis.TextAnalysis.LetterCase;
import utils.ir.analysis.TextAnalysis.Tokenization;
import utils.ir.analysis.TextAnalysis.TextAnalysisProcessing;

/**
 * TextAnalyzer is used to constructor various analyzers in Lucene.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TextAnalyzer extends Analyzer {
	
	public Stemming stemming;
	public StopWords stopWords;
	public LetterCase letterCase;
	public Tokenization tokenization;
	
	/**
	 * Get a text analyzer by specifying a list of text analyze processing methods.
	 * 
	 * @param procs
	 * @return
	 */
	public static TextAnalyzer get( TextAnalysisProcessing... procs ) {
		TextAnalyzer analyzer = new TextAnalyzer();
		for ( TextAnalysisProcessing proc : procs ) {
			// note that if any proc is null, a null exception will be thrown here
			// this is to make sure that get(String... procs) make uses valid alias.
			if ( proc instanceof Stemming ) {
				analyzer.stemming = (Stemming) proc;
			} else if ( proc instanceof StopWords ) {
				analyzer.stopWords = (StopWords) proc;
			} else if ( proc instanceof LetterCase ) {
				analyzer.letterCase = (LetterCase) proc;
			} else if ( proc instanceof Tokenization ) {
				analyzer.tokenization = (Tokenization) proc;
			}
		}
		return analyzer;
	}
	
	/**
	 * Get a text analyzer by specifying a list of text analyze processing methods.
	 * 
	 * @param tokenization
	 * @param letterCase
	 * @param stopWords
	 * @param stemming
	 * @return
	 */
	public static TextAnalyzer get( Tokenization tokenization, LetterCase letterCase, StopWords stopWords, Stemming stemming ) {
		TextAnalyzer analyzer = new TextAnalyzer();
		analyzer.stemming = stemming;
		analyzer.stopWords = stopWords;
		analyzer.letterCase = letterCase;
		analyzer.tokenization = tokenization;
		return analyzer;
	}
	
	/**
	 * <p>
	 * Get a text analyzer by specifying a list of text analyze processing methods by alias. Accepted alias include:;
	 * </p>
	 * <ul>
	 * <li>Tokenization methods:
	 * <ul>
	 * <li>No tokenization: "no tk", "tk no", "notk", "tkno".</li>
	 * <li>Tokenize by whitespace: "white", "whitespace".</li>
	 * <li>Tokenize by any non-letter character: "letter", "no letter", "non letter".</li>
	 * <li>Tokenize by any character that is neither a letter nor a digit: "letter digit", "no letter digit", "digit letter", "alpha", "no alpha", "non alpha".</li>
	 * <li>Tokenize by Lucene standard tokenizer: "std tk".</li>
	 * </ul>
	 * </li>
	 * <li>Letter case normalization:
	 * <ul>
	 * <li>Keep letter case: "nocase", "no case", "keepcase".</li>
	 * <li>Normalize to lower case: "lc", "lowercase".</li>
	 * </ul>
	 * </li>
	 * <li>Stopwords removal methods:
	 * <ul>
	 * <li>Do not remove stopwords: "nostop", "no stop", "non stop".</li>
	 * <li>Use indri's stopwords: "indri stop", "stop indri".</li>
	 * <li>Use lucene's stopwords: "luc stop", "stop luc", "lucene stop", "stop lucene".</li>
	 * <li>If you would not like stopwords to be replaced by [OOV], put an extra field as one of the following: "norep", "no rep", "nooov", "no oov",
	 * "noreplace", "no replace".</li>
	 * </ul>
	 * </li>
	 * <li>Stemming:
	 * <ul>
	 * <li>Do not stem words: "nostem", "stemno", "no stem", "stem no".</li>
	 * <li>Krovetz stemming: "kstem".</li>
	 * <li>Indri Krovetz stemming: "indri kstem", "kstem indri".</li>
	 * <li>Porter stemming: "porter".</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param alias
	 * @return
	 */
	public static TextAnalyzer get( String... alias ) {
		boolean replace_stopwords = true;
		TextAnalysisProcessing[] procs = new TextAnalysisProcessing[alias.length];
		for ( int ix = 0 ; ix < alias.length ; ix++ ) {
			procs[ix] = TextAnalysis.get( alias[ix] );
			String proc = alias[ix].trim().toLowerCase().replaceAll( "\\s+", " " );
			if ( proc.equals( "norep" ) || proc.equals( "no rep" ) || proc.equals( "noreplace" ) || proc.equals( "no replace" ) || proc.equals( "nooov" ) || proc.equals( "no oov" ) ) {
				replace_stopwords = false;
			}
		}
		if ( !replace_stopwords ) {
			for ( int ix = 0 ; ix < procs.length ; ix++ ) {
				TextAnalysisProcessing proc = procs[ix];
				if ( proc != null && proc instanceof StopWords ) {
					if ( proc == StopWords.INDRI_STOPWORDS ) {
						procs[ix] = StopWords.INDRI_STOPWORDS_NOREPLACE;
					}
					if ( proc == StopWords.LUCENE_STOPWORDS ) {
						procs[ix] = StopWords.LUCENE_STOPWORDS_NOREPLACE;
					}
				}
			}
		}
		return get( procs );
	}
	
	public TokenStreamComponents createComponents( String fieldName, Reader reader ) {
		
		TokenStreamComponents ts = null;
		
		if ( tokenization == null || tokenization == Tokenization.NO ) {
			ts = new TokenStreamComponents( new KeywordTokenizer( reader ) );
		} else if ( tokenization == Tokenization.WHITE_SPACE ) {
			ts = new TokenStreamComponents( new WhitespaceTokenizer( reader ) );
		} else if ( tokenization == Tokenization.NON_LETTER ) {
			ts = new TokenStreamComponents( new LetterTokenizer( reader ) );
		} else if ( tokenization == Tokenization.NON_LETTER_DIGIT ) {
			ts = new TokenStreamComponents( new LetterOrDigitTokenizer( reader ) );
		} else if ( tokenization == Tokenization.LUCENE_STANDARD ) {
			ts = new TokenStreamComponents( new StandardTokenizer( reader ) );
		}
		
		if ( letterCase == LetterCase.LOWERCASE ) {
			ts = new TokenStreamComponents( ts.getTokenizer(), new LowerCaseFilter( ts.getTokenStream() ) );
		}
		
		if ( stopWords != null && stopWords != StopWords.DO_NOT_REMOVE ) {
			if ( stopWords == StopWords.INDRI_STOPWORDS || stopWords == StopWords.LUCENE_STOPWORDS ) {
				ts = new TokenStreamComponents( ts.getTokenizer(), new StopwordsFilter( ts.getTokenStream(), stopWords.stopwords ) );
			} else if ( stopWords == StopWords.INDRI_STOPWORDS_NOREPLACE || stopWords == StopWords.LUCENE_STOPWORDS_NOREPLACE ) {
				ts = new TokenStreamComponents( ts.getTokenizer(), new StopFilter( ts.getTokenStream(), StopFilter.makeStopSet( ArrayUtils.toStringArray( stopWords.stopwords ) ) ) );
			}
		}
		
		if ( stemming == Stemming.PORTER ) {
			ts = new TokenStreamComponents( ts.getTokenizer(), new PorterStemFilter( ts.getTokenStream() ) );
		} else if ( stemming == Stemming.KROVETZ ) {
			ts = new TokenStreamComponents( ts.getTokenizer(), new KStemFilter( ts.getTokenStream() ) );
		}
		
		return ts;
		
	}
	
}
