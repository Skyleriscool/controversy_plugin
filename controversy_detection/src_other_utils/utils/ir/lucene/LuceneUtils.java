package utils.ir.lucene;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DocumentStoredFieldVisitor;

import utils.ArrayUtils;
import utils.ir.eval.SearchResult;
import utils.ir.eval.SearchResults;
import utils.ir.analysis.AnalyzerUtils;
import utils.ir.lm.unigram.EstimatedModel;
import utils.ir.lm.unigram.MixtureModel;
import utils.ir.lm.unigram.SortedUnigramModel;
import utils.ir.lm.unigram.TreeMapSample;
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lm.unigram.UnigramSample;
import utils.ir.lm.unigram.BufferedSample;
import utils.ir.lucene.similarity.QLDirichletSmoothing;

/**
 * Utilities related to Apache Lucene.
 * 
 * @author Jiepu Jiang
 * @version Feb 18, 2015
 */
public class LuceneUtils {
	
	/**
	 * Get a unigram sample from a stored lucene document vector. It throws an exception if the index did not store document vector.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static TreeMapSample getDocSample( IndexReader index, int docid, String field ) throws IOException {
		TreeMapSample docsample = new TreeMapSample();
		Terms vect = index.getTermVector( docid, field );
		TermsEnum iterator = vect.iterator( null );
		BytesRef term = null;
		while ( ( term = iterator.next() ) != null ) {
			docsample.update( Term.toString( term ), vect.getSumTotalTermFreq() );
		}
		docsample.setLength();
		return docsample;
	}
	
	/**
	 * Get a unigram sample from stored lucene document vectors (each field has a weight, such that term frequency of different fields are weighted and then
	 * summed together). It throws an exception if the index did not store document vector.
	 * 
	 * @param index
	 * @param docid
	 * @param fields
	 * @param weights
	 * @return
	 * @throws IOException
	 */
	public static TreeMapSample getDocFieldWeightedSample( IndexReader index, int docid, String[] fields, double[] weights ) throws IOException {
		TreeMapSample docsample = new TreeMapSample();
		Fields vects = index.getTermVectors( docid );
		for ( int ix = 0 ; ix < fields.length ; ix++ ) {
			String field = fields[ix];
			double weight = weights[ix];
			Terms vect = vects.terms( field );
			TermsEnum iterator = vect.iterator( null );
			BytesRef term = null;
			while ( ( term = iterator.next() ) != null ) {
				docsample.update( Term.toString( term ), weight * vect.getSumTotalTermFreq() );
			}
		}
		docsample.setLength();
		return docsample;
	}
	
	/**
	 * Get a unigram sample for an index field. Will use buffer.
	 * 
	 * @param index
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static UnigramSample getCorpusSample( IndexReader index, String field ) throws IOException {
		return getCorpusSample( index, field, true );
	}
	
	/**
	 * Get a unigram sample for an index field. Can select whether to use buffer. Buffer is recommended when you are going to repeatedly accessing the
	 * statistics of the same set of terms, such that index I/O can be minimized.
	 * 
	 * @param index
	 * @param field
	 * @param buffered
	 * @return
	 * @throws IOException
	 */
	public static UnigramSample getCorpusSample( IndexReader index, String field, boolean buffered ) throws IOException {
		UnigramSample sample = new LuceneFieldSample( index, field );
		if ( buffered ) {
			sample = new BufferedSample( sample );
		}
		return sample;
	}
	
