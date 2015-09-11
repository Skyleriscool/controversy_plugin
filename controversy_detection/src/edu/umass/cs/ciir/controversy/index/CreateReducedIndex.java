package edu.umass.cs.ciir.controversy.index;

import java.io.File;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;

public class CreateReducedIndex {
	
	public static void main( String[] args ) {
		try {
			
			double rate = 0.10;
			
			Set<String> judged_entries = new TreeSet<String>();
			{
				String path_index = "D:/controversy/controversy_score_index";
				Directory dir = FSDirectory.open( new File( path_index ) );
				IndexReader index = DirectoryReader.open( dir );
				for ( int docid = 0 ; docid < index.maxDoc() ; docid++ ) {
					judged_entries.add( index.document( docid ).get( "title" ) );
				}
				index.close();
				dir.close();
			}
			
			Set<String> unjudged_entries = new TreeSet<String>();
			{
				String path_index = "C:/wikidump_index_random10";
				Directory dir = FSDirectory.open( new File( path_index ) );
				IndexReader index = DirectoryReader.open( dir );
				for ( int docid = 0 ; docid < index.maxDoc() ; docid++ ) {
					String title = index.document( docid ).get( "title" );
					if ( !judged_entries.contains( title ) ) {
						unjudged_entries.add( title );
					}
				}
				index.close();
				dir.close();
			}
			
			System.out.println( judged_entries.size() + "\t" + unjudged_entries.size() );
			
			{
				String path_index = "C:/wikidump_index_random10";
				Directory dir = FSDirectory.open( new File( path_index ) );
				Analyzer analyzer = TextAnalyzer.get( "alpha", "lc", "kstem", "indri stop", "no oov" );
				IndexWriterConfig index_config = new IndexWriterConfig( IRSettings.LUCENE_VERSION, analyzer );
				IndexWriter index = new IndexWriter( dir, index_config );
				
				Random random = new Random();
				int count = 0;
				for ( String title : unjudged_entries ) {
					if ( random.nextFloat() >= rate ) {
						count++;
						index.deleteDocuments( new Term( "title", title ) );
						if ( count % 1000 == 0 ) {
							System.out.println( count + "/" + unjudged_entries.size() );
						}
					}
				}
				
				index.forceMergeDeletes();
				
				System.out.println( index.numDocs() );
				
				index.close();
				dir.close();
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
