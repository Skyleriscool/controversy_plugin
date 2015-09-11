package utils.ir.lm;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;

import utils.ir.analysis.AnalyzerUtils;
import utils.ir.lm.unigram.MixtureModel;
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lm.unigram.UnigramSample;
import utils.ir.lm.unigram.TreeMapSample;
import utils.ir.lm.unigram.EstimatedModel;
import utils.ir.lm.unigram.SortedUnigramModel;

/**
 * Utilities related to language modeling.
 * 
 * @author Jiepu Jiang
 * @version Feb 15, 2015
 */
public class LMUtils {
	
	public static final double E10 = 1.0e-10;
	public static final double E20 = 1.0e-20;
	public static final double E30 = 1.0e-30;
	public static final double E40 = 1.0e-40;
	public static final double E50 = 1.0e-50;
	public static final double E60 = 1.0e-60;
	public static final double E70 = 1.0e-70;
	public static final double E80 = 1.0e-80;
	public static final double E90 = 1.0e-90;
	public static final double E100 = 1.0e-100;
	
	/**
	 * Get probability of a word from the model. If zero probability, return the default probability value.
	 * 
	 * @param model
	 * @param word
	 * @param default_probability
	 * @return
	 * @throws IOException
	 */
	public static double probability( UnigramModel model, String word, double default_probability ) throws IOException {
		double probability = model.probability( word );
		if ( probability == 0 ) {
			probability = default_probability;
		}
		return probability;
	}
	
	/**
	 * <p>
	 * Calculate the log probability of a model m1 given another model m2 on a set of pre-defined vocabulary words. The specific calculation is as follows:
	 * </p>
	 * 
	 * <pre>
	 * log P(M1|M2) = sum_w { P(w|M1) * logP(w|M2) }
	 * </pre>
	 * 
	 * @param m1
	 * @param m2
	 * @param vocabulary
	 * @param default_probability
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel m1, UnigramModel m2, Iterator<String> vocabulary, double default_probability ) throws IOException {
		double logProb = 0;
		while ( vocabulary.hasNext() ) {
			String word = vocabulary.next();
			double weight = probability( m1, word, default_probability );
			double probability = probability( m2, word, default_probability );
			logProb = logProb + weight * Math.log( probability );
		}
		return logProb;
	}
	
	/**
	 * Calculate the log probability of a model m1 given another model m2. The vocabulary words are those in model m1 (this is the scenario that m1 is a query
	 * model while m2 is a document model).
	 * 
	 * @param m1
	 * @param m2
	 * @param default_probability
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel m1, UnigramModel m2, double default_probability ) throws IOException {
		return logProb( m1, m2, m1.iterator(), default_probability );
	}
	
	/**
	 * Calculate the log probability of a model m1 given another model m2. Only considering a few top weighted words in m1.
	 * 
	 * @param m1
	 * @param m2
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel m1, UnigramModel m2, int top, double default_probability ) throws IOException {
		double logProb = 0;
		m1 = new SortedUnigramModel( m1 );
		int count = 0;
		for ( String word : m1 ) {
			double weight = probability( m1, word, default_probability );
			double probability = probability( m2, word, default_probability );
			logProb = logProb + weight * Math.log( probability );
			if ( count >= top ) {
				break;
			}
		}
		return logProb;
	}
	
	/**
	 * Calculate the log probability of independently generating a list of words from the unigram model.
	 * 
	 * @param model
	 * @param words
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel model, String[] words, double default_probability ) throws IOException {
		double logProb = 0;
		for ( String word : words ) {
			double probability = probability( model, word, default_probability );
			logProb = logProb + Math.log( probability );
		}
		return logProb;
	}
	
	/**
	 * Calculate the log probability of independently generating a list of words from the unigram model.
	 * 
	 * @param model
	 * @param words
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel model, Collection<String> words, double default_probability ) throws IOException {
		double logProb = 0;
		for ( String word : words ) {
			double probability = probability( model, word, default_probability );
			logProb = logProb + Math.log( probability );
		}
		return logProb;
	}
	
	/**
	 * Calculate the log probability of independently generating a chunk of text (tokenized by the specified analyzer) from the unigram model.
	 * 
	 * @param model
	 * @param words
	 * @return
	 * @throws IOException
	 */
	public static double logProb( UnigramModel model, String text, Analyzer analyzer, double default_probability ) throws IOException {
		return logProb( model, AnalyzerUtils.tokenize( text, analyzer ), default_probability );
	}
	
