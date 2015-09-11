package utils.ir.eval;

import java.util.*;

/**
 * QrelsInfo is an interface for a topic's relevant information.
 * 
 * @author Jiepu Jiang
 * @version Mar 5, 2013
 */
public interface QrelsInfo {
	
	/**
	 * Get a collection of all relevant documents.
	 * 
	 * @return
	 */
	public Collection<String> relevantDocuments();
	
	/**
	 * Get the relevance score of the specified document.
	 * 
	 * @param docno
	 * @return
	 */
	public double relevance( String docno );
	
}
