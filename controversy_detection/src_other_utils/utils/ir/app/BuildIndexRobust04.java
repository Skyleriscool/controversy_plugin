package utils.ir.app;

import java.io.File;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.dataset.Dataset;
import utils.ir.dataset.DirectoryDataset;
import utils.ir.dataset.TrecTextDataset;
import utils.ir.lucene.TextField;

public class BuildIndexRobust04 {
	
	public static void main( String[] args ) {
		try {
			
			String path_dataset = "D:/collection/trec.adhoc/collection.robust04";
			String path_index = "D:/index_lucene_robust04";
			
			Directory dir = FSDirectory.open( new File( path_index ) );
			TextAnalyzer analyzer = TextAnalyzer.get( "std tk", "lc", "kstem", "indri stop", "no oov" );
			
			IndexWriterConfig config = new IndexWriterConfig( IRSettings.LUCENE_VERSION, analyzer );
			config.setOpenMode( IndexWriterConfig.OpenMode.CREATE );
			IndexWriter index = new IndexWriter( dir, config );
			
			Dataset dataset = new DirectoryDataset( new File( path_dataset ), TrecTextDataset.class );
			Map<String, String> doc = dataset.next();
			while ( doc != null ) {
				Document d = new Document();
				d.add( new StringField( "docno", doc.get( "docno" ), Store.YES ) );
				d.add( new TextField( "content", doc.get( "content" ), true, true, true ) );
				System.out.println( doc.get( "docno" ) );
				index.addDocument( d );
				doc = dataset.next();
			}
			dataset.close();
			
			index.close();
			dir.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
