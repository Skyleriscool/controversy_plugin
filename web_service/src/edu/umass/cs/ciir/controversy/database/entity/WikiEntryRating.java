package edu.umass.cs.ciir.controversy.database.entity;

public class WikiEntryRating {
	
	protected long requestid;
	protected long timestamp;
	protected String entry;
	protected int rating;
	
	public WikiEntryRating( long requestid, long timestamp, String entry, int rating ) {
		this.requestid = requestid;
		this.timestamp = timestamp;
		this.entry = entry;
		this.rating = rating;
	}
	
	public long getRequestid() {
		return requestid;
	}
	
	public void setRequestid( long requestid ) {
		this.requestid = requestid;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp( long timestamp ) {
		this.timestamp = timestamp;
	}
	
	public String getEntry() {
		return entry;
	}
	
	public void setEntry( String entry ) {
		this.entry = entry;
	}
	
	public int getRating() {
		return rating;
	}
	
	public void setRating( int rating ) {
		this.rating = rating;
	}
	
}