	/**
	 * Find from the index the first document that has the specified field value. If no document is found, return -1.
	 * 
	 * @param index
	 * @param field
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static int find( IndexReader index, String field, String value ) throws IOException {
		BytesRef term = new BytesRef( value );
		DocsEnum posting = MultiFields.getTermDocsEnum( index, MultiFields.getLiveDocs( index ), field, term );
		if ( posting != null ) {
			int docid = posting.nextDoc();
			if ( docid != DocsEnum.NO_MORE_DOCS ) {
				return docid;
			}
		}
		return -1;
	}
	
	/**
	 * Find from the index all documents that have the specified field value.
	 * 
	 * @param index
	 * @param field
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static List<Integer> findAll( IndexReader index, String field, String value ) throws IOException {
		List<Integer> docs = new ArrayList<Integer>();
		BytesRef term = new BytesRef( value );
		DocsEnum posting = MultiFields.getTermDocsEnum( index, MultiFields.getLiveDocs( index ), field, term );
		int docid = posting.nextDoc();
		while ( docid != DocsEnum.NO_MORE_DOCS ) {
			docs.add( docid );
			docid = posting.nextDoc();
		}
		return docs;
	}
	
	/**
	 * Accessing a stored string value in a document field. For efficiency issues, get a visitor by calling getDocFieldVisitor(String) and reuse the visitor.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static String getDocFieldStringValue( IndexReader index, int docid, String field ) throws IOException {
		DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor( field );
		index.document( docid, visitor );
		return visitor.getDocument().get( field );
	}
	
	/**
	 * Accessing a stored int value in a document field. For efficiency issues, get a visitor by calling getDocFieldVisitor(String) and reuse the visitor.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static int getDocFieldIntValue( IndexReader index, int docid, String field ) throws IOException {
		DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor( field );
		index.document( docid, visitor );
		return visitor.getDocument().getField( field ).numericValue().intValue();
	}
	
	/**
	 * Accessing a stored long value in a document field. For efficiency issues, get a visitor by calling getDocFieldVisitor(String) and reuse the visitor.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static long getDocFieldLongValue( IndexReader index, int docid, String field ) throws IOException {
		DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor( field );
		index.document( docid, visitor );
		return visitor.getDocument().getField( field ).numericValue().longValue();
	}
	
	/**
	 * Accessing a stored float value in a document field. For efficiency issues, get a visitor by calling getDocFieldVisitor(String) and reuse the visitor.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static float getDocFieldFloatValue( IndexReader index, int docid, String field ) throws IOException {
		DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor( field );
		index.document( docid, visitor );
		return visitor.getDocument().getField( field ).numericValue().floatValue();
	}
	
	/**
	 * Accessing a stored double value in a document field. For efficiency issues, get a visitor by calling getDocFieldVisitor(String) and reuse the visitor.
	 * 
	 * @param index
	 * @param docid
	 * @param field
	 * @return
	 * @throws IOException
	 */
	public static double getDocFieldDoubleValue( IndexReader index, int docid, String field ) throws IOException {
		DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor( field );
		index.document( docid, visitor );
		return visitor.getDocument().getField( field ).numericValue().doubleValue();
	}
	
	/**
	 * Get a equally-weighted term query (combined using boolean query; set each term SHOULD occur).
	 * 
	 * @param field
	 * @param terms
	 * @return
	 */
	public static Query getQuery( String field, String[] terms ) {
		return getQuery( field, terms, null );
	}
	
	/**
	 * Get a weighted term query (combined using boolean query; set each term SHOULD occur).
	 * 
	 * @param field
	 * @param terms
	 * @param weights
	 * @return
	 */
	public static Query getQuery( String field, String[] terms, double[] weights ) {
		if ( terms.length == 0 ) {
			return null;
		}
		BooleanQuery q = new BooleanQuery();
		for ( int ix = 0 ; ix < terms.length ; ix++ ) {
			Query term = new TermQuery( new Term( field, terms[ix] ) );
			if ( weights != null ) {
				term.setBoost( (float) weights[ix] );
			}
			q.add( term, Occur.SHOULD );
		}
		return q;
	}
	
	public static Query getQuery( String field, String query, Analyzer analyzer ) throws IOException {
		return getQuery( field, AnalyzerUtils.tokenizeAsArray( query, analyzer ) );
	}
	
	public static SearchResults getSearchResults( TopDocs luceneresults ) {
		return getSearchResults( luceneresults, 0 );
	}
	
	public static SearchResults getSearchResults( TopDocs luceneresults, int top ) {
		SearchResults results = new SearchResults();
		for ( ScoreDoc doc : luceneresults.scoreDocs ) {
			results.add( new SearchResult( doc.doc, null, doc.score ) );
			if ( top > 0 && results.size() >= top ) {
				break;
			}
		}
		return results;
	}
	
