package edu.umass.cs.ciir.controversy.index;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.dataset.TrecWarcDataset;

public class BuildIndexClueweb09B {
	
	public static void main( String[] args ) {
		try {
			
			String pathsrc = args[0];
			String pathout = args[1];
			
			Analyzer analyzer = TextAnalyzer.get( "alpha", "lc", "kstem", "indri stop", "no oov" );
			
			Directory index_dir = FSDirectory.open( new File( pathout ) );
			IndexWriterConfig index_config = new IndexWriterConfig( IRSettings.LUCENE_VERSION, analyzer );
			IndexWriter index_writer = new IndexWriter( index_dir, index_config );
			
			process( new File( pathsrc ), index_writer );
			
			index_writer.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static void process( File f, IndexWriter index_writer ) throws IOException {
		
		if ( f.isDirectory() ) {
			
			for ( File file : f.listFiles() ) {
				process( file, index_writer );
			}
		} else {
			
			int count = 0;
			
			TrecWarcDataset dataset = new TrecWarcDataset( f, true, true );
			Map<String, String> doc = dataset.next();
			while ( doc != null ) {
				
				count++;
				if ( count % 1000 == 0 ) {
					System.out.println( " >> processing " + f.getAbsolutePath() + ", " + count + " documents finished" );
				}
				
				String docno = doc.get( "docno" );
				String url = doc.get( "url" ).trim().toLowerCase();
				String content = doc.get( "content" );
				
				Document d = new Document();
				d.add( new StringField( "docno", docno, Store.YES ) );
				d.add( new StringField( "url", url, Store.YES ) );
				d.add( new TextField( "content", content, Store.YES ) );
				index_writer.addDocument( d );
				
				doc = dataset.next();
				
			}
			dataset.close();
			
		}
	}
	
}
