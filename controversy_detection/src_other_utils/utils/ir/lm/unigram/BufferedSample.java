package utils.ir.lm.unigram;

import java.io.IOException;
import java.util.Iterator;

/**
 * BufferedUnigramSample creates a buffered layer for any implementation of unigram sample. It can be used for environments which need repetitiously and random
 * access of language sample. A maximum buffer size can be set (if a max buffer size is set, it will not continue buffer information once buffer is full). By
 * default, no maximum buffer size will be specified, and thus the memory usage should be concerned in such cases.
 * 
 * @author Jiepu Jiang
 * @version Feb 12, 2015
 */
public class BufferedSample implements UnigramSample {
	
	/** Source unigram sample. */
	private UnigramSample src;
	
	/** Buffered unigram sample. */
	private TreeMapSample buffer;
	
	/** Max buffer size. */
	private int max_buffer;
	
	/**
	 * Constructor. By default, no maximum buffer size will be specified, and thus the memory usage should be concerned in such cases.
	 * 
	 * @param src
	 * @throws IOException
	 */
	public BufferedSample( UnigramSample src ) throws IOException {
		this( src, 0 );
	}
	
	/**
	 * Constructor. By default, no maximum buffer size will be specified, and thus the memory usage should be concerned in such cases.
	 * 
	 * @param src
	 * @param max_buffer
	 * @throws IOException
	 */
	public BufferedSample( UnigramSample src, int max_buffer ) throws IOException {
		this.src = src;
		this.max_buffer = max_buffer;
		this.buffer = new TreeMapSample( src.length() );
	}
	
	public double frequency( String word ) throws IOException {
		if ( buffer.containsWord( word ) ) {
			return buffer.frequency( word );
		}
		double freq = src.frequency( word );
		if ( max_buffer <= 0 || buffer.sizeVocabulary() < max_buffer ) {
			buffer.setFrequency( word, freq );
		}
		return freq;
	}
	
	public double length() {
		return buffer.length();
	}
	
	public long sizeVocabulary() throws IOException {
		return src.sizeVocabulary();
	}
	
	public Iterator<String> iterator() {
		return src.iterator();
	}
	
}
