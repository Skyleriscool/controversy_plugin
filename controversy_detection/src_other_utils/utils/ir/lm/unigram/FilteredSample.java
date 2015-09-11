package utils.ir.lm.unigram;

import java.io.IOException;

import java.util.Set;
import java.util.Iterator;

/**
 * FilteredUnigramSample filters certain words from an existing unigram sample.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class FilteredSample implements UnigramSample {
	
	private UnigramSample src;
	private Set<String> filter_words;
	
	private double adjust_length;
	
	public FilteredSample( UnigramSample sample, Set<String> filter_words ) throws IOException {
		this( sample, filter_words, true );
	}
	
	public FilteredSample( UnigramSample sample, Set<String> filter_words, boolean adjustLength ) throws IOException {
		this.src = sample;
		this.filter_words = filter_words;
		if ( adjustLength ) {
			for ( String word : filter_words ) {
				this.adjust_length += src.frequency( word );
			}
		}
	}
	
	public double frequency( String word ) throws IOException {
		return filter_words.contains( word ) ? 0 : src.frequency( word );
	}
	
	public double length() throws IOException {
		return src.length() - adjust_length;
	}
	
	public Iterator<String> iterator() {
		return new FilteredIterator( src.iterator() );
	}
	
	private class FilteredIterator implements Iterator<String> {
		
		private Iterator<String> iterator;
		private String next;
		
		FilteredIterator( Iterator<String> iterator ) {
			this.iterator = iterator;
			this.next = null;
			while ( iterator.hasNext() ) {
				String nextword = iterator.next();
				if ( !filter_words.contains( nextword ) ) {
					this.next = nextword;
					break;
				}
			}
		}
		
		public boolean hasNext() {
			return next != null;
		}
		
		public String next() {
			String retstr = next;
			next = null;
			while ( iterator.hasNext() ) {
				String nextword = iterator.next();
				if ( !filter_words.contains( nextword ) ) {
					this.next = nextword;
					break;
				}
			}
			return retstr;
		}
		
		public void remove() {
		}
		
	}
	
	public long sizeVocabulary() throws IOException {
		return src.sizeVocabulary() - filter_words.size();
	}
	
}
