package utils.ir.dataset;

import java.util.Map;
import java.io.File;
import java.io.IOException;

import utils.StringUtils;

/**
 * Trectext format dataset. "docno" field will be extracted; "doc" stores the raw content of document; "content" field stores cleaned document (by removing
 * xml-style tags).
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class TrecTextDataset extends TrecDataset {
	
	public TrecTextDataset( File file, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		super( file, doc_bg, doc_ed, gzip );
	}
	
	public TrecTextDataset( File file, boolean gzip ) throws IOException {
		super( file, gzip );
	}
	
	public TrecTextDataset( String path, String doc_bg, String doc_ed, boolean gzip ) throws IOException {
		super( path, doc_bg, doc_ed, gzip );
	}
	
	public TrecTextDataset( String path, boolean gzip ) throws IOException {
		super( path, gzip );
	}
	
	/**
	 * Tags within this list will be removed along with all incorporated contents. By default, DOCNO and FILEID will be removed from TRECTEXT documents.
	 */
	public static String[] remove_tag_list = new String[] {
			"DOCNO", "FILEID"
	};
	
	public Map<String, String> next() throws IOException {
		Map<String, String> doc = super.next();
		if ( doc != null && doc.get( "doc" ) != null ) {
			String cleaned = StringUtils.removeTagsWithContents( doc.get( "doc" ), " ", remove_tag_list );
			cleaned = StringUtils.removeTags( cleaned, " " );
			doc.put( "content", cleaned );
		}
		return doc;
	}
	
}
