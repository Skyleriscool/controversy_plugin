package edu.umass.cs.ciir.controversy.database.entity;

public class Request {
	
	protected long requestid;
	protected long timestamp;
	protected String ip;
	protected String userid;
	protected String url;
	protected String param;
	protected String text;
	protected String webpage;
	protected int texthash;
	
	public Request( String userid, String ip, String url, String text, String webpage, String param, long timestamp ) {
		this.userid = userid;
		this.ip = ip;
		this.url = url;
		this.param = param;
		this.text = text;
		this.texthash = text.hashCode();
		this.webpage = webpage;
		this.timestamp = timestamp;
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
	
	public String getIp() {
		return ip;
	}
	
	public void setIp( String ip ) {
		this.ip = ip;
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid( String userid ) {
		this.userid = userid;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl( String url ) {
		this.url = url;
	}
	
	public String getParam() {
		return param;
	}
	
	public void setParam( String param ) {
		this.param = param;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText( String text ) {
		this.text = text;
	}
	
	public int getTextHash() {
		return this.texthash;
	}
	
	public void setWebpage( String webpage ) {
		this.webpage = webpage;
	}
	
	public String getWebpage() {
		return this.webpage;
	}
	
}
