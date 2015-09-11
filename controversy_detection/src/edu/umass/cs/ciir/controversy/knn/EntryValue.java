package edu.umass.cs.ciir.controversy.knn;

/**
 * EntryValue stores a String entry and its associated double value (e.g., a wikipedia entry and its similarity score).
 * 
 * @author Jiepu Jiang
 * @version May 25, 2015
 */
public class EntryValue {
	
	protected String entry;
	protected double value;
	
	public EntryValue( String entry, double score ) {
		this.entry = entry;
		this.value = score;
	}
	
	public String getEntry() {
		return entry;
	}
	
	public void setEntry( String entry ) {
		this.entry = entry;
	}
	
	public double getScore() {
		return value;
	}
	
	public void setScore( double score ) {
		this.value = score;
	}
	
}
