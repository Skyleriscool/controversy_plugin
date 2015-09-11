package utils.ir.dataset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.examples.HtmlToPlainText;

import utils.ir.dataset.format.WarcRecord;

/**
 * WARC format dataset. The "docno" field will be extracted; "doc" and "html" store the raw html content of document; "url" stores URL of the resources;
 * "content" field stores cleaned (if clearnhtml is true) or uncleaned (false) html contents.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TrecWarcDataset implements Dataset {
	
	private boolean cleanhtml;
	private InputStream src;
	private DataInputStream instream;
	
	/**
	 * Constructor. Html documetn will be cleaned and stored as "content".
	 * 
	 * @param instream
	 * @param gzip
	 * @param cleanhtml
	 * @throws IOException
	 */
	public TrecWarcDataset( String path, boolean gzip, boolean cleanhtml ) throws IOException {
		this( new File( path ), gzip, cleanhtml );
	}
	
	/**
	 * Constructor. Html documetn will be cleaned and stored as "content".
	 * 
	 * @param instream
	 * @param gzip
	 * @param cleanhtml
	 * @throws IOException
	 */
	public TrecWarcDataset( File file, boolean gzip, boolean cleanhtml ) throws IOException {
		this.src = new FileInputStream( file );
		if ( gzip ) {
			this.src = new GZIPInputStream( src );
		}
		this.instream = new DataInputStream( src );
		this.cleanhtml = cleanhtml;
	}
	
	/**
	 * Constructor. Html documetn will be cleaned and stored as "content".
	 * 
	 * @param instream
	 * @param gzip
	 * @throws IOException
	 */
	public TrecWarcDataset( String path, boolean gzip ) throws IOException {
		this( new File( path ), gzip, true );
	}
	
	/**
	 * Constructor. Html documetn will be cleaned and stored as "content".
	 * 
	 * @param instream
	 * @param gzip
	 * @throws IOException
	 */
	public TrecWarcDataset( File file, boolean gzip ) throws IOException {
		this( file, gzip, true );
	}
	
	/**
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.instream.close();
		this.src.close();
	}
	
	/**
	 * @return Whether it cleans html document
	 */
	public boolean cleanhtml() {
		return cleanhtml;
	}
	
	/**
	 * @param cleanhtml
	 *            To set up the htmlclean settings
	 */
	public void setCleanhtml( boolean cleanhtml ) {
		this.cleanhtml = cleanhtml;
	}
	
	private static HtmlToPlainText formatter = new HtmlToPlainText();
	
	public Map<String, String> next() throws IOException {
		
		WarcRecord rec = WarcRecord.readNextWarcRecord( instream );
		while ( rec != null && rec.getHeaderMetadataItem( "WARC-TREC-ID" ) == null ) {
			rec = WarcRecord.readNextWarcRecord( instream );
		}
		if ( rec == null ) {
			return null;
		}
		
		HashMap<String, String> doc = new HashMap<String, String>();
		doc.put( "docno", rec.getHeaderMetadataItem( "WARC-TREC-ID" ) );
		doc.put( "url", rec.getHeaderMetadataItem( "WARC-Target-URI" ) );
		
		String content = rec.getContentUTF8();
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader( new StringReader( content ) );
		String line = reader.readLine();
		boolean content_start = false;
		while ( line != null ) {
			if ( content_start ) {
				sb.append( line );
				sb.append( "\n" );
			}
			if ( line.length() == 0 ) {
				content_start = true;
			}
			line = reader.readLine();
		}
		reader.close();
		
		doc.put( "doc", sb.toString() );
		doc.put( "html", doc.get( "doc" ) );
		if ( cleanhtml ) {
			String cleaned = null;
			try {
				Document htmldoc = Jsoup.parse( doc.get( "html" ) );
				cleaned = formatter.getPlainText( htmldoc );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			if ( cleaned == null ) {
				cleaned = doc.get( "html" );
			}
			doc.put( "content", cleaned );
		} else {
			doc.put( "content", doc.get( "html" ) );
		}
		return doc;
		
	}
	
}
