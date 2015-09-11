package utils.ir.eval;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;

import utils.KVPair;

/**
 * Eval includes utilities of calculating search evaluation metrics.
 * 
 * @author Jiepu Jiang
 * @version Feb 17, 2015
 */
public class Evaluation {
	
	/** Calculate PC@k for each k in pos_prec. */
	protected int[] pos_prec;
	
	/** Calculate nDCG@k for each k in pos_nDCG. */
	protected int[] pos_nDCG;
	
	/** Calculate recall@k for each k in pos_recall. */
	protected int[] pos_recall;
	
	private Set<Integer> set_pos_prec;
	private Set<Integer> set_pos_nDCG;
	private Set<Integer> set_pos_recall;
	
	/** The maximum position of the result list to be scanned. */
	private int max_pos = 0;
	
	private static final Evaluation instance = new Evaluation();
	
	/**
	 * Constructor. By default, will report: 1) precision and nDCG at position 1, 5, 10, 15, 20, 30, 40, 50, 100; 2) recall at position 5, 10, 20, 30, 40, 50,
	 * 100, 200, 300, 400, 500.
	 */
	public Evaluation() {
		this( new int[] { 1, 5, 10, 15, 20, 30, 40, 50, 100 }, new int[] { 1, 5, 10, 15, 20, 30, 40, 50, 100 }, new int[] { 5, 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 } );
	}
	