	/**
	 * Calculate the Kullback–Leibler divergence between two models m1 and m2 on a set of vocabulary words.
	 * 
	 * @param m1
	 * @param m2
	 * @param vocabulary
	 * @return
	 * @throws IOException
	 */
	public static double KLDivergence( UnigramModel m1, UnigramModel m2, Iterator<String> vocabulary, double default_probability ) throws IOException {
		double sum = 0;
		while ( vocabulary.hasNext() ) {
			String word = vocabulary.next();
			double prob1 = probability( m1, word, default_probability );
			double prob2 = probability( m2, word, default_probability );
			sum = sum + prob1 * Math.log( prob1 / prob2 );
		}
		return sum;
	}
	
	/**
	 * Calculate the Kullback–Leibler divergence between two models m1 and m2. Use m1's vocabulary for calculation.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 * @throws IOException
	 */
	public static double KLDivergence( UnigramModel m1, UnigramModel m2, double default_probability ) throws IOException {
		return KLDivergence( m1, m2, m1.iterator(), default_probability );
	}
	
	/**
	 * Calculate the Kullback–Leibler divergence between two models m1 and m2. Use m1's vocabulary for calculation. Only consider top weighted words in m1.
	 * 
	 * @param m1
	 * @param m2
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static double KLDivergence( UnigramModel m1, UnigramModel m2, int top, double default_probability ) throws IOException {
		m1 = new SortedUnigramModel( m1 );
		int count = 0;
		double sum = 0;
		for ( String word : m1 ) {
			double prob1 = probability( m1, word, default_probability );
			double prob2 = probability( m2, word, default_probability );
			sum = sum + prob1 * Math.log( prob1 / prob2 );
			count++;
			if ( count >= top ) {
				break;
			}
		}
		return sum;
	}
	
	/**
	 * Calculate the cosine similarty between two unigram model.
	 * 
	 * @param words
	 * @param m1
	 * @param m2
	 * @return
	 * @throws IOException
	 */
	public static double cosine( UnigramModel m1, UnigramModel m2, Iterator<String> words, double default_probability ) throws IOException {
		double top = 0;
		double bot1 = 0;
		double bot2 = 0;
		while ( words.hasNext() ) {
			String word = words.next();
			double prob1 = probability( m1, word, default_probability );
			double prob2 = probability( m2, word, default_probability );
			top = top + prob1 * prob2;
			bot1 = bot1 + prob1 * prob1;
			bot2 = bot2 + prob2 * prob2;
		}
		return top / Math.pow( bot1 * bot2, 0.5 );
	}
	
	private static double default_threshold_cosine_stable = 0.000001;
	private static int default_max_loop = 100;
	
