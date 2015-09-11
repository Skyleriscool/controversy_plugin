package edu.umass.cs.ciir.controversy.experiment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;

import edu.umass.cs.ciir.controversy.knn.KNNScorer;
import edu.umass.cs.ciir.controversy.knn.aggregation.Generative;
import edu.umass.cs.ciir.controversy.knn.aggregation.KNNAggregation;
import edu.umass.cs.ciir.controversy.knn.db.ControversyDatabase;
import edu.umass.cs.ciir.controversy.knn.db.LuceneControversyDatabase;
import edu.umass.cs.ciir.controversy.knn.sim.LuceneQLSearcher;
import edu.umass.cs.ciir.controversy.knn.sim.LuceneTopWordsQuery;

import utils.IOUtils;
import utils.StatUtils;
import utils.ir.lucene.LuceneUtils;
import utils.ir.analysis.TextAnalyzer;

public class EvaluateSpeed {
	
	public static void main( String[] args ) {
		try {
			
			String path_index = "D:/controversy/index_clueweb09_lucene";
			String path_wikiindex = "C:/wikidump_index_merge/";
			
			int topwords = 35;
			int topentries = 50;
			
			Directory dir = FSDirectory.open( new File( path_index ) );
			IndexReader index = DirectoryReader.open( dir );
			
			LuceneTopWordsQuery qc = new LuceneTopWordsQuery( TextAnalyzer.get( "alpha", "lc", "indri stop", "kstem" ), "text" );
			LuceneQLSearcher searcher = new LuceneQLSearcher( path_wikiindex, 1500, "title", "entry", "text" );
			ControversyDatabase db = new LuceneControversyDatabase( "D:/controversy/controversy_score_index", "title", "score" );
			KNNAggregation aggregation = new Generative();
			KNNScorer scorer = new KNNScorer( qc, searcher, db, aggregation );
			
			List<String[]> instances = getInstances( index );
			evaluate( instances, scorer, topwords, topentries );
			
			index.close();
			dir.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static String getContent( IndexReader index, String url ) throws IOException {
		int docid = LuceneUtils.find( index, "url", url.toLowerCase() );
		if ( docid >= 0 ) {
			String content = index.document( docid ).get( "content" );
			return content;
		}
		return null;
	}
	
	private static List<String[]> getInstances( IndexReader index ) throws IOException {
		
		Map<String, List<Double>> url_ratings = new TreeMap<>();
		
		InputStream instream = new EvaluateSpeed().getClass().getResourceAsStream( "url_rating" );
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine(); // skip the first line in the file
		while ( ( line = reader.readLine() ) != null ) {
			String[] splits = line.split( "\t" );
			String url = splits[3].toLowerCase();
			double rating = Double.parseDouble( splits[5] );
			if ( !url.startsWith( "http://en.wikipedia.org" ) ) {
				if ( !url_ratings.containsKey( url ) ) {
					url_ratings.put( url, new ArrayList<Double>() );
				}
				url_ratings.get( url ).add( rating );
			}
		}
		reader.close();
		instream.close();
		
		List<String[]> instances = new ArrayList<String[]>();
		for ( String url : url_ratings.keySet() ) {
			String content = getContent( index, url );
			String rating = "no";
			if ( StatUtils.mean( url_ratings.get( url ) ) < 2.5 ) {
				rating = "yes";
			}
			if ( content != null ) {
				instances.add( new String[] { url, content, rating } );
			}
		}
		
		return instances;
		
	}
	
	private static void evaluate( List<String[]> instances, KNNScorer scorer, int topwords, int topentries ) throws IOException {
		long time_bg = System.currentTimeMillis();
		for ( String[] instance : instances ) {
			scorer.getScore( instance[1], topwords, topentries );
		}
		long time_ed = System.currentTimeMillis();
		double speed = ( time_ed - time_bg ) / ( 1000.0 * instances.size() );
		System.out.println( speed );
	}
	
}
