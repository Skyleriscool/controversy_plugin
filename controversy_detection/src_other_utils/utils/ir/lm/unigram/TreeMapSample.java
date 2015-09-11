package utils.ir.lm.unigram;

import java.io.IOException;
import java.io.StringReader;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * TreeMapUnigramSample stores a unigram sample as a TreeMap and allows updating of stored sample frequencies. Note that TreeMapUnigramSample assumes you will
 * control and manage sample length by yourself (thus when you update stored sample frequencies, it will not by itself update sample length information). This
 * setting would be useful if you only need to store partial sample frequecny information in TreeMapUnigramSample.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class TreeMapSample implements UnigramSample {
	
	protected TreeMap<String, Double> stats;
	protected double length;
	
	/**
	 * Initiate an empty treemap sample.
	 */
	public TreeMapSample() {
		this( 0 );
	}
	
	/**
	 * Initiate an empty treemap sample with the specified sample length. It is specifically useful if you just want to store statistics for some words (such
	 * that you cannot get length by summing up word frequencies).
	 * 
	 * @param length
	 *            Length of the unigram sample.
	 */
	public TreeMapSample( double length ) {
		this.stats = new TreeMap<String, Double>();
		this.length = length;
	}
	
	public boolean equals( Object obj ) {
		TreeMapSample sample = (TreeMapSample) obj;
		if ( length == sample.length && stats.size() == sample.stats.size() ) {
			for ( String key : stats.keySet() ) {
				if ( !sample.stats.containsKey( key ) || stats.get( key ) != sample.stats.get( key ) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Get a copy of the reference to word frequency statistics.
	 * 
	 * @return
	 */
	public TreeMap<String, Double> getStats() {
		return this.stats;
	}
	
	/**
	 * Get a deep clone of the word frequency statistics.
	 * 
	 * @return
	 */
	public TreeMap<String, Double> getStatsClone() {
		TreeMap<String, Double> clone = new TreeMap<String, Double>();
		for ( String key : stats.keySet() ) {
			clone.put( key, stats.get( key ) );
		}
		return this.stats;
	}
	
	/**
	 * Set term frequency statistics.
	 * 
	 * @param stats
	 * @return
	 */
	public TreeMapSample setStats( TreeMap<String, Double> stats ) {
		this.stats = stats;
		return this;
	}
	
	/**
	 * Set term frequency statistics by creating a new copy of the provided statistics. The original statistics will be replaced.
	 * 
	 * @param stats
	 * @return
	 */
	public TreeMapSample setStatsClone( Map<String, Double> stats ) {
		this.stats = new TreeMap<String, Double>();
		for ( String key : stats.keySet() ) {
			this.stats.put( key, stats.get( key ) );
		}
		return this;
	}
	
	public double length() {
		return this.length;
	}
	
	/**
	 * Set the length of the sample to a specific value.
	 * 
	 * @param length
	 *            Length of the sample to be set.
	 * @return The previous set length of the sample.
	 */
	public TreeMapSample setLength( double length ) {
		this.length = length;
		return this;
	}
	
	/**
	 * Calculate currently stored frequencies in the sample as the length of the sample and set the value.
	 * 
	 * @return The previous set length of the sample.
	 */
	public TreeMapSample setLength() {
		this.length = 0;
		for ( String w : stats.keySet() ) {
			this.length = this.length + stats.get( w );
		}
		return this;
	}
	
	/**
	 * @param word
	 *            A word.
	 * @return Whether currently buffered sample contains the key or not (note that this is different from the case that the word is not observed in the sample;
	 *         the criteria for unobserve sample is by its zero frequency).
	 */
	public boolean containsWord( String word ) {
		return stats.containsKey( word );
	}
	
	/**
	 * Update current term frequency statistics.
	 * 
	 * @param word
	 * @param freq
	 */
	public void update( String word, double freq ) throws IOException {
		double count = stats.getOrDefault( word, 0.0 ) + freq;
		stats.put( word, count );
	}
	
	/**
	 * Update current term frequency statistics.
	 * 
	 * @param text
	 * @param analyzer
	 * @throws IOException
	 */
	public void update( String text, Analyzer analyzer ) throws IOException {
		TokenStream ts = analyzer.tokenStream( "", new StringReader( text ) );
		CharTermAttribute attr = ts.getAttribute( CharTermAttribute.class );
		try {
			ts.reset();
			while ( ts.incrementToken() ) {
				String term = attr.toString();
				update( term, 1.0 );
			}
			ts.end();
		} finally {
			ts.close();
		}
	}
	
	/**
	 * Collect word frequency information in the provided sample sp and update current term frequency statistics.
	 * 
	 * @param sample
	 */
	public void update( UnigramSample sample, double weight ) throws IOException {
		for ( String word : sample ) {
			update( word, sample.frequency( word ) * weight );
		}
	}
	
	/**
	 * Collect word frequency information in the provided sample sp and update current term frequency statistics.
	 * 
	 * @param sample
	 */
	public void update( UnigramSample sample ) throws IOException {
		update( sample, 1.0 );
	}
	
	/**
	 * Collect a collection of parsed words into current sample.
	 * 
	 * @param words
	 * @throws IOException
	 */
	public void update( Collection<String> words, double weight ) throws IOException {
		for ( String word : words ) {
			update( word, weight );
		}
	}
	
	/**
	 * Collect a collection of parsed words into current sample.
	 * 
	 * @param words
	 * @throws IOException
	 */
	public void update( Collection<String> words ) throws IOException {
		update( words, 1.0 );
	}
	
	/**
	 * Collect an array of parsed words into current sample.
	 * 
	 * @param words
	 * @throws IOException
	 */
	public void update( String... words ) throws IOException {
		for ( String word : words ) {
			update( word, 1.0 );
		}
	}
	
	/**
	 * Setting word frequency information. Note that length of the sample will NOT be updated automatically.
	 * 
	 * @param word
	 *            A word.
	 * @param freq
	 *            Frequency of the word in the unigram sample to be collected.
	 * @return Previously stored frequency (if any) of the word in the unigram sample.
	 */
	public Double setFrequency( String word, double freq ) {
		return stats.put( word, freq );
	}
	
	/**
	 * Remove a word's entry and the frequency stored in current sample.
	 * 
	 * @param word
	 */
	public void removeFrequency( String word ) {
		if ( stats.containsKey( word ) ) {
			stats.remove( word );
		}
	}
	
	public double frequency( String word ) {
		return stats.getOrDefault( word, 0.0 );
	}
	
	/**
	 * @return Number of words currently stored in the sample's buffer (note that if you have set zero frequency for a word in the sample, the word will still
	 *         be counted).
	 */
	public long sizeVocabulary() {
		return stats.size();
	}
	
	public Iterator<String> iterator() {
		return stats.keySet().iterator();
	}
	
}
