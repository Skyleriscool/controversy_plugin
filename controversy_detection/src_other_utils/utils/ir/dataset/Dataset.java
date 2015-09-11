package utils.ir.dataset;

import java.util.Map;
import java.io.IOException;

/**
 * <p>
 * Dataset is an interface for iteratively reading documents from a dataset. A dataset is a collection of documents; each document may contain several data
 * fields. Each document will be stored and returned as a Map, whose key and value are both Strings. The key should be case-insensitive.
 * </p>
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public interface Dataset {
	
	/**
	 * Get the next document in the dataset, or null if it is the end of the dataset. Each document is represented as a String to String map. The key is
	 * case-insensitive.
	 * 
	 * @return Next document in this dataset (or null if end of dataset).
	 * @throws IOException
	 */
	public Map<String, String> next() throws IOException;
	
	/**
	 * Close resources associated with the dataset. If you create a dataset from a stream or reader, you are supposed to close them by yourself.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;
	
}
