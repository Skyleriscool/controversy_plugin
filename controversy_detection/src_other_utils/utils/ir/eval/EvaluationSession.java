package utils.ir.eval;

import java.util.Set;
import java.util.List;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import utils.MathUtils;
import utils.Comparators;

/**
 * Evaluation metrics related to session search.
 * 
 * @author Jiepu Jiang
 * @version Feb 17, 2015
 */
public class EvaluationSession {
	
	/**
	 * Evaluate the nDCG@k of each query reformulation's results.
	 * 
	 * @param k
	 * @param qrels
	 * @param results
	 * @return
	 */
	public static double[] nDCG( int k, QrelsInfo qrels, String[][] results ) {
		double[] metrics = new double[results.length];
		for ( int ix = 0 ; ix < results.length ; ix++ ) {
			if ( ix < results.length && results[ix] != null && results[ix].length >= 0 ) {
				metrics[ix] = new Evaluation( new int[] { k }, new int[] { k }, new int[] { k } ).eval( qrels, results[ix] ).getDouble( "nDCG@" + k );
			}
		}
		return metrics;
	}
	
	/**
	 * 
	 * 
	 * @param k
	 * @param pdown
	 * @param beta
	 * @param qrels
	 * @param results
	 * @param max_clicked_ranks
	 * @param max_browsed_ranks
	 * @return
	 */
	public static double[] inDCG( int k, double pdown, double beta, Qrels qrels, String[][] results ) {
		double[] metrics = new double[results.length];
		metrics[0] = new Evaluation( new int[] { k }, new int[] { k }, new int[] { k } ).eval( qrels, results[0] ).getDouble( "nDCG@" + k );
		for ( int ix = 1 ; ix < results.length ; ix++ ) {
			String[][] cat_results = new String[ix][];
			for ( int qix = 0 ; qix < ix ; qix++ ) {
				cat_results[qix] = results[qix];
			}
			Qrels irels = qrels.irel( cat_results, pdown, beta );
			metrics[ix] = new Evaluation( new int[] { k }, new int[] { k }, new int[] { k } ).eval( irels, results[ix] ).getDouble( "nDCG@" + k );
		}
		return metrics;
	}
	
	/**
	 * Evaluate the nsDCG@k metric without considering duplicate documents. The implementation is based on the session track overview paper in 2010 and Kanoulas
	 * et al. in SIGIR 2011.
	 * 
	 * @param k
	 * @param b
	 * @param bq
	 * @param qrels
	 * @param results
	 *            Ranked results for each of the query in the session. If a query's results are empty set, simply assign it as String[0].
	 * @return
	 */
	public static double nsDCG( int k, double b, double bq, QrelsInfo qrels, String[][] results ) {
		
		// note that the ideal ranking list is identical for each of the reformulation's search results
		List<Double> ranked_scores = new ArrayList<Double>();
		for ( String docno : qrels.relevantDocuments() ) {
			double score = qrels.relevance( docno );
			if ( score > 0 ) {
				ranked_scores.add( score );
			}
		}
		Collections.sort( ranked_scores, Comparators.DoubleDesc );
		
		double dcg_rank = 0;
		double dcg_ideal = 0;
		
		for ( int qix = 0 ; qix < results.length ; qix++ ) {
			
			String[] ranklist = ( qix < results.length ? results[qix] : new String[0] );
			double session_discount = Math.log( bq + qix ) / Math.log( bq );
			
			for ( int pos = 0 ; pos < k ; pos++ ) {
				
				String docno = ( pos < ranklist.length ? ranklist[pos] : "dummy" );
				double rank_discount = Math.log( b + pos ) / Math.log( b );
				
				double score_rank = ( docno == null ? 0 : qrels.relevance( docno ) );
				double score_ideal = ( pos < ranked_scores.size() ? ranked_scores.get( pos ) : 0 );
				
				dcg_rank = dcg_rank + ( Math.pow( 2, score_rank - 1 ) / ( session_discount * rank_discount ) );
				dcg_ideal = dcg_ideal + ( Math.pow( 2, score_ideal - 1 ) / ( session_discount * rank_discount ) );
				
			}
			
		}
		
		return dcg_rank / dcg_ideal;
		
	}
	
