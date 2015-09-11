package edu.umass.cs.ciir.controversy.database.entity;

public class URLRating {
	
	protected long requestid;
	protected long timestamp;
	protected int rating;
	
	public URLRating( long requestid, long timestamp, int rating ) {
		this.requestid = requestid;
		this.timestamp = timestamp;
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
	
	public int getRating() {
		return rating;
	}
	
	public void setRating( int rating ) {
		this.rating = rating;
	}
	
}