	/**
	 * Estimate a parsimonious language model for the observed unigram word sample by factoring out a known (assumed) noise model. It uses EM for model
	 * estimation. The estimation terminates if the model gets stable (with cosine similarity of two rounds less than the default threshold, 0.000001) or after
	 * certain 100 iterations.
	 * 
	 * @param sample
	 * @param noise_model
	 * @param weight_noise
	 * @param default_probability
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel factorOut( UnigramSample sample, UnigramModel noise_model, double weight_noise, double default_probability ) throws IOException {
		return factorOut( sample, noise_model, weight_noise, default_probability, default_threshold_cosine_stable, default_max_loop );
	}
	
	/**
	 * Estimate a parsimonious language model for the observed unigram word sample by factoring out a known (assumed) noise model. It uses EM for model
	 * estimation. The estimation terminates if the model gets stable (with cosine similarity of two rounds less than the default threshold, 0.000001) or after
	 * certain amounts of iterations.
	 * 
	 * @param sample
	 * @param noise_model
	 * @param weight_noise
	 * @param default_probability
	 * @param max_loop
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel factorOut( UnigramSample sample, UnigramModel noise_model, double weight_noise, double default_probability, int max_loop ) throws IOException {
		return factorOut( sample, noise_model, weight_noise, default_probability, default_threshold_cosine_stable, max_loop );
	}
	
	/**
	 * Estimate a parsimonious language model for the observed unigram word sample by factoring out a known (assumed) noise model. It uses EM for model
	 * estimation. The estimation terminates if the model gets stable (with cosine similarity of two rounds less than a threshold) or after certain amounts of
	 * iterations.
	 * 
	 * @param sample
	 * @param noise_model
	 * @param weight_noise
	 * @param default_probability
	 * @param threshold_cosine
	 * @param max_loop
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel factorOut( UnigramSample sample, UnigramModel noise_model, double weight_noise, double default_probability, double threshold_cosine, int max_loop ) throws IOException {
		UnigramModel current_model = EstimatedModel.MLE( sample );
		for ( int loop = 0 ; max_loop <= 0 || loop < max_loop ; loop++ ) {
			UnigramModel new_model = factorOut( sample, current_model, noise_model, weight_noise, default_probability );
			double cosine = cosine( current_model, new_model, sample.iterator(), default_probability );
			if ( ( 1 - cosine ) < threshold_cosine ) {
				return new_model;
			}
			current_model = new_model;
		}
		return current_model;
	}
	
	private static UnigramModel factorOut( UnigramSample sample, UnigramModel current_model, UnigramModel noise_model, double weight_noise, double default_probability ) throws IOException {
		TreeMapSample predicted_sample = new TreeMapSample();
		double weight_current_model = 1 - weight_noise;
		for ( String word : sample ) {
			double observed_freq = sample.frequency( word );
			double prob_current_model = probability( current_model, word, default_probability );
			double prob_noise = probability( noise_model, word, default_probability );
			double predicted_freq = observed_freq * weight_current_model * prob_current_model / ( weight_current_model * prob_current_model + weight_noise * prob_noise );
			predicted_sample.update( word, predicted_freq );
		}
		predicted_sample.setLength();
		return EstimatedModel.MLE( predicted_sample );
	}
	
	/**
	 * Learn three models from two samples <code>sample1</code> and <code>sample2</code> using an EM algorithm: <code>model1</code>, a model for words that are
	 * unique in <code>sample1</code>; <code>model2</code>, a model for words that are unique in <code>sample2</code>; <code>model_common</code>, a model for
	 * the common contents of <code>sample1</code> and <code>sample2</code>. It assumes that: <code>sample1</code> is generated from the mixture model of
	 * <code>model1</code>, <code>model_common</code>, and <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and
	 * <code>weight_noise</code>; <code>sample2</code> is generated from the mixture model of <code>model2</code>, <code>model_common</code>, and
	 * <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and <code>weight_noise</code>.
	 * 
	 * @param sample1
	 * @param sample2
	 * @param noise_model
	 * @param weight_model
	 * @param weight_common
	 * @param default_probability
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel[] factorOut( UnigramSample sample1, UnigramSample sample2, UnigramModel noise_model, double weight_model, double weight_common, double default_probability ) throws IOException {
		return factorOut( sample1, sample2, noise_model, weight_model, weight_common, default_probability, default_threshold_cosine_stable, default_max_loop );
	}
	
	/**
	 * Learn three models from two samples <code>sample1</code> and <code>sample2</code> using an EM algorithm: <code>model1</code>, a model for words that are
	 * unique in <code>sample1</code>; <code>model2</code>, a model for words that are unique in <code>sample2</code>; <code>model_common</code>, a model for
	 * the common contents of <code>sample1</code> and <code>sample2</code>. It assumes that: <code>sample1</code> is generated from the mixture model of
	 * <code>model1</code>, <code>model_common</code>, and <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and
	 * <code>weight_noise</code>; <code>sample2</code> is generated from the mixture model of <code>model2</code>, <code>model_common</code>, and
	 * <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and <code>weight_noise</code>.
	 * 
	 * @param sample1
	 * @param sample2
	 * @param noise_model
	 * @param weight_model
	 * @param weight_common
	 * @param default_probability
	 * @param max_loop
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel[] factorOut( UnigramSample sample1, UnigramSample sample2, UnigramModel noise_model, double weight_model, double weight_common, double default_probability, int max_loop ) throws IOException {
		return factorOut( sample1, sample2, noise_model, weight_model, weight_common, default_probability, default_threshold_cosine_stable, max_loop );
	}
	
	/**
	 * Learn three models from two samples <code>sample1</code> and <code>sample2</code> using an EM algorithm: <code>model1</code>, a model for words that are
	 * unique in <code>sample1</code>; <code>model2</code>, a model for words that are unique in <code>sample2</code>; <code>model_common</code>, a model for
	 * the common contents of <code>sample1</code> and <code>sample2</code>. It assumes that: <code>sample1</code> is generated from the mixture model of
	 * <code>model1</code>, <code>model_common</code>, and <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and
	 * <code>weight_noise</code>; <code>sample2</code> is generated from the mixture model of <code>model2</code>, <code>model_common</code>, and
	 * <code>model_noise</code> with weights <code>weight_model</code>, <code>weight_common</code>, and <code>weight_noise</code>.
	 * 
	 * @param sample1
	 * @param sample2
	 * @param noise_model
	 * @param weight_model
	 * @param weight_common
	 * @param default_probability
	 * @param threshold_cosine
	 * @param max_loop
	 * @return
	 * @throws IOException
	 */
	public static UnigramModel[] factorOut( UnigramSample sample1, UnigramSample sample2, UnigramModel noise_model, double weight_model, double weight_common, double default_probability, double threshold_cosine, int max_loop ) throws IOException {
		
		UnigramModel current_model1 = EstimatedModel.MLE( sample1 );
		UnigramModel current_model2 = EstimatedModel.MLE( sample2 );
		
		TreeMapSample sample_common = new TreeMapSample();
		sample_common.update( sample1 );
		sample_common.update( sample2 );
		sample_common.setLength();
		UnigramModel current_model_common = EstimatedModel.MLE( sample_common );
		
		for ( int loop = 0 ; loop < max_loop || max_loop <= 0 ; loop++ ) {
			UnigramModel[] new_model = factorOut( sample1, sample2, current_model1, current_model2, current_model_common, noise_model, weight_model, weight_common, default_probability );
			double diff1 = 1 - cosine( current_model1, new_model[0], sample1.iterator(), default_probability );
			double diff2 = 1 - cosine( current_model2, new_model[1], sample2.iterator(), default_probability );
			double diffcommon = 1 - cosine( current_model_common, new_model[2], sample_common.iterator(), default_probability );
			if ( diff1 < threshold_cosine && diff2 < threshold_cosine && diffcommon < threshold_cosine ) {
				return new_model;
			}
			current_model1 = new_model[0];
			current_model2 = new_model[1];
			current_model_common = new_model[2];
			System.out.println( loop + "\t" + diff1 + "\t" + diff2 + "\t" + diffcommon );
		}
		
		return new UnigramModel[] { current_model1, current_model2, current_model_common };
		
	}
	
