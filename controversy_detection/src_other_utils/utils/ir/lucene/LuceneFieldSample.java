package utils.ir.lucene;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.BytesRef;

import utils.ir.lm.unigram.UnigramSample;

/**
 * An implementation of a whole lucene index's unigram sample.
 * 
 * @author Jiepu Jiang
 * @version Feb 16, 2015
 */
public class LuceneFieldSample implements UnigramSample {
	
	protected IndexReader index;
	protected String field;
	protected double length;
	protected long sizeVocabulary;
	
	public LuceneFieldSample( IndexReader index, String field ) throws IOException {
		this.index = index;
		this.field = field;
		this.length = index.getSumTotalTermFreq( field );
		// this.sizeVocabulary = MultiFields.getTerms( index, field ).size();
	}
	
	public Iterator<String> iterator() {
		TermsEnum termsEnum = null;
		try {
			Terms terms = MultiFields.getTerms( index, field );
			termsEnum = terms.iterator( null );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return new IndexFieldTermIterator( termsEnum );
	}
	
	public static class IndexFieldTermIterator implements Iterator<String> {
		
		protected TermsEnum termsEnum;
		protected String next;
		
		public IndexFieldTermIterator( TermsEnum termsEnum ) {
			this.termsEnum = termsEnum;
		}
		
		public boolean hasNext() {
			try {
				BytesRef term = termsEnum.next();
				if ( term != null ) {
					next = Term.toString( term );
					return true;
				}
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			return false;
		}
		
		public String next() {
			return next;
		}
		
	}
	
	public double frequency( String word ) throws IOException {
		return index.totalTermFreq( new Term( field, word ) );
	}
	
	public double length() throws IOException {
		return length;
	}
	
	public long sizeVocabulary() throws IOException {
		return sizeVocabulary;
	}
	
}