	/**
	 * Search results from a lucene index using query likelihood model with dirichlet smoothing. The relevance scores are QL scores in log base.
	 * 
	 * @param searcher
	 * @param field_docno
	 * @param field_search
	 * @param terms
	 * @param weights
	 * @param mu
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static SearchResults searchQL( IndexSearcher searcher, String field_docno, String field_search, String[] terms, double[] weights, double mu, int top ) throws IOException {
		QLDirichletSmoothing sim = new QLDirichletSmoothing( mu );
		searcher.setSimilarity( sim );
		Query query = getQuery( field_search, terms, weights );
		SearchResults results = getSearchResults( searcher.search( query, top ) );
		for ( SearchResult result : results ) {
			result.setScore( QLDirichletSmoothing.rescaleScore( result.getScore(), searcher.getIndexReader(), field_search, terms, weights ) );
		}
		results.setResultsDocno( searcher.getIndexReader(), field_docno, null );
		return results;
	}
	
	/**
	 * Search results from a lucene index using query likelihood model with dirichlet smoothing. The relevance scores are QL scores in log base.
	 * 
	 * @param searcher
	 * @param field_docno
	 * @param field_search
	 * @param terms
	 * @param weights
	 * @param mu
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static SearchResults searchQL( IndexSearcher searcher, String field_docno, String field_search, String[] terms, double mu, int top ) throws IOException {
		double[] weights = new double[terms.length];
		for ( int ix = 0 ; ix < weights.length ; ix++ ) {
			weights[ix] = 1.0 / terms.length;
		}
		return searchQL( searcher, field_docno, field_search, terms, weights, mu, top );
	}
	
	/**
	 * Search results from a lucene index using query likelihood model with dirichlet smoothing. The relevance scores are QL scores in log base.
	 * 
	 * @param searcher
	 * @param field_docno
	 * @param field_search
	 * @param textquery
	 * @param analyzer
	 * @param mu
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static SearchResults searchQL( IndexSearcher searcher, String field_docno, String field_search, String textquery, Analyzer analyzer, double mu, int top ) throws IOException {
		return searchQL( searcher, field_docno, field_search, AnalyzerUtils.tokenizeAsArray( textquery, analyzer ), mu, top );
	}
	
	/**
	 * Search results from a lucene index using query likelihood model with dirichlet smoothing. The relevance scores are QL scores in log base.
	 * 
	 * @param searcher
	 * @param field_docno
	 * @param field_search
	 * @param qmodel
	 * @param mu
	 * @param top
	 * @return
	 * @throws IOException
	 */
	public static SearchResults searchQL( IndexSearcher searcher, String field_docno, String field_search, UnigramModel qmodel, double mu, int top ) throws IOException {
		List<String> terms = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for ( String word : qmodel ) {
			double prob = qmodel.probability( word );
			if ( prob > 0 ) {
				terms.add( word );
				weights.add( prob );
			}
		}
		return searchQL( searcher, field_docno, field_search, ArrayUtils.toStringArray( terms ), ArrayUtils.toDoubleArray( weights ), mu, top );
	}
	
	public static UnigramModel expandRM1( IndexSearcher searcher, String field_search, String[] qterms, double[] weights, double mu, int num_fbdoc, int num_fbword ) throws IOException {
		
		TreeMapSample qsample = new TreeMapSample();
		QLDirichletSmoothing sim = new QLDirichletSmoothing( mu );
		searcher.setSimilarity( sim );
		Query query = getQuery( field_search, qterms, weights );
		TopDocs topdocs = searcher.search( query, num_fbdoc );
		SearchResults results = getSearchResults( topdocs );
		UnigramModel corpus_model = EstimatedModel.MLE( getCorpusSample( searcher.getIndexReader(), field_search, true ) );
		for ( SearchResult result : results ) {
			UnigramSample docsample = getDocSample( searcher.getIndexReader(), result.getDocid(), field_search );
			UnigramModel docmodel_mle = EstimatedModel.MLE( docsample );
			UnigramModel docmodel_smoothed = EstimatedModel.DirichletSmoothing( docsample, corpus_model, mu );
			double docprob = 0;
			for ( int ix = 0 ; ix < qterms.length ; ix++ ) {
				double prob = docmodel_smoothed.probability( qterms[ix] );
				if ( prob > 0 ) {
					docprob += weights[ix] * Math.log( prob );
				}
			}
			if ( docprob != 0 ) {
				docprob = Math.exp( docprob );
				for ( String word : docmodel_mle ) {
					qsample.update( word, docmodel_mle.probability( word ) * docprob );
				}
			}
		}
		qsample.setLength();
		
		if ( num_fbword <= 0 ) {
			return EstimatedModel.MLE( qsample );
		}
		TreeMapSample topwords = new TreeMapSample();
		UnigramModel qmodel = new SortedUnigramModel( EstimatedModel.MLE( qsample ) );
		for ( String word : qmodel ) {
			topwords.update( word, qmodel.probability( word ) );
			if ( topwords.sizeVocabulary() >= num_fbword ) {
				break;
			}
		}
		topwords.setLength();
		
		return EstimatedModel.MLE( topwords );
		
	}
	
