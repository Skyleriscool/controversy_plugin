package edu.umass.cs.ciir.controversy.knn.sim;

import java.io.IOException;
import java.util.Map;
import java.util.List;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

public interface IndexSearcher {
	
	public List<EntryValue> search( Object query, int topentries, Map<String, Object> info );
	
	public void close() throws IOException;
	
}
