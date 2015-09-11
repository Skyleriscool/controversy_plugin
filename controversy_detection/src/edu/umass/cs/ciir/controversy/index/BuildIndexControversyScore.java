package edu.umass.cs.ciir.controversy.index;

import java.io.BufferedReader;
import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.IOUtils;
import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;

public class BuildIndexControversyScore {
	
	public static void main( String[] args ) {
		try {
			
			args = new String[] {
					"C:/Users/Jiepu/Downloads/allAnnotations-redirectsFixed.txt",
					"D:/controversy_score_index",
			};
			
			String pathsrc = args[0];
			String pathout = args[1];
			
			Analyzer analyzer = TextAnalyzer.get( "alpha", "lc", "kstem", "indri stop", "no oov" );
			
			Directory index_dir = FSDirectory.open( new File( pathout ) );
			IndexWriterConfig index_config = new IndexWriterConfig( IRSettings.LUCENE_VERSION, analyzer );
			IndexWriter index_writer = new IndexWriter( index_dir, index_config );
			
			BufferedReader reader = IOUtils.getBufferedReader( pathsrc );
			String line = reader.readLine();
			while ( line != null ) {
				String[] splits = line.split( "\t" );
				if ( splits.length == 2 ) {
					String entry = splits[0].toLowerCase().replaceAll( "\\s+", " " );
					float score = Float.parseFloat( splits[1] );
					if ( score < 0 ) {
						score = 0;
					}
					Document doc = new Document();
					doc.add( new StringField( "title", entry, Store.YES ) );
					doc.add( new FloatField( "score", score, Store.YES ) );
					index_writer.addDocument( doc );
				} else {
					System.out.println( line );
				}
				line = reader.readLine();
			}
			reader.close();
			
			index_writer.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