	private static UnigramModel[] factorOut( UnigramSample sample1, UnigramSample sample2, UnigramModel model1, UnigramModel model2, UnigramModel common, UnigramModel noise, double weight_model, double weight_common, double default_probability ) throws IOException {
		
		TreeMapSample sample_m1 = new TreeMapSample();
		TreeMapSample sample_m2 = new TreeMapSample();
		TreeMapSample sample_common1 = new TreeMapSample();
		TreeMapSample sample_common2 = new TreeMapSample();
		
		double weight_noise = 1 - weight_model - weight_common;
		
		for ( String word : sample1 ) {
			double observed_freq = sample1.frequency( word );
			double prob_model1 = probability( model1, word, default_probability );
			double prob_common = probability( common, word, default_probability );
			double prob_noise = probability( noise, word, default_probability );
			double predicted_freq_model1 = observed_freq * weight_model * prob_model1 / ( weight_model * prob_model1 + weight_common * prob_common + weight_noise * prob_noise );
			double predicted_freq_common = observed_freq * weight_common * prob_common / ( weight_model * prob_model1 + weight_common * prob_common + weight_noise * prob_noise );
			sample_m1.update( word, predicted_freq_model1 );
			sample_common1.update( word, predicted_freq_common );
		}
		
		for ( String word : sample2 ) {
			double observed_freq = sample2.frequency( word );
			double prob_model2 = probability( model2, word, default_probability );
			double prob_common = probability( common, word, default_probability );
			double prob_noise = probability( noise, word, default_probability );
			double predicted_freq_model2 = observed_freq * weight_model * prob_model2 / ( weight_model * prob_model2 + weight_common * prob_common + weight_noise * prob_noise );
			double predicted_freq_common = observed_freq * weight_common * prob_common / ( weight_model * prob_model2 + weight_common * prob_common + weight_noise * prob_noise );
			sample_m2.update( word, predicted_freq_model2 );
			sample_common2.update( word, predicted_freq_common );
		}
		
		sample_m1.setLength();
		sample_m2.setLength();
		sample_common1.setLength();
		sample_common2.setLength();
		
		return new UnigramModel[] {
				EstimatedModel.MLE( sample_m1 ),
				EstimatedModel.MLE( sample_m2 ),
				new MixtureModel( new UnigramModel[] { EstimatedModel.MLE( sample_common1 ), EstimatedModel.MLE( sample_common2 ) }, new double[] { 0.5, 0.5 } ),
		};
		
	}
	
}