	public static UnigramModel expandRM1( IndexSearcher searcher, String field_search, String[] qterms, double mu, int num_fbdoc, int num_fbword ) throws IOException {
		double[] weights = new double[qterms.length];
		for ( int ix = 0 ; ix < weights.length ; ix++ ) {
			weights[ix] = 1.0 / qterms.length;
		}
		return expandRM1( searcher, field_search, qterms, weights, mu, num_fbdoc, num_fbword );
	}
	
	public static UnigramModel expandRM1( IndexSearcher searcher, String field_search, String text_query, Analyzer analyzer, double mu, int num_fbdoc, int num_fbword ) throws IOException {
		return expandRM1( searcher, field_search, AnalyzerUtils.tokenizeAsArray( text_query, analyzer ), mu, num_fbdoc, num_fbword );
	}
	
	public static UnigramModel expandRM1( IndexSearcher searcher, String field_search, UnigramModel qmodel, double mu, int num_fbdoc, int num_fbword ) throws IOException {
		List<String> terms = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for ( String word : qmodel ) {
			double prob = qmodel.probability( word );
			if ( prob > 0 ) {
				terms.add( word );
				weights.add( prob );
			}
		}
		return expandRM1( searcher, field_search, ArrayUtils.toStringArray( terms ), ArrayUtils.toDoubleArray( weights ), mu, num_fbdoc, num_fbword );
	}
	
	public static UnigramModel expandRM3( IndexSearcher searcher, String field_search, String[] qterms, double[] weights, double mu, int num_fbdoc, int num_fbword, double weight_qorg ) throws IOException {
		TreeMapSample sample_q = new TreeMapSample();
		for ( int ix = 0 ; ix < qterms.length ; ix++ ) {
			sample_q.update( qterms[ix], weights[ix] );
		}
		sample_q.setLength();
		UnigramModel model_q = EstimatedModel.MLE( sample_q );
		UnigramModel model_fb = expandRM1( searcher, field_search, qterms, weights, mu, num_fbdoc, num_fbword );
		return new MixtureModel( new UnigramModel[] { model_q, model_fb }, new double[] { weight_qorg, 1 - weight_qorg } );
	}
	
	public static UnigramModel expandRM3( IndexSearcher searcher, String field_search, String[] qterms, double mu, int num_fbdoc, int num_fbword, double weight_qorg ) throws IOException {
		double[] weights = new double[qterms.length];
		for ( int ix = 0 ; ix < weights.length ; ix++ ) {
			weights[ix] = 1.0 / qterms.length;
		}
		return expandRM3( searcher, field_search, qterms, weights, mu, num_fbdoc, num_fbword, weight_qorg );
	}
	
	public static UnigramModel expandRM3( IndexSearcher searcher, String field_search, String text_query, Analyzer analyzer, double mu, int num_fbdoc, int num_fbword, double weight_qorg ) throws IOException {
		return expandRM3( searcher, field_search, AnalyzerUtils.tokenizeAsArray( text_query, analyzer ), mu, num_fbdoc, num_fbword, weight_qorg );
	}
	
	public static UnigramModel expandRM3( IndexSearcher searcher, String field_search, UnigramModel qmodel, double mu, int num_fbdoc, int num_fbword, double weight_qorg ) throws IOException {
		List<String> terms = new ArrayList<String>();
		List<Double> weights = new ArrayList<Double>();
		for ( String word : qmodel ) {
			double prob = qmodel.probability( word );
			if ( prob > 0 ) {
				terms.add( word );
				weights.add( prob );
			}
		}
		return expandRM3( searcher, field_search, ArrayUtils.toStringArray( terms ), ArrayUtils.toDoubleArray( weights ), mu, num_fbdoc, num_fbword, weight_qorg );
	}
	
}
