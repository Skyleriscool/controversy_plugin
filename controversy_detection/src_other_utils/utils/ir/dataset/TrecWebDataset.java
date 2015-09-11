package utils.ir.dataset;

import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.examples.HtmlToPlainText;

/**
 * TRECWEB format dataset. The "docno" field will be extracted; "doc" and "html" store the raw html content of document; "content" field stores cleaned (if
 * clearnhtml is true) or uncleaned (false) html contents.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TrecWebDataset extends TrecDataset {
	
	private boolean cleanhtml;
	
	public TrecWebDataset( File file, String doc_bg, String doc_ed, boolean gzip, boolean cleanhtml ) throws IOException {
		super( file, doc_bg, doc_ed, gzip );
		this.cleanhtml = cleanhtml;
	}
	
	public TrecWebDataset( File file, boolean gzip, boolean cleanhtml ) throws IOException {
		super( file, gzip );
		this.cleanhtml = cleanhtml;
	}
	
	public TrecWebDataset( String path, String doc_bg, String doc_ed, boolean gzip, boolean cleanhtml ) throws IOException {
		super( path, doc_bg, doc_ed, gzip );
		this.cleanhtml = cleanhtml;
	}
	
	public TrecWebDataset( String path, boolean gzip, boolean cleanhtml ) throws IOException {
		super( path, gzip );
		this.cleanhtml = cleanhtml;
	}
	
	public TrecWebDataset( File file, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		this( file, doc_bg, doc_ed, gzip, true );
	}
	
	public TrecWebDataset( File file, boolean gzip ) throws IOException {
		this( file, gzip, true );
	}
	
	public TrecWebDataset( String path, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		this( path, doc_bg, doc_ed, gzip, true );
	}
	
	public TrecWebDataset( String path, boolean gzip ) throws IOException {
		this( path, gzip, true );
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
	
	/** The regular expression for the main content part of the document. */
	private static Pattern pattern_content = Pattern.compile( "<DOC>.+?</DOCHDR>(.+)</DOC>", Pattern.CASE_INSENSITIVE + Pattern.DOTALL + Pattern.MULTILINE );
	private static HtmlToPlainText formatter = new HtmlToPlainText();
	
	public Map<String, String> next() throws IOException {
		Map<String, String> doc = super.next();
		if ( doc != null && doc.get( "doc" ) != null ) {
			Matcher mcontent = pattern_content.matcher( doc.get( "doc" ) );
			String html = "";
			if ( mcontent.find() ) {
				html = mcontent.group( 1 );
			}
			doc.put( "html", html );
			if ( cleanhtml ) {
				String cleaned = null;
				try {
					Document htmldoc = Jsoup.parse( html );
					cleaned = formatter.getPlainText( htmldoc );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				if ( cleaned == null ) {
					cleaned = html;
				}
				doc.put( "content", cleaned );
			} else {
				doc.put( "content", html );
			}
		}
		return doc;
	}
	
}
