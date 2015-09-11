package edu.umass.cs.ciir.controversy.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import utils.ir.IRSettings;
import utils.ir.analysis.TextAnalyzer;
import utils.ir.lucene.TextField;
import edu.umass.cs.ciir.controversy.data.WikiDumpParser;
import edu.umass.cs.ciir.controversy.data.WikiDumpDocument;

/**
 * This is the main class that builds index for the wikidump collection. It skipps entries including ":" in their titles.
 * 
 * @author Jiepu Jiang
 * @version May 23, 2015
 * @see WikiDumpParser
 * @see WikiDumpDocument
 */
public class BuildIndexWikiDump {
	
	public static void main( String[] args ) {
		try {
			
			args = new String[] {
					"D:/wikidump",
					"D:/wikidump_index"
			};
			
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
	
	private static void process( File f, IndexWriter index_writer ) throws SAXException, IOException, ParserConfigurationException {
		if ( f.isFile() ) {
			
			InputStream instream = new FileInputStream( f );
			if ( f.getName().toLowerCase().endsWith( ".gz" ) ) {
				instream = new GZIPInputStream( instream );
			} else if ( f.getName().toLowerCase().endsWith( ".bz2" ) ) {
				instream = new BZip2CompressorInputStream( instream );
			}
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			saxParser.parse( instream, new WikiDumpParser() {
				
				private int count = 0;
				
				@Override
				public void processDocument( WikiDumpDocument doc ) {
					count++;
					if ( doc.isMainEntry() && !doc.hasRedirect() ) {
						// build index for this page
						try {
							Document d = new Document();
							d.add( new StringField( "entry", doc.getTitle().replaceAll( "\\s+", " " ).trim(), Store.YES ) );
							d.add( new StringField( "title", doc.getTitle().toLowerCase().replaceAll( "\\s+", " " ), Store.YES ) );
							d.add( new StringField( "id", doc.getId().toLowerCase().replaceAll( "\\s+", " " ), Store.YES ) );
							d.add( new TextField( "text", doc.getText(), false, false, false ) );
							index_writer.addDocument( d );
							System.out.println( " >> " + f.getName() + ", " + count + ", add entry " + doc.getTitle() );
						} catch ( Exception e ) {
							e.printStackTrace();
						}
					} else {
						System.out.println( " >> " + f.getName() + ", " + count + ", skip entry " + doc.getTitle() + ( doc.hasRedirect() ? " --> " + doc.getRedirect() : "" ) );
					}
				}
				
			} );
			
			instream.close();
			
		} else {
			for ( File file : f.listFiles() ) {
				process( file, index_writer );
			}
		}
	}
	
}
