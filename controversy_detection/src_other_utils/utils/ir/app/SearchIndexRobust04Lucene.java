package utils.ir.app;

import java.io.File;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;

import utils.KVPair;
import utils.ir.eval.Qrels;
import utils.ir.eval.Evaluation;
import utils.ir.eval.SearchResults;
import utils.ir.lucene.LuceneUtils;
import utils.ir.lucene.similarity.QLDirichletSmoothing;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.analysis.AnalyzerUtils;
import utils.ir.dataset.topic.TRECTopic;
import utils.ir.dataset.topic.RobustTrack;

public class SearchIndexRobust04Lucene {
	
	public static void main( String[] args ) {
		try {
			
			String path_index = "C:/Users/Jiepu/Data/index_lucene_robust04";
			
			Directory dir = FSDirectory.open( new File( path_index ) );
			IndexReader index = DirectoryReader.open( dir );
			
			IndexSearcher searcher = new IndexSearcher( index );
			QLDirichletSmoothing similarity = new QLDirichletSmoothing( 1000 );
			searcher.setSimilarity( similarity );
			
			TextAnalyzer analyzer = TextAnalyzer.get( "std tk", "lc", "kstem", "indri stop", "no oov" );
			
			Map<Integer, TRECTopic> topics = RobustTrack.loadTopicsMap( RobustTrack.Robust04 );
			Map<Integer, Qrels> topic_qrels = RobustTrack.loadQrels( RobustTrack.Robust04 );
			
			for ( int topicid : topic_qrels.keySet() ) {
				TRECTopic topic = topics.get( topicid );
				Qrels qrels = topic_qrels.get( topicid );
				String[] terms = AnalyzerUtils.tokenizeAsArray( topic.get( "ti" ), analyzer );
				double[] weights = new double[terms.length];
				for ( int ix = 0 ; ix < weights.length ; ix++ ) {
					weights[ix] = 1.0 / terms.length;
				}
				Query lucene_query = LuceneUtils.getQuery( "content", terms, weights );
				// System.out.println(lucene_query);
				TopDocs luceneresults = searcher.search( lucene_query, 1000 );
				SearchResults results = LuceneUtils.getSearchResults( luceneresults, 1000 );
				results.setResultsDocno( index, "docno", null );
				// for ( SearchResult result : results ) {
				// result.setScore( QLDrichletSmoothing.rescaleScore( result.getScore(), index, "content", terms ) );
				// }
				// System.out.println( results.toStringTrecFormat( topicid + "", "QL" ) );
				KVPair evals = Evaluation.evaluate( qrels, results.getResultList() );
				System.out.println( topicid + "\t" + evals.getDouble( "nDCG@10" ) + "\t" + evals.getDouble( "avgPrec" ) );
			}
			
			index.close();
			dir.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
