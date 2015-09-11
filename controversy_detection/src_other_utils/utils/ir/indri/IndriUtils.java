package utils.ir.indri;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Formatter;

import org.apache.lucene.analysis.Analyzer;

import lemurproject.indri.DocumentVector;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

import utils.ArrayUtils;
import utils.ir.IRSettings;
import utils.ir.eval.SearchResult;
import utils.ir.eval.SearchResults;
import utils.ir.analysis.TextAnalysis;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.analysis.AnalyzerUtils;
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lm.unigram.UnigramSample;
import utils.ir.lm.unigram.TreeMapSample;
import utils.ir.lm.unigram.EstimatedModel;

/**
 * Utilities related to Indri.
 * 
 * @author Jiepu Jiang
 * @version Feb 17, 2015
 */
public class IndriUtils {
	
	/**
	 * Get a unigram sample from an indri document (including all fields). It returns null if the document with the specified id do not exist.
	 * 
	 * @param vect
	 * @param ignoreOOV
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( DocumentVector vect, boolean ignoreOOV ) throws Exception {
		TreeMapSample docsample = new TreeMapSample();
		String[] stems = vect.stems;
		int[] positions = vect.positions;
		for ( int ix = 0 ; ix < positions.length ; ix++ ) {
			String term = stems[positions[ix]];
			if ( !ignoreOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
				docsample.update( term, 1.0 );
			}
		}
		docsample.setLength();
		return docsample;
	}
	
	/**
	 * Get a unigram sample from an indri document field. It returns null if the document with the specified id do not exist.
	 * 
	 * @param vect
	 * @param field
	 * @param ignoreOOV
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( DocumentVector vect, String field, boolean ignoreOOV ) throws Exception {
		TreeMapSample docsample = new TreeMapSample();
		String[] stems = vect.stems;
		int[] positions = vect.positions;
		DocumentVector.Field[] fields = vect.fields;
		for ( DocumentVector.Field fd : fields ) {
			if ( fd.name.equalsIgnoreCase( field ) ) {
				for ( int ix = fd.begin ; ix < fd.end ; ix++ ) {
					String term = stems[positions[ix]];
					if ( !ignoreOOV || !term.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
						docsample.update( term, 1.0 );
					}
				}
			}
		}
		docsample.setLength();
		return docsample;
	}
	
	/**
	 * Get a unigram sample from an indri document (including all fields). It returns null if the document with the specified id do not exist.
	 * 
	 * @param index
	 * @param docid
	 * @param ignoreOOV
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( QueryEnvironment index, int docid, boolean ignoreOOV ) throws Exception {
		DocumentVector[] vects = index.documentVectors( new int[] { docid } );
		if ( vects != null && vects.length >= 1 ) {
			return getDocSample( vects[0], ignoreOOV );
		}
		return null;
	}
	
	/**
	 * Get a unigram sample from an indri document field. It returns null if the document with the specified id do not exist.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @param ignoreOOV
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( QueryEnvironment index, int docid, String field, boolean ignoreOOV ) throws Exception {
		DocumentVector[] vects = index.documentVectors( new int[] { docid } );
		if ( vects != null && vects.length >= 1 ) {
			return getDocSample( vects[0], field, ignoreOOV );
		}
		return null;
	}
	
	/**
	 * Get a unigram sample from an indri document (including all fields). It returns null if the document with the specified id do not exist. It ignores [OOV].
	 * 
	 * @param index
	 * @param docid
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( QueryEnvironment index, int docid ) throws Exception {
		return getDocSample( index, docid, true );
	}
	
	/**
	 * Get a unigram sample from an indri document field. It returns null if the document with the specified id do not exist. It ignores [OOV].
	 * 
	 * @param index
	 * @param docid
	 * @param ignoreOOV
	 * @return
	 * @throws Exception
	 */
	public static TreeMapSample getDocSample( QueryEnvironment index, int docid, String field ) throws Exception {
		return getDocSample( index, docid, field, true );
	}
	
