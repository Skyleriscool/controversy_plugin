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
import utils.ir.lm.unigram.UnigramModel;
import utils.ir.lucene.LuceneUtils;
import utils.ir.lucene.similarity.QLDirichletSmoothing;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.analysis.AnalyzerUtils;
import utils.ir.dataset.topic.TRECTopic;
import utils.ir.dataset.topic.RobustTrack;

public class SearchIndexRobust04LuceneRM3 {
	
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
				UnigramModel qmodel = LuceneUtils.expandRM3( searcher, "content", topic.get( "ti" ), analyzer, 1000, 10, 20, 0.5 );
				SearchResults results = LuceneUtils.searchQL( searcher, "docno", "content", qmodel, 1000, 1000 );
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
