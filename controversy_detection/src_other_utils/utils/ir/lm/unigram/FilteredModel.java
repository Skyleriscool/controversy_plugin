package utils.ir.lm.unigram;

import java.io.IOException;
import java.util.Set;
import java.util.Iterator;

/**
 * FilteredModel filters certain words from an existing unigram sample.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class FilteredModel implements UnigramModel {
	
	private UnigramModel src;
	private Set<String> filter_words;
	
	public FilteredModel( UnigramModel model, Set<String> filter_words ) throws IOException {
		this.src = model;
		this.filter_words = filter_words;
	}
	
	public double probability( String word ) throws IOException {
		return filter_words.contains( word ) ? 0 : src.probability( word );
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
