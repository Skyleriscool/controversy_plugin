package edu.umass.cs.ciir.controversy.knn.sim;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;

import utils.ArrayUtils;
import utils.ir.lucene.LuceneUtils;
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lm.unigram.TreeMapSample;
import utils.ir.lm.unigram.EstimatedModel;
import utils.ir.lm.unigram.SortedUnigramModel;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

public class LuceneTopWordsQuery implements QueryConstructor {
	
	protected Analyzer analyzer;
	protected String field_text;
	
	public LuceneTopWordsQuery( Analyzer analyzer, String field_text ) {
		this.analyzer = analyzer;
		this.field_text = field_text;
	}
	
	/**
	 * Returns an array. Array[0] is a Lucene query; Array[1] is the search field; Array[2] is a list of terms; Array[3] is a list of weights.
	 */
	public Object constructQuery( String text, int topwords, Map<String, Object> info ) {
		
		long timestamp = System.currentTimeMillis();
		Object q = null;
		try {
			
			// actually the IOException would never happen
			// so you don't need to handle it
			TreeMapSample sample = new TreeMapSample();
			sample.update( text, analyzer );
			sample.setLength();
			
			UnigramModel qm = EstimatedModel.MLE( sample );
			qm = new SortedUnigramModel( qm );
			
			List<String> terms = new ArrayList<String>();
			List<Double> weights = new ArrayList<Double>();
			
			Iterator<String> iterator = qm.iterator();
			while ( iterator.hasNext() ) {
				String term = iterator.next();
				double weight = qm.probability( term );
				terms.add( term );
				weights.add( weight );
				if ( terms.size() >= topwords ) {
					break;
				}
			}
			
			String[] array_terms = ArrayUtils.toStringArray( terms );
			double[] array_weights = ArrayUtils.toDoubleArray( weights );
			Query query = LuceneUtils.getQuery( field_text, array_terms, array_weights );
			
			q = new Object[] { query, field_text, array_terms, array_weights };
			
			if ( info != null ) {
				EntryValue[] term_weights = new EntryValue[array_terms.length];
				for ( int ix = 0 ; ix < array_terms.length ; ix++ ) {
					term_weights[ix] = new EntryValue( array_terms[ix], array_weights[ix] );
				}
				info.put( "query_topwords", topwords );
				info.put( "query_termweights", term_weights );
				info.put( "time_construct_query", ( System.currentTimeMillis() - timestamp ) / 1000.0 );
			}
			
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		return q;
		
	}
	
}