	/**
	 * Get SearchResults object based on Indri's search results.
	 * 
	 * @param indri_results
	 * @param docno
	 */
	public static SearchResults getSearchResults( ScoredExtentResult[] indri_results, String[] docno ) {
		SearchResults results = new SearchResults();
		for ( int ix = 0 ; ix < indri_results.length ; ix++ ) {
			results.add( new SearchResult( indri_results[ix].document, docno[ix], indri_results[ix].score ) );
		}
		return results;
	}
	
	/**
	 * Set dirichlet smoothing parameter of the index to mu.
	 * 
	 * @param index
	 * @param mu
	 * @throws Exception
	 */
	public static void setDirichletSmoothing( QueryEnvironment index, int mu ) throws Exception {
		index.setScoringRules( new String[] {
				"method:dirichlet,mu:" + mu
		} );
	}
	
	/**
	 * Tokenize the query terms and create an indri #combine query. It returns null if the query string does not contain any meaningful words after
	 * tokenization.
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static String getIndriQuery( String query ) throws IOException {
		String[] tokens = parseTokens( query );
		if ( countNonStopTokens( tokens ) > 0 ) {
			return getCombineQuery( tokens );
		}
		return null;
	}
	
	/**
	 * Create an indri #weight query using the term probabilities. It returns null no words in the unigram model has a positive probability.
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	public static String getIndriQuery( UnigramModel model ) throws IOException {
		List<String> tokens = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for ( String token : model ) {
			if ( !token.equalsIgnoreCase( IRSettings.TOKEN_STOPWORDS ) ) {
				double probability = model.probability( token );
				if ( probability > 0 ) {
					tokens.add( token );
					weights.add( probability );
				}
			}
		}
		if ( tokens.size() > 0 ) {
			return getWeightQuery( ArrayUtils.toStringArray( tokens ), ArrayUtils.toDoubleArray( weights ) );
		}
		return null;
	}
	
	/**
	 * Create an indri #weight query using the term probabilities. It returns null no words in the unigram model has a positive probability.
	 * 
	 * @param sample
	 * @return
	 * @throws IOException
	 */
	public static String getIndriQuery( UnigramSample sample ) throws IOException {
		return getIndriQuery( EstimatedModel.MLE( sample ) );
	}
	
