package utils.ir.eval;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.TreeSet;

import utils.StringUtils;

public class ClickSensitiveEvaluation {
	
	public static void main( String[] args ) {
		try {
			
			Random random = new Random();
			Map<String, Double> relevance = Qrels.loadQrels( "D:/click_metrics/trec_runs/trec2_adhoc/qrels" ).get( "101" ).getMap();
			Map<String, Double> pclick = samplePClick( relevance, 0.55, 0.055, 0.39, 0.039, random );
			List<String> ranking = SearchResults.toDocnoList( SearchResults.loadTrecFormat( "D:/click_metrics/trec_runs/trec2_adhoc/input.cityau" ).get( "101" ) );
			
			DEBUG = true;
			csnDCG( ranking, 10, relevance, pclick, 0.55, 0.39, 1 );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Randomly sample
	 * 
	 * @param relevance
	 * @param pclick_R
	 * @param sd_pclick_R
	 * @param pclick_NR
	 * @param sd_pclick_NR
	 * @param random
	 * @return
	 */
	public static Map<String, Double> samplePClick( Map<String, Double> relevance, double pclick_R, double sd_pclick_R, double pclick_NR, double sd_pclick_NR, Random random ) {
		Map<String, Double> pclick = new TreeMap<String, Double>();
		for ( String docno : relevance.keySet() ) {
			double rel = relevance.get( docno );
			double pc = random.nextGaussian();
			if ( rel > 0 ) {
				pc = pclick_R + sd_pclick_R * pc;
			} else {
				pc = pclick_NR + sd_pclick_NR * pc;
			}
			if ( pc > 1 ) {
				pc = 1;
			}
			if ( pc < 0 ) {
				pc = 0;
			}
			pclick.put( docno, pc );
		}
		return pclick;
	}
	
	public static boolean DEBUG = false;
	
	public static double csnDCG( List<String> ranking, int k, Map<String, Double> relevance, Map<String, Double> pclick, double pclick_R, double pclick_NR, double penalty ) {
		
		if ( DEBUG ) {
			System.out.println( "--------------------------DEBUG INFO--------------------------" );
			System.out.println( "|ranklist| = " + ranking.size() + ", k = " + k );
			System.out.println( "|relevance| = " + relevance.size() + ", |pclick| = " + pclick.size() );
			System.out.printf( "P(click|R) = %.3f, P(click|NR) = %.3f, penalty = %.2f\n", pclick_R, pclick_NR, penalty );
			System.out.println();
		}
		
		List<String> ranking_best = new ArrayList<String>();
		List<String> ranking_worst = new ArrayList<String>();
		
		Set<String> alldocs = new TreeSet<String>();
		alldocs.addAll( relevance.keySet() );
		alldocs.addAll( pclick.keySet() );
		for ( int ix = 0 ; ix < 10 ; ix++ ) {
			alldocs.add( "DUMMY_DOCUMENT_" + ( ix + 1 ) );
		}
		
		ranking_best.addAll( alldocs );
		ranking_worst.addAll( alldocs );
		
		Collections.sort( ranking_best, new ComparatorBestRanking( relevance, pclick, pclick_R, pclick_NR, penalty ) );
		Collections.sort( ranking_worst, new ComparatorWorstRanking( relevance, pclick, pclick_R, pclick_NR, penalty ) );
		
		double sum = 0;
		double sum_best = 0;
		double sum_worst = 0;
		
		if ( DEBUG ) {
			System.out.printf( "%-10s%-10s%-40s%-40s%-40s%10s%10s%10s\n", "Rank", "Discount", "Docno", "Best", "Worst", "g (Docno)", "g (Best)", "g (Worst)" );
		}
		
		for ( int ix = 0 ; ix < k && ix < ranking.size() ; ix++ ) {
			double discount = 1.0 / ( Math.log( ix + 2 ) / Math.log( 2 ) );
			double gain = getGain( ranking.get( ix ), relevance, pclick, pclick_R, pclick_NR, penalty );
			double gain_best = getGain( ranking_best.get( ix ), relevance, pclick, pclick_R, pclick_NR, penalty );
			double gain_worst = getGain( ranking_worst.get( ix ), relevance, pclick, pclick_R, pclick_NR, penalty );
			sum = sum + gain * discount;
			sum_best = sum_best + gain_best * discount;
			sum_worst = sum_worst + gain_worst * discount;
			if ( DEBUG ) {
				System.out.printf(
						"%-10d%-10.2f%-40s%-40s%-40s%10.2f%10.2f%10.2f\n",
						( ix + 1 ), discount,
						ranking.get( ix ) + " (" + StringUtils.formatDouble( relevance.getOrDefault( ranking.get( ix ), 0.0 ), 2 ) + "," + StringUtils.formatDouble( pclick.getOrDefault( ranking.get( ix ), 0.0 ), 2 ) + ")",
						ranking_best.get( ix ) + " (" + StringUtils.formatDouble( relevance.getOrDefault( ranking_best.get( ix ), 0.0 ), 2 ) + "," + StringUtils.formatDouble( pclick.getOrDefault( ranking_best.get( ix ), 0.0 ), 2 ) + ")",
						ranking_worst.get( ix ) + " (" + StringUtils.formatDouble( relevance.getOrDefault( ranking_worst.get( ix ), 0.0 ), 2 ) + "," + StringUtils.formatDouble( pclick.getOrDefault( ranking_worst.get( ix ), 0.0 ), 2 ) + ")",
						gain, gain_best, gain_worst );
			}
		}
		
		double csnDCG = 0;
		if ( sum_best > sum_worst ) {
			csnDCG = ( sum - sum_worst ) / ( sum_best - sum_worst );
		}
		
		if ( DEBUG ) {
			System.out.printf( "csDCG = %.2f, csDCG (best) = %.2f, csDCG (worst) = %.2f\n", sum, sum_best, sum_worst );
			System.out.printf( "csnDCG = %.3f\n", csnDCG );
		}
		
		return csnDCG;
		
	}
	
	private static double getGain( String docno, Map<String, Double> relevance, Map<String, Double> pclick, double pclick_R, double pclick_NR, double penalty ) {
		double rel = relevance.getOrDefault( docno, 0.0 );
		double pc_default = ( rel > 0 ? pclick_R : pclick_NR );
		double pc = pclick.getOrDefault( docno, pc_default );
		double gain = ( rel > 0 ? ( Math.pow( 2, rel ) - 1 ) : ( -penalty ) );
		return gain * pc;
	}
	
	private static class ComparatorBestRanking implements Comparator<String> {
		
		protected Map<String, Double> relevance;
		protected Map<String, Double> pclick;
		protected double pclick_R;
		protected double pclick_NR;
		protected double penalty;
		
		public ComparatorBestRanking( Map<String, Double> relevance, Map<String, Double> pclick, double pclick_R, double pclick_NR, double penalty ) {
			this.relevance = relevance;
			this.pclick = pclick;
			this.pclick_R = pclick_R;
			this.pclick_NR = pclick_NR;
			this.penalty = penalty;
		}
		
		public int compare( String doc1, String doc2 ) {
			Double gain1 = getGain( doc1, relevance, pclick, pclick_R, pclick_NR, penalty );
			Double gain2 = getGain( doc2, relevance, pclick, pclick_R, pclick_NR, penalty );
			return gain2.compareTo( gain1 );
		}
		
	}
	
	private static class ComparatorWorstRanking implements Comparator<String> {
		
		protected Map<String, Double> relevance;
		protected Map<String, Double> pclick;
		protected double pclick_R;
		protected double pclick_NR;
		protected double penalty;
		
		public ComparatorWorstRanking( Map<String, Double> relevance, Map<String, Double> pclick, double pclick_R, double pclick_NR, double penalty ) {
			this.relevance = relevance;
			this.pclick = pclick;
			this.pclick_R = pclick_R;
			this.pclick_NR = pclick_NR;
			this.penalty = penalty;
		}
		
		public int compare( String doc1, String doc2 ) {
			Double gain1 = getGain( doc1, relevance, pclick, pclick_R, pclick_NR, penalty );
			Double gain2 = getGain( doc2, relevance, pclick, pclick_R, pclick_NR, penalty );
			return gain1.compareTo( gain2 );
		}
		
	}
	
}
