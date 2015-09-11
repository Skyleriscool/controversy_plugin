package utils.ir.app;

import java.util.Map;

import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

import utils.KVPair;
import utils.ir.eval.Qrels;
import utils.ir.eval.Evaluation;
import utils.ir.eval.SearchResults;
import utils.ir.indri.IndriUtils;
import utils.ir.dataset.topic.TRECTopic;
import utils.ir.dataset.topic.RobustTrack;

public class SearchIndexRobust04Indri {
	
	public static void main( String[] args ) {
		try {
			
			String path_index = "C:/Users/Jiepu/Data/index_indri_robust04";
			
			QueryEnvironment index = new QueryEnvironment();
			index.addIndex( path_index );
			IndriUtils.setDirichletSmoothing( index, 1500 );
			
			Map<Integer, TRECTopic> topics = RobustTrack.loadTopicsMap( RobustTrack.Robust04 );
			Map<Integer, Qrels> topic_qrels = RobustTrack.loadQrels( RobustTrack.Robust04 );
			
			for ( int topicid : topic_qrels.keySet() ) {
				TRECTopic topic = topics.get( topicid );
				Qrels qrels = topic_qrels.get( topicid );
				String indri_query = IndriUtils.getIndriQuery( topic.get( "ti" ) );
				ScoredExtentResult[] indri_results = index.runQuery( indri_query, 1000 );
				String[] docnos = index.documentMetadata( indri_results, "docno" );
				SearchResults results = IndriUtils.getSearchResults( indri_results, docnos );
				System.out.println( results.toStringTrecFormat( topicid + "", "QL" ) );
				KVPair evals = Evaluation.evaluate( qrels, results.getResultList() );
				// System.out.println( topicid + "\t" + evals.getDouble( "nDCG@10" ) + "\t" + evals.getDouble( "avgPrec" ) );
				break;
			}
			
			index.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
