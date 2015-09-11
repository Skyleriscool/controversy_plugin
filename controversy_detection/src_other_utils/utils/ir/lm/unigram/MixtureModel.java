package utils.ir.lm.unigram;

import java.io.IOException;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;

/**
 * LinearMixtureUnigramModel implements linear combination of unigram models. A whole iteration of all words in the mixed model can be created lazily.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class MixtureModel implements UnigramModel {
	
	private UnigramModel[] models;
	private double[] weights;
	
	/** Vocabulary of the mixture model. */
	private Collection<String> vocabulary;
	
	/**
	 * Constructor.
	 * 
	 * @param models
	 * @param weights
	 * @throws IOException
	 */
	public MixtureModel( UnigramModel[] models, double[] weights ) throws IOException {
		this( models, weights, true, null );
	}
	
	/**
	 * Constructor. Weights of models will be rescaled to be summed up to 1.
	 * 
	 * @param models
	 * @param weights
	 * @param normWeights
	 * @throws IOException
	 */
	public MixtureModel( UnigramModel[] models, double[] weights, boolean normWeights ) throws IOException {
		this( models, weights, normWeights, null );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param models
	 * @param weights
	 * @param vocabulary
	 * @throws IOException
	 */
	public MixtureModel( UnigramModel[] models, double[] weights, Collection<String> vocabulary ) throws IOException {
		this( models, weights, true, vocabulary );
	}
	
	/**
	 * Constructor. Weights of models will be rescaled to be summed up to 1.
	 * 
	 * @param models
	 * @param weights
	 * @param normWeights
	 * @param vocabulary
	 * @throws IOException
	 */
	public MixtureModel( UnigramModel[] models, double[] weights, boolean normWeights, Collection<String> vocabulary ) throws IOException {
		this.models = models;
		this.weights = weights;
		if ( normWeights ) {
			double norm = 0;
			for ( int i = 0 ; i < weights.length ; i++ ) {
				norm = norm + weights[i];
			}
			for ( int i = 0 ; i < weights.length ; i++ ) {
				weights[i] = weights[i] / norm;
			}
		}
		this.vocabulary = vocabulary;
	}
	
	public double probability( String word ) throws IOException {
		double weighted_prob = 0;
		for ( int i = 0 ; i < models.length ; i++ ) {
			double prob = models[i].probability( word );
			if ( Double.isNaN( prob ) || Double.isInfinite( prob ) ) {
				prob = 0;
			}
			weighted_prob += prob * weights[i];
		}
		return weighted_prob;
	}
	
	private void createWordSet() {
		vocabulary = new TreeSet<String>();
		for ( int i = 0 ; i < models.length ; i++ ) {
			for ( String word : models[i] ) {
				vocabulary.add( word );
			}
		}
	}
	
	public Iterator<String> iterator() {
		if ( vocabulary == null ) {
			createWordSet();
		}
		return vocabulary.iterator();
	}
	
	public long sizeVocabulary() throws IOException {
		if ( vocabulary == null ) {
			createWordSet();
		}
		return vocabulary.size();
	}
	
}
