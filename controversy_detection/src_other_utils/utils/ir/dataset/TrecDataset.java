package utils.ir.dataset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import utils.IOUtils;
import utils.StringUtils;

/**
 * <p>
 * TrecDataset is the generic Dataset implementation for all TREC format datasets. It only recognize the start and the end of a document and extract docno of
 * the document from the collection file. The whole document's content, including the start and end tags will be stored as a single field "doc"; the docno will
 * be stored as a field "docno". The default start and end tags are &lt;DOC&gt; and &lt;/DOC&gt;.
 * </p>
 * <p>
 * Subclasses are expected to extract specific metadata from the DOC content according to the specific TREC format. Squirrel includes implementation for the
 * following TREC formats:
 * </p>
 * <ul>
 * <li>
 * <h3>Trectext format (edu.pitt.sis.iris.squirrel.TrecTextDataset)</h3>
 * "docno" field will be extracted; "doc" stores the raw content of document; "content" field stores cleaned document (by removing all xml-style tags).</li>
 * <li>
 * <h3>Trecweb format (edu.pitt.sis.iris.squirrel.TrecWebDataset)</h3>
 * "docno" field will be extracted; "doc" and "html" store the raw html content of document; "content" field stores cleaned document (by removing html tags,
 * scripts and extracting metadata from headers and tags).</li>
 * <li>
 * <h3>Warc format (edu.pitt.sis.iris.squirrel.TrecWarcDataset)</h3>
 * "docno" field will be extracted; "doc" and "html" store the raw html content of document; "content" field stores cleaned document (by removing html tags,
 * scripts and extracting metadata from headers and tags).</li>
 * </ul>
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TrecDataset implements Dataset {
	
	protected InputStream instream;
	protected BufferedReader reader;
	
	protected String line_doc_bg;
	protected String line_doc_ed;
	
	/**
	 * Create an object for reading a trec-format dataset. You can specify the start and end tag for each document.
	 * 
	 * @param file
	 * @param doc_bg
	 * @param doc_ed
	 * @param gzip
	 * @throws IOException
	 */
	public TrecDataset( File file, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		this.instream = new FileInputStream( file );
		if ( gzip ) {
			this.instream = new GZIPInputStream( this.instream );
		}
		this.reader = IOUtils.getBufferedReader( this.instream );
		this.line_doc_bg = doc_bg;
		this.line_doc_ed = doc_ed;
	}
	
	/**
	 * Create an object for reading a trec-format dataset. You can specify the start and end tag for each document.
	 * 
	 * @param file
	 * @param gzip
	 * @throws IOException
	 */
	public TrecDataset( File file, boolean gzip ) throws IOException {
		this( file, "<DOC>", "</DOC>", gzip );
	}
	
	/**
	 * Create an object for reading a trec-format dataset. You can specify the start and end tag for each document.
	 * 
	 * @param path
	 * @param doc_bg
	 * @param doc_ed
	 * @param gzip
	 * @throws IOException
	 */
	public TrecDataset( String path, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		this.instream = new FileInputStream( path );
		if ( gzip ) {
			this.instream = new GZIPInputStream( this.instream );
		}
		this.reader = IOUtils.getBufferedReader( this.instream );
		this.line_doc_bg = doc_bg;
		this.line_doc_ed = doc_ed;
	}
	
	/**
	 * Create an object for reading a trec-format dataset. You can specify the start and end tag for each document.
	 * 
	 * @param path
	 * @param gzip
	 * @throws IOException
	 */
	public TrecDataset( String path, boolean gzip ) throws IOException {
		this( path, "<DOC>", "</DOC>", gzip );
	}
	
	public Map<String, String> next() throws IOException {
		boolean indoc = false;
		StringBuilder buffer = null;
		String line = reader.readLine();
		while ( line != null ) {
			if ( line.equalsIgnoreCase( line_doc_bg ) ) {
				indoc = true;
				buffer = new StringBuilder();
			}
			if ( indoc ) {
				buffer.append( line );
				buffer.append( "\n" );
				if ( line.equalsIgnoreCase( line_doc_ed ) ) {
					break;
				}
			}
			line = reader.readLine();
		}
		if ( buffer == null ) {
			return null;
		}
		HashMap<String, String> doc = new HashMap<String, String>();
		String content = buffer.toString();
		doc.put( "doc", content );
		String docno = StringUtils.extractFirst( content, "<DOCNO>(.+?)</DOCNO>", 1 );
		if ( docno != null ) {
			doc.put( "docno", docno.trim() );
		}
		return doc;
	}
	
	public void close() throws IOException {
		reader.close();
		instream.close();
	}
	
}
