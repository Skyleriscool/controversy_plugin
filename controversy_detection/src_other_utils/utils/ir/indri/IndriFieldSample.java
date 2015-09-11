package utils.ir.indri;

import java.util.Iterator;
import java.io.IOException;

import utils.ir.lm.unigram.UnigramSample;
import lemurproject.indri.QueryEnvironment;

/**
 * Loading a corpus language sample from an indri index. Note that numWords() and words() have not yet been implemented due to the lack of certain API support
 * in Indri. Besides, there's no efficient way of couting corpus length by fields. So, no matter you set a field name or not, length() returns the length of the
 * whole corpus.
 * 
 * @author Jiepu Jiang
 * @version Feb 16, 2015
 */
public class IndriFieldSample implements UnigramSample {
	
	protected QueryEnvironment index;
	protected String field;
	protected double length;
	
	/**
	 * Constructor.
	 * 
	 * @param index
	 * @throws Exception
	 */
	public IndriFieldSample( QueryEnvironment index ) throws Exception {
		this( index, null );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param index
	 * @param field
	 * @throws Exception
	 */
	public IndriFieldSample( QueryEnvironment index, String field ) throws Exception {
		this.index = index;
		this.field = field;
		this.length = index.termCount();
	}
	
	private static String getExpression( String field, String word ) {
		String exp = "\"" + word + "\"";
		if ( field != null ) {
			exp = exp + ".(" + field + ")";
		}
		return exp;
	}
	
	public double frequency( String word ) throws IOException {
		double freq = 0;
		String expression = getExpression( field, word );
		try {
			freq = index.expressionCount( expression );
		} catch ( Exception e ) {
			IOException ioe = new IOException( "Cannot estimate the frequeny of expression: " + expression );
			ioe.setStackTrace( e.getStackTrace() );
			throw ioe;
		}
		return freq;
	}
	
	public double length() throws IOException {
		return this.length;
	}
	
	public Iterator<String> iterator() {
		return null;
	}
	
	public long sizeVocabulary() throws IOException {
		return -1;
	}
	
}
