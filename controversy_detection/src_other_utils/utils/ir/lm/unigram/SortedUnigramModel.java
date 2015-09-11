package utils.ir.lm.unigram;

import java.io.IOException;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

/**
 * SortedUnigramModel is a wrapper for unigram model whose keys (words in vocabulary) have been sorted. The default sorting is by word probability. You can also
 * assign customized sorted keys.
 * 
 * @author Jiepu Jiang
 * @version Feb 13, 2015
 */
public class SortedUnigramModel implements UnigramModel {
	
	protected UnigramModel model;
	protected List<String> sorted_keys;
	
	public long sizeVocabulary() {
		return sorted_keys.size();
	}
	
	private static Comparator<String> getComparator( final UnigramModel model ) {
		return new Comparator<String>() {
			public int compare( String key1, String key2 ) {
				try {
					return new Double( model.probability( key2 ) ).compareTo( model.probability( key1 ) );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				return 0;
			}
		};
	}
	
	/**
	 * Constructor without specifying sorting comparator. The default comparator will be used, which will sort unigram model by word probability (decreasingly).
	 * 
	 * @param model
	 *            A unigram model to be sorted.
	 * @throws IOException
	 */
	public SortedUnigramModel( UnigramModel model ) {
		this( model, getComparator( model ) );
	}
	
	/**
	 * Constructor. A comparator can be provided for sorting.
	 * 
	 * @param model
	 *            A unigram model to be sorted.
	 * @param comparator
	 *            A unigram model to be sorted.
	 * @throws IOException
	 */
	public SortedUnigramModel( UnigramModel model, Comparator<String> comparator ) {
		this.sorted_keys = new ArrayList<String>();
		this.model = model;
		for ( String key : model ) {
			this.sorted_keys.add( key );
		}
		Collections.sort( this.sorted_keys, comparator );
	}
	
	public double probability( String word ) throws IOException {
		return model.probability( word );
	}
	
	private class WPIterator implements Iterator<String> {
		
		private Iterator<String> iterator;
		
		private WPIterator() {
			iterator = sorted_keys.iterator();
		}
		
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		public String next() {
			return iterator.next();
		}
		
		public void remove() {
		}
		
	}
	
	public String toString() {
		return toString( 0 );
	}
	
	public String toString( int topWords ) {
		try {
			int count = 0;
			Iterator<String> words = iterator();
			StringBuilder sb = new StringBuilder();
			while ( words.hasNext() ) {
				String w = words.next();
				sb.append( w );
				sb.append( "\t" );
				sb.append( Double.toString( probability( w ) ) );
				sb.append( "\n" );
				count++;
				if ( topWords > 0 && count >= topWords ) {
					break;
				}
			}
			return sb.toString();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return super.toString();
	}
	
	public Iterator<String> iterator() {
		return new WPIterator();
	}
	
}