	/**
	 * Constructor by specifying cutoff k values for PC@k, nDCG@k, and recall@k.
	 * 
	 * @param pos_prec
	 *            A list of positions where the PC@k will be calculated
	 * @param pos_nDCG
	 *            A list of positions where the nDCG@k will be calculated
	 * @param pos_recall
	 *            A list of positions where the recall@k will be calculated
	 */
	public Evaluation( int[] pos_prec, int[] pos_nDCG, int[] pos_recall ) {
		
		this.pos_prec = pos_prec;
		this.pos_nDCG = pos_nDCG;
		this.pos_recall = pos_recall;
		
		this.set_pos_prec = new HashSet<Integer>();
		this.set_pos_nDCG = new HashSet<Integer>();
		this.set_pos_recall = new HashSet<Integer>();
		
		this.max_pos = 0;
		for ( int pos : pos_prec ) {
			set_pos_prec.add( pos );
			if ( pos > max_pos ) {
				max_pos = pos;
			}
		}
		for ( int pos : pos_nDCG ) {
			set_pos_nDCG.add( pos );
			if ( pos > max_pos ) {
				max_pos = pos;
			}
		}
		for ( int pos : pos_recall ) {
			set_pos_recall.add( pos );
			if ( pos > max_pos ) {
				max_pos = pos;
			}
		}
		
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>P@k</li>
	 * <li>nDCG@k</li>
	 * <li>R@k(recall at k)</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments.
	 * @param search_results
	 *            Ranked documents.
	 * @return Evaluation results for the ranked documents.
	 */
	public KVPair eval( final QrelsInfo qrels, String[] search_results ) {
		
		KVPair eval = new KVPair();
		Collection<String> rels = qrels.relevantDocuments();
		
		// sorted relevant documents by relevance scores
		int numrel = 0;
		List<String> ideal_results = new ArrayList<String>();
		for ( String doc : rels ) {
			if ( qrels.relevance( doc ) > 0 ) {
				ideal_results.add( doc );
				numrel++;
			}
		}
		
		Collections.sort( ideal_results, new Comparator<String>() {
			public int compare( String doc1, String doc2 ) {
				return new Double( qrels.relevance( doc2 ) ).compareTo( qrels.relevance( doc1 ) );
			}
		} );
		
		// no relevant documents or empty results list
		if ( numrel == 0 || search_results.length == 0 ) {
			for ( int k : pos_prec ) {
				eval.put( "P@" + k, 0.0 );
			}
			for ( int k : pos_nDCG ) {
				eval.put( "nDCG@" + k, 0.0 );
			}
			for ( int k : pos_recall ) {
				eval.put( "R@" + k, 0.0 );
			}
			eval.put( "avgPrec", 0.0 );
			eval.put( "R-prec", 0.0 );
			eval.put( "rec-rank", 0.0 );
			eval.put( "rel", numrel );
			eval.put( "ret", ( search_results == null || search_results.length == 0 ) ? 0 : search_results.length );
			eval.put( "rel-ret", 0 );
			return eval;
		}
		
		int count_rel = 0;
		double sum_ndcg = 0;
		double sum_ndcg_perfect = 0;
		double sum_avg_prec = 0;
		
		for ( int ix = 0 ; ix < search_results.length || ix < max_pos ; ix++ ) {
			
			int pos = ix + 1;
			String doc = ix < search_results.length ? search_results[ix] : null;
			double rel = doc == null ? 0 : qrels.relevance( doc );
			
			if ( doc != null && rel > 0 ) {
				count_rel++;
				sum_avg_prec = sum_avg_prec + 1.0 * count_rel / pos;
				sum_ndcg = sum_ndcg + ( Math.pow( 2.0, rel ) - 1 ) / ( Math.log( 1 + pos ) );
				if ( !eval.containsKey( "rec-rank" ) ) {
					eval.put( "rec-rank", new Double( 1.0 / pos ) );
				}
			}
			
			if ( ix < numrel ) {
				// aggregate ideal dcg
				double relscore = qrels.relevance( ideal_results.get( ix ) );
				if ( relscore > 0 ) {
					sum_ndcg_perfect = sum_ndcg_perfect + ( Math.pow( 2.0, relscore ) - 1 ) / ( Math.log( 1 + pos ) );
				}
			}
			
			if ( pos == numrel ) {
				String key = "R-prec";
				double metric = 1.0 * count_rel / pos;
				eval.put( key, metric );
			}
			
			if ( pos == search_results.length && !eval.containsKey( "R-prec" ) ) {
				// just in case the number of relevant documents is greater than the length of the rank list
				String key = "R-prec";
				double metric = 1.0 * count_rel / numrel;
				eval.put( key, metric );
			}
			
			if ( set_pos_prec.contains( pos ) ) {
				String key = "P@" + pos;
				double metric = 1.0 * count_rel / pos;
				eval.put( key, metric );
			}
			
			if ( set_pos_recall.contains( pos ) ) {
				String key = "R@" + pos;
				double metric = count_rel == 0 ? 0 : 1.0 * count_rel / numrel;
				eval.put( key, metric );
			}
			
			if ( set_pos_nDCG.contains( pos ) ) {
				String key = "nDCG@" + pos;
				double metric = sum_ndcg == 0 ? 0 : 1.0 * sum_ndcg / sum_ndcg_perfect;
				eval.put( key, metric );
				eval.put( "DCG@" + pos, sum_ndcg );
				eval.put( "iDCG@" + pos, sum_ndcg_perfect );
			}
			
		}
		
		if ( !eval.containsKey( "rec-rank" ) ) {
			eval.put( "rec-rank", 0.0 );
		}
		
		if ( numrel == 0 ) {
			eval.put( "R-prec", 0.0 );
		}
		
		eval.put( "avgPrec", new Double( sum_avg_prec == 0 ? 0.0 : sum_avg_prec / numrel ) );
		eval.put( "rel", numrel );
		eval.put( "ret", search_results.length );
		eval.put( "rel-ret", count_rel );
		eval.put( "recall", new Double( rels.size() == 0 ? 0.0 : 1.0 * count_rel / numrel ) );
		
		return eval;
		
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>PC@k</li>
	 * <li>nDCG@k</li>
	 * <li>recall at k</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments
	 * @param search_results
	 *            Ranked documents
	 * @return Evaluation results for the ranked documents
	 */
	public KVPair eval( final QrelsInfo qrels, final List<SearchResult> search_results ) {
		return eval( qrels, search_results, false );
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>PC@k</li>
	 * <li>nDCG@k</li>
	 * <li>recall at k</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments
	 * @param search_results
	 *            Ranked documents
	 * @param rerank_tied_docs
	 *            If rerank_tied_docs is true, the search results to be evaluated will be re-ranked: first by document score in descending order, then by docno
	 *            in descending order (this is the setting in TREC web track official evaluation scripts ndeval.pl).
	 */
	public KVPair eval( final QrelsInfo qrels, final List<SearchResult> search_results, final boolean rerank_tied_docs ) {
		List<SearchResult> results = new ArrayList<SearchResult>();
		for ( SearchResult result : search_results ) {
			results.add( result );
		}
		if ( rerank_tied_docs ) {
			Collections.sort( results, new Comparator<SearchResult>() {
				public int compare( SearchResult result1, SearchResult result2 ) {
					int val = new Double( result2.getScore() ).compareTo( result1.getScore() );
					if ( val == 0 ) {
						if ( rerank_tied_docs ) {
							// if maxTies is true, tied-rank documents will be further ranked by docno
							val = result2.getDocno().compareTo( result1.getDocno() );
						}
					}
					return val;
				}
			} );
		}
		String[] docs = new String[results.size()];
		int ix = 0;
		for ( SearchResult result : results ) {
			docs[ix] = result.getDocno();
			ix++;
		}
		return eval( qrels, docs );
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>PC@k</li>
	 * <li>nDCG@k</li>
	 * <li>recall at k</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments
	 * @param search_results
	 *            Ranked documents
	 * @return Evaluation results for the ranked documents
	 */
	public static KVPair evaluate( final QrelsInfo qrels, String[] search_results ) {
		return instance.eval( qrels, search_results );
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>PC@k</li>
	 * <li>nDCG@k</li>
	 * <li>recall at k</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments
	 * @param search_results
	 *            Ranked documents
	 * @return Evaluation results for the ranked documents
	 */
	public static KVPair evaluate( final QrelsInfo qrels, final List<SearchResult> search_results ) {
		return instance.eval( qrels, search_results );
	}
	
	/**
	 * <p>
	 * Evaluation the ranked documents. Metrics to be evaluated include:
	 * </p>
	 * <ul>
	 * <li>PC@k</li>
	 * <li>nDCG@k</li>
	 * <li>recall at k</li>
	 * <li>avgPrec</li>
	 * <li>R-prec</li>
	 * <li>rec-rank</li>
	 * <li>rel, ret, rel-ret</li>
	 * </ul>
	 * 
	 * @param qrels
	 *            Relevance assessments
	 * @param search_results
	 *            Ranked documents
	 * @param rerank_tied_docs
	 *            If rerank_tied_docs is true, the search results will be re-ranked: first by document score in descending order, then by docno in descending
	 *            order (this is the setting in trec web track official evaluation scripts ndeval.pl).
	 * @return Evaluation results for the ranked documents
	 */
	public static KVPair evaluate( final QrelsInfo qrels, final List<SearchResult> search_results, final boolean maxTies ) {
		return instance.eval( qrels, search_results, maxTies );
	}
	
}
