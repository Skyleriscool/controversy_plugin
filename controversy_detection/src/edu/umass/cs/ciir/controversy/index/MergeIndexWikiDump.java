package edu.umass.cs.ciir.controversy.index;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;

public class MergeIndexWikiDump {
	
	public static void main( String[] args ) {
		try {
			
			String pathsrc = args[0];
			String pathout = args[1];
			
			Analyzer analyzer = TextAnalyzer.get( "alpha", "lc", "kstem", "indri stop", "no oov" );
			
			Directory index_dir = FSDirectory.open( new File( pathout ) );
			IndexWriterConfig index_config = new IndexWriterConfig( IRSettings.LUCENE_VERSION, analyzer );
			IndexWriter index_writer = new IndexWriter( index_dir, index_config );
			
			for ( File f : new File( pathsrc ).listFiles() ) {
				System.out.println( " >> adding " + f.getAbsolutePath() );
				Directory dir = FSDirectory.open( f );
				index_writer.addIndexes( dir );
			}
			
			index_writer.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
