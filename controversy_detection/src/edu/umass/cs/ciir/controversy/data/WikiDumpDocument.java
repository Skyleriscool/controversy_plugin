package edu.umass.cs.ciir.controversy.data;

/**
 * A parsed document from the Wiki dump file. I did not extract all fields.
 * 
 * @author Jiepu Jiang
 * @version May 23, 2015
 */
public class WikiDumpDocument {
	
	protected String title;
	protected String id;
	protected String redirect;
	protected StringBuilder text;
	
	public WikiDumpDocument() {
		this.text = new StringBuilder();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "title: " + title + "\n" );
		sb.append( "id: " + id + "\n" );
		sb.append( "redirect: " + redirect + "\n" );
		sb.append( "text: " + text );
		return sb.toString();
	}
	
	/**
	 * Whether it is a main entry. Entries that are not main entries include ":", e.g., "Talk:", "Category Talk:".
	 * 
	 * @return
	 */
	public boolean isMainEntry() {
		return !title.contains( ":" );
	}
	
	/**
	 * Whether it is redirected to some other page.
	 * 
	 * @return
	 */
	public boolean hasRedirect() {
		return redirect != null;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle( String title ) {
		this.title = title;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId( String id ) {
		this.id = id;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	public void setRedirect( String redirect ) {
		this.redirect = redirect;
	}
	
	public String getText() {
		return text.toString();
	}
	
	public void appendText( String text ) {
		this.text.append( text );
	}
	
}
