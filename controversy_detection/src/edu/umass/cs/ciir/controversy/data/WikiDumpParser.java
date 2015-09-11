package edu.umass.cs.ciir.controversy.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Parsing an wiki dump xml file using sax. Each document is extracted and will be processed using the specified processing method.
 * </p>
 * 
 * <p>
 * Callers should implement processDocument(WikiDumpDocument doc) to specify how to process each document.
 * </p>
 * 
 * @author Jiepu Jiang
 * @version May 23, 2015
 * @see WikiDumpDocument
 */
public abstract class WikiDumpParser extends DefaultHandler {
	
	public abstract void processDocument( WikiDumpDocument doc );
	
	private boolean inTitle;
	private boolean inId;
	private boolean inRevision;
	private boolean inText;
	
	private WikiDumpDocument doc;
	
	private void reset() {
		inTitle = false;
		inId = false;
		inRevision = false;
		inText = false;
		doc = new WikiDumpDocument();
	}
	
	public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {
		
		if ( qName.equalsIgnoreCase( "page" ) ) {
			reset();
		}
		
		if ( qName.equalsIgnoreCase( "title" ) ) {
			inTitle = true;
		}
		
		if ( qName.equalsIgnoreCase( "id" ) && !inRevision ) {
			inId = true;
		}
		
		if ( qName.equalsIgnoreCase( "redirect" ) && !inRevision ) {
			doc.setRedirect( attributes.getValue( "title" ) );
		}
		
		if ( qName.equalsIgnoreCase( "revision" ) ) {
			inRevision = true;
		}
		
		if ( qName.equalsIgnoreCase( "text" ) ) {
			inText = true;
		}
		
	}
	
	public void endElement( String uri, String localName, String qName ) throws SAXException {
		
		if ( qName.equalsIgnoreCase( "page" ) ) {
			// process this document
			processDocument( doc );
		}
		
		if ( qName.equalsIgnoreCase( "title" ) ) {
			inTitle = false;
		}
		
		if ( qName.equalsIgnoreCase( "id" ) && !inRevision ) {
			inId = false;
		}
		
		if ( qName.equalsIgnoreCase( "revision" ) ) {
			inRevision = false;
		}
		
		if ( qName.equalsIgnoreCase( "text" ) ) {
			inText = false;
		}
		
	}
	
	public void characters( char ch[], int start, int length ) throws SAXException {
		
		if ( inTitle ) {
			doc.setTitle( new String( ch, start, length ) );
		}
		
		if ( inId ) {
			doc.setId( new String( ch, start, length ) );
		}
		
		if ( inText ) {
			doc.appendText( new String( ch, start, length ) );
			doc.appendText( "\n" );
		}
		
	}
	
}