	/**
	 * Create an indri #weight query by weighting other indri queries.
	 * 
	 * @param indri_queries
	 * @param weights
	 * @return
	 */
	public static String getIndriWeightedQuery( String[] indri_queries, double[] weights ) {
		if ( indri_queries.length == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#weight(" );
		for ( int ix = 0 ; ix < indri_queries.length ; ix++ ) {
			if ( weights[ix] > 0 ) {
				fmt.format( " %.8f %s", weights[ix], indri_queries[ix] );
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	private static final Analyzer analyzer = TextAnalyzer.get( "white" );
	
	/**
	 * <p>
	 * Parse the query into an array of tokens by the following process:
	 * </p>
	 * <ol>
	 * <li>replace "u.s." by "us" (note that this abbreviation is only one of those which would be translated in indri, we ignore others here just because there
	 * are no full list of the abbreviations and "u.s." is prevelant in TREC queries);</li>
	 * <li>replace characters that are not numbers or English letters by white space;</li>
	 * <li>replace indri standard stopwords into &lt;STOP&gt;.</li>
	 * </ol>
	 * <p>
	 * For example, a sentence such as "u.s. president obama's family-tree and clinton's" will be processed into the following tokens:
	 * </p>
	 * 
	 * <pre>
	 * token[0]: us
	 * token[1]: president
	 * token[2]: obama
	 * token[3]: s
	 * token[4]: family
	 * token[5]: tree
	 * token[6]: &lt;oov&gt;
	 * token[7]: clinton
	 * token[8]: s
	 * </pre>
	 * <p>
	 * The returned tokens can be used for getQueryXXX() methods.
	 * </p>
	 * 
	 * @param org_query
	 * @return
	 * @throws IOException
	 */
	public static String[] parseTokens( String org_query ) throws IOException {
		org_query = org_query.toLowerCase();
		org_query = org_query.replaceAll( "u\\.s\\.", "1234567890us1234567890" );
		org_query = org_query.replaceAll( "<stop>", "1234567890stopwords1234567890" );
		org_query = org_query.replaceAll( "<oov>", "1234567890stopwords1234567890" );
		org_query = org_query.replaceAll( "[^0-9a-zA-Z]", " " );
		String[] tokens = AnalyzerUtils.tokenizeAsArray( org_query, analyzer );
		for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
			if ( tokens[ix].equals( "1234567890us1234567890" ) ) {
				tokens[ix] = "us";
			} else if ( TextAnalysis.SET_STOPWORDS_INDRI.contains( tokens[ix] ) || tokens[ix].equals( "1234567890stopwords1234567890" ) ) {
				tokens[ix] = "<oov>";
			}
		}
		return tokens;
	}
	
	private static int countNonStopTokens( String[] tokens ) {
		int count = 0;
		if ( tokens != null ) {
			for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
				if ( !tokens[ix].equalsIgnoreCase( "<STOP>" ) && !tokens[ix].equalsIgnoreCase( "<oov>" ) ) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Create a #combine indri query for the list of tokens (by default the tokens are not stemmed and default field will be used).
	 * 
	 * @param tokens
	 * @return
	 */
	public static String getCombineQuery( String[] tokens ) {
		return getCombineQuery( tokens, (String[]) null, false );
	}
	
	/**
	 * Create a #combine indri query for the list of tokens (use default field).
	 * 
	 * @param tokens
	 * @param stemmed
	 * @return
	 */
	public static String getCombineQuery( String[] tokens, boolean stemmed ) {
		return getCombineQuery( tokens, (String[]) null, stemmed );
	}
	
	/**
	 * Create a #combine indri query for the list of tokens (by default the tokens are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Field of the tokens.
	 * @return An indri #combine query.
	 */
	public static String getCombineQuery( String[] tokens, String field ) {
		return getCombineQuery( tokens, field, false );
	}
	
	/**
	 * Create a #combine indri query for the list of tokens (by default the tokens are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @return An indri #combine query.
	 */
	public static String getCombineQuery( String[] tokens, String[] fields ) {
		return getCombineQuery( tokens, fields, false );
	}
	
	/**
	 * Create a #combine indri query for the list of tokens.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Field of the tokens.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri #combine query.
	 */
	public static String getCombineQuery( String[] tokens, String field, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#combine(" );
		for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
			if ( !tokens[ix].equalsIgnoreCase( "<STOP>" ) && !tokens[ix].equalsIgnoreCase( "<oov>" ) ) {
				String tk = tokens[ix];
				if ( stemmed ) {
					tk = "\"" + tk + "\"";
				}
				if ( field != null ) {
					tk = tk + ".(" + field + ")";
				}
				fmt.format( " %s", tk );
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Create a #combine indri query for the list of tokens.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri #combine query.
	 */
	public static String getCombineQuery( String[] tokens, String[] fields, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#combine(" );
		for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
			if ( !tokens[ix].equalsIgnoreCase( "<STOP>" ) && !tokens[ix].equalsIgnoreCase( "<oov>" ) ) {
				String tk = tokens[ix];
				if ( stemmed ) {
					tk = "\"" + tk + "\"";
				}
				if ( fields != null ) {
					tk = tk + ".(" + fields[ix] + ")";
				}
				fmt.format( " %s", tk );
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Create a #weight indri query for the list of tokens (by default terms are not stemmed and default field will be used).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param child_nodes_weights
	 *            Weights of the tokens.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, double[] weight ) {
		return getWeightQuery( tokens, (String[]) null, weight, false );
	}
	
	/**
	 * Create a #weight indri query for the list of tokens (will use default field).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param child_nodes_weights
	 *            Weights of the tokens.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, double[] weight, boolean stemmed ) {
		return getWeightQuery( tokens, (String[]) null, weight, stemmed );
	}
	
	/**
	 * Create a #weight indri query for the list of tokens (by default the tokens are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Field of the tokens.
	 * @param child_nodes_weights
	 *            Weights of the tokens.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, String field, double[] weight ) {
		return getWeightQuery( tokens, field, weight, false );
	}
	
	/**
	 * Create a #weight indri query for the list of tokens (by default the tokens are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @param child_nodes_weights
	 *            Weights of the tokens.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, String[] fields, double[] weight ) {
		return getWeightQuery( tokens, fields, weight, false );
	}
	
	/**
	 * Create a #weight indri query for the list of tokens.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Field of the tokens.
	 * @param weights
	 *            Weights of the tokens.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, String field, double[] weights, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#weight(" );
		for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
			if ( !tokens[ix].equalsIgnoreCase( "<STOP>" ) && !tokens[ix].equalsIgnoreCase( "<oov>" ) ) {
				String tk = tokens[ix];
				if ( stemmed ) {
					tk = "\"" + tk + "\"";
				}
				if ( field != null ) {
					tk = tk + ".(" + field + ")";
				}
				if ( weights != null ) {
					fmt.format( " %.8f %s", weights[ix], tk );
				}
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Create a #weight indri query for the list of tokens.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @param weights
	 *            Weights of the tokens.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri #weight query.
	 */
	public static String getWeightQuery( String[] tokens, String[] fields, double[] weights, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#weight(" );
		for ( int ix = 0 ; ix < tokens.length ; ix++ ) {
			if ( !tokens[ix].equalsIgnoreCase( "<STOP>" ) && !tokens[ix].equalsIgnoreCase( "<oov>" ) ) {
				String tk = tokens[ix];
				if ( stemmed ) {
					tk = "\"" + tk + "\"";
				}
				if ( fields != null ) {
					tk = tk + ".(" + fields[ix] + ")";
				}
				if ( weights != null ) {
					fmt.format( " %.8f %s", weights[ix], tk );
				}
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases (by default all terms are not stemmed and will use
	 * default field).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, double weight_t, double weight_o, double weight_u ) {
		return getSDMQuery( tokens, (String[]) null, weight_t, weight_o, weight_u, false );
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases (by default all terms are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, String[] fields, double weight_t, double weight_o, double weight_u ) {
		return getSDMQuery( tokens, fields, weight_t, weight_o, weight_u, false );
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases (by default all terms are not stemmed).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Fields of the tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, String field, double weight_t, double weight_o, double weight_u ) {
		return getSDMQuery( tokens, field, weight_t, weight_o, weight_u, false );
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases (term will use default fields).
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, double weight_t, double weight_o, double weight_u, boolean stemmed ) {
		return getSDMQuery( tokens, (String[]) null, weight_t, weight_o, weight_u, stemmed );
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param field
	 *            Fields of the tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, String field, double weight_t, double weight_o, double weight_u, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#weight(" );
		{
			// an #combine query for all single terms
			String qt = getCombineQuery( tokens, field, stemmed );
			if ( qt != null && qt.trim().length() > 0 ) {
				fmt.format( " %.8f %s", weight_t, qt );
			}
		}
		{
			// an #combine query for all #1 phrases
			if ( countNonStopTokens( tokens ) > 1 ) {
				fmt.format( " %.8f #combine(", weight_o );
				for ( int ix = 0 ; ix < tokens.length - 1 ; ix++ ) {
					String w1 = tokens[ix];
					String w2 = tokens[ix + 1];
					if ( !w1.equalsIgnoreCase( "<STOP>" ) && !w1.equalsIgnoreCase( "<oov>" ) && !w2.equalsIgnoreCase( "<STOP>" ) && !w2.equalsIgnoreCase( "<oov>" ) ) {
						String tk1 = w1;
						String tk2 = w2;
						if ( stemmed ) {
							tk1 = "\"" + tk1 + "\"";
							tk2 = "\"" + tk2 + "\"";
						}
						if ( field != null ) {
							tk1 = tk1 + ".(" + field + ")";
							tk2 = tk2 + ".(" + field + ")";
						}
						fmt.format( " #1( %s %s )", tk1, tk2 );
					}
				}
				fmt.format( " )" );
			}
		}
		{
			// an #combine query for all #uw8 phrases
			if ( countNonStopTokens( tokens ) > 1 ) {
				fmt.format( " %.8f #combine(", weight_u );
				for ( int ix = 0 ; ix < tokens.length - 1 ; ix++ ) {
					String w1 = tokens[ix];
					String w2 = tokens[ix + 1];
					if ( !w1.equalsIgnoreCase( "<STOP>" ) && !w1.equalsIgnoreCase( "<oov>" ) && !w2.equalsIgnoreCase( "<STOP>" ) && !w2.equalsIgnoreCase( "<oov>" ) ) {
						String tk1 = w1;
						String tk2 = w2;
						if ( stemmed ) {
							tk1 = "\"" + tk1 + "\"";
							tk2 = "\"" + tk2 + "\"";
						}
						if ( field != null ) {
							tk1 = tk1 + ".(" + field + ")";
							tk2 = tk2 + ".(" + field + ")";
						}
						fmt.format( " #uw8( %s %s )", tk1, tk2 );
					}
				}
				fmt.format( " )" );
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Create an indri sequential dependence model including single terms, #1 phrases, and #uw8 phrases.
	 * 
	 * @param tokens
	 *            A list of tokens.
	 * @param fields
	 *            Fields of the tokens.
	 * @param weight_t
	 *            Weight of single terms.
	 * @param weight_o
	 *            Weight of #1 phrases.
	 * @param weight_u
	 *            Weight of #uw8 phrases.
	 * @param stemmed
	 *            Whether the tokens are stemmed or not.
	 * @return An indri sequential dependence model query.
	 */
	public static String getSDMQuery( String[] tokens, String[] fields, double weight_t, double weight_o, double weight_u, boolean stemmed ) {
		if ( countNonStopTokens( tokens ) == 0 ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "#weight(" );
		{
			// an #combine query for all single terms
			String qt = getCombineQuery( tokens, fields, stemmed );
			if ( qt != null && qt.trim().length() > 0 ) {
				fmt.format( " %.8f %s", weight_t, qt );
			}
		}
		{
			// an #combine query for all #1 phrases
			if ( countNonStopTokens( tokens ) > 1 ) {
				Formatter fmt_o = new Formatter();
				for ( int ix = 0 ; ix < tokens.length - 1 ; ix++ ) {
					String w1 = tokens[ix];
					String w2 = tokens[ix + 1];
					if ( !w1.equalsIgnoreCase( "<STOP>" ) && !w1.equalsIgnoreCase( "<oov>" ) && !w2.equalsIgnoreCase( "<STOP>" ) && !w2.equalsIgnoreCase( "<oov>" ) ) {
						String tk1 = w1;
						String tk2 = w2;
						if ( stemmed ) {
							tk1 = "\"" + tk1 + "\"";
							tk2 = "\"" + tk2 + "\"";
						}
						if ( fields != null ) {
							tk1 = tk1 + ".(" + fields[ix] + ")";
							tk2 = tk2 + ".(" + fields[ix + 1] + ")";
						}
						fmt_o.format( " #1( %s %s )", tk1, tk2 );
					}
				}
				String q_o = fmt_o.toString();
				fmt_o.close();
				if ( q_o.trim().length() > 0 ) {
					fmt.format( " %.8f #combine(%s )", weight_o, q_o );
				}
			}
		}
		{
			// an #combine query for all #uw8 phrases
			if ( countNonStopTokens( tokens ) > 1 ) {
				Formatter fmt_o = new Formatter();
				for ( int ix = 0 ; ix < tokens.length - 1 ; ix++ ) {
					String w1 = tokens[ix];
					String w2 = tokens[ix + 1];
					if ( !w1.equalsIgnoreCase( "<STOP>" ) && !w1.equalsIgnoreCase( "<oov>" ) && !w2.equalsIgnoreCase( "<STOP>" ) && !w2.equalsIgnoreCase( "<oov>" ) ) {
						String tk1 = w1;
						String tk2 = w2;
						if ( stemmed ) {
							tk1 = "\"" + tk1 + "\"";
							tk2 = "\"" + tk2 + "\"";
						}
						if ( fields != null ) {
							tk1 = tk1 + ".(" + fields[ix] + ")";
							tk2 = tk2 + ".(" + fields[ix + 1] + ")";
						}
						fmt_o.format( " #uw8( %s %s )", tk1, tk2 );
					}
				}
				String q_u = fmt_o.toString();
				fmt_o.close();
				if ( q_u.trim().length() > 0 ) {
					fmt.format( " %.8f #combine(%s )", weight_u, q_u );
				}
			}
		}
		fmt.format( " )" );
		fmt.close();
		return sb.toString();
	}
	
}