	/**
	 * nsDCG@k using default parameter settings (used in the SIGIR'11 paper about evaluating multi-query sessions).
	 * 
	 * @param k
	 * @param qrels
	 * @param results
	 * @return
	 */
	public static double nsDCG( int k, QrelsInfo qrels, String[][] results ) {
		return nsDCG( k, 2, 4, qrels, results );
	}
	
	/**
	 * Evaluate the accumulated instance recall at a fixed depth (k) of each query reformulation's results.
	 * 
	 * @param k
	 * @param qrels
	 * @param results
	 * @return
	 */
	public static double[] instanceRecall( int k, QrelsInfo qrels, String[][] results ) {
		Collection<String> rels = qrels.relevantDocuments();
		Set<String> found = new TreeSet<String>();
		double[] metrics = new double[results.length];
		for ( int ix = 0 ; ix < results.length ; ix++ ) {
			if ( ix < results.length && results[ix] != null ) {
				for ( int rank = 0 ; rank < k && rank < results[ix].length ; rank++ ) {
					if ( rels.contains( results[ix][rank] ) ) {
						found.add( results[ix][rank] );
					}
				}
			}
			metrics[ix] = 1.0 * found.size() / rels.size();
		}
		return metrics;
	}
	
	/**
	 * Evaluate the newly gained instance recall at a fixed depth (k) of each query reformulation's results.
	 * 
	 * @param k
	 * @param qrels
	 * @param results
	 * @return
	 */
	public static double[] instanceRecallGained( int k, QrelsInfo qrels, String[][] results ) {
		Collection<String> rels = qrels.relevantDocuments();
		Set<String> found = new TreeSet<String>();
		double[] metrics = new double[results.length];
		for ( int ix = 0 ; ix < results.length ; ix++ ) {
			if ( ix < results.length && results[ix] != null ) {
				for ( int rank = 0 ; rank < k && rank < results[ix].length ; rank++ ) {
					if ( rels.contains( results[ix][rank] ) ) {
						found.add( results[ix][rank] );
					}
				}
			}
			metrics[ix] = 1.0 * found.size() / rels.size() - ( ix == 0 ? 0 : metrics[ix - 1] );
		}
		return metrics;
	}
	
	/**
	 * Calculate for each of the reformulation its results' average jaccard similarity with all previous query's results.
	 * 
	 * @param k
	 * @param results
	 * @return
	 */
	public static double[] avgJaccard( int k, String[][] results ) {
		double[] metrics = new double[results.length];
		for ( int ix = 1 ; ix < results.length ; ix++ ) {
			List<String> results_curr = new ArrayList<String>();
			for ( int pos = 0 ; pos < k && pos < results[ix].length ; pos++ ) {
				results_curr.add( results[ix][pos] );
			}
			double jaccard = 0;
			double count = 0;
			for ( int previx = 0 ; previx < ix ; previx++ ) {
				List<String> results_prev = new ArrayList<String>();
				for ( int pos = 0 ; pos < k && pos < results[previx].length ; pos++ ) {
					results_prev.add( results[previx][pos] );
				}
				jaccard = jaccard + MathUtils.jaccard( results_curr, results_prev );
				count++;
			}
			metrics[ix] = jaccard / count;
		}
		return metrics;
	}
	
	/**
	 * Calculate the overall average jaccard similarity between each pair of queries at depth k.
	 * 
	 * @param k
	 * @param results
	 * @return
	 */
	public static double avgJaccardOverall( int k, String[][] results ) {
		double jaccard = 0;
		double count = 0;
		for ( int i = 0 ; i < results.length - 1 ; i++ ) {
			List<String> results_i = new ArrayList<String>();
			for ( int pos = 0 ; pos < k && pos < results[i].length ; pos++ ) {
				results_i.add( results[i][pos] );
			}
			for ( int j = i + 1 ; j < results.length ; j++ ) {
				List<String> results_j = new ArrayList<String>();
				for ( int pos = 0 ; pos < k && pos < results[j].length ; pos++ ) {
					results_j.add( results[j][pos] );
				}
				jaccard = jaccard + MathUtils.jaccard( results_i, results_j );
				count++;
			}
		}
		return jaccard / count;
	}
	
}
