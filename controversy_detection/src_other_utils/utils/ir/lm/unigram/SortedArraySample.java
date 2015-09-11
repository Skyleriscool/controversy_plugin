package utils.ir.lm.unigram;

import java.util.Arrays;
import java.util.Iterator;

import utils.ArrayUtils;

/**
 * Implementation of unigram sample that stores words and frequencies in the sample as a sorted array (by words). Note that it is designed only to staticly
 * store unigram sample information, rather than constantly updating stored samples.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class SortedArraySample implements UnigramSample {
	
	protected String[] words;
	protected double[] freqs;
	protected double length;
	
	/**
	 * Constructor. Sample length will be calculated based on the samples.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param doc_length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, int[] freqs ) {
		setBuffer( words, freqs );
		setLength();
	}
	
	/**
	 * Constructor. Sample length will be calculated based on the samples.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param doc_length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, long[] freqs ) {
		setBuffer( words, freqs );
		setLength();
	}
	
	/**
	 * Constructor. Sample length will be calculated based on the samples.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param doc_length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, double[] freqs ) {
		setBuffer( words, freqs );
		setLength();
	}
	
	/**
	 * Constructor. Sample length should be specified manually.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, int[] freqs, double length ) {
		setBuffer( words, freqs );
		setLength( length );
	}
	
	/**
	 * Constructor. Sample length should be specified manually.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, long[] freqs, double length ) {
		setBuffer( words, freqs );
		setLength( length );
	}
	
	/**
	 * Constructor. Sample length should be specified manually.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 * @param length
	 *            Length of the sample.
	 */
	public SortedArraySample( String[] words, double[] freqs, double length ) {
		setBuffer( words, freqs );
		setLength( length );
	}
	
	/**
	 * Note that even though necessary, length will not be reset after you reset the buffered array, unless you manually do that.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 */
	public void setBuffer( String[] words, int[] freqs ) {
		this.words = words;
		this.freqs = ArrayUtils.toDoubleArray( freqs );
	}
	
	/**
	 * Note that even though necessary, length will not be reset after you reset the buffered array, unless you manually do that.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 */
	public void setBuffer( String[] words, long[] freqs ) {
		this.words = words;
		this.freqs = ArrayUtils.toDoubleArray( freqs );
	}
	
	/**
	 * Note that even though necessary, length will not be reset after you reset the buffered array, unless you manually do that.
	 * 
	 * @param words
	 *            An array of words.
	 * @param freqs
	 *            An array of words' frequencies in the sample.
	 */
	public void setBuffer( String[] words, double[] freqs ) {
		this.words = words;
		this.freqs = freqs;
	}
	
	/**
	 * Set sample length to a specified value.
	 * 
	 * @param length
	 */
	public void setLength( double length ) {
		this.length = length;
	}
	
	/**
	 * Set sample length by summing up current available samples in the buffer.
	 */
	public void setLength() {
		length = 0;
		for ( int i = 0 ; i < freqs.length ; i++ ) {
			length = length + freqs[i];
		}
	}
	
	/**
	 * @param pos
	 *            A position in the array that stores sample words.
	 * @return The word at the position.
	 */
	public String wordAt( int pos ) {
		return words[pos];
	}
	
	/**
	 * @param pos
	 *            A position in the array that stores sample words.
	 * @return Frequency of the word at the position.
	 */
	public double freqAt( int pos ) {
		return freqs[pos];
	}
	
	/** @return Length of the sample. */
	public double length() {
		return length;
	}
	
	/** @return Number of unique words in the sample. */
	public long sizeVocabulary() {
		return words.length;
	}
	
	public double frequency( String word ) {
		int pos = Arrays.binarySearch( words, word );
		return pos >= 0 ? freqs[pos] : 0;
	}
	
	/**
	 * An iterator for the words array.
	 */
	private class ArrayIterator implements Iterator<String> {
		
		private int pos = 0;
		
		public boolean hasNext() {
			return pos < words.length;
		}
		
		public String next() {
			String element = null;
			if ( pos < words.length ) {
				element = words[pos];
				pos++;
			}
			return element;
		}
		
		public void remove() {
		}
		
	}
	
	public Iterator<String> iterator() {
		return new ArrayIterator();
	}
	
}
