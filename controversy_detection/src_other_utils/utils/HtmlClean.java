package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Utilities for cleaning an html document. Note that the implementation is based on matching of regular expression patterns, and thus it is less efficient
 * compared with a dom/sax implementation.
 * 
 * @author Jiepu Jiang
 * @version Mar 1, 2013
 */
public class HtmlClean {
	
	private final static Pattern pTag = Pattern.compile( "<[^>]+>" );
	private final static Pattern pHead = Pattern.compile( "<head(\\s[^>]+)?>.+?</head>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pTitle = Pattern.compile( "<title(\\s[^>]+)?>(.+?)</title>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pKeywords = Pattern.compile( "<meta[^>]+name=\"?keywords\"?[^>]+content=\"?([^>]+)\"?[^>]*>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pDescription = Pattern.compile( "<meta[^>]+name=\"?description\"?[^>]+content=\"?([^>]+)\"?[^>]*>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pImage = Pattern.compile( "<img\\s+[^>]+alt=\"(.+?)\"[^>]*>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pStyle = Pattern.compile( "<style(\\s[^>]+)?>.+?</style>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	private final static Pattern pScript = Pattern.compile( "<script(\\s[^>]+)?>.+?</script>", Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL );
	
	/**
	 * Clean an html webpage. It will: 1. extract "title", "keywords", "description" from head part, append to the text and remove the whole head part; 2.
	 * extract "alt" metadata from image tags and replace the original image tags; 3. remove all styles, javascripts; 4. remove all the rest html tags.
	 * 
	 * @param htmlText
	 *            An html text.
	 * @return A cleaned text.
	 */
	public static String clean( String htmlText ) {
		
		// extract useful metadata from html head
		StringBuilder sbHead = new StringBuilder();
		Matcher mHead = pHead.matcher( htmlText );
		while ( mHead.find() ) {
			String textHead = mHead.group();
			Matcher mTitle = pTitle.matcher( textHead );
			while ( mTitle.find() ) {
				sbHead.append( mTitle.group( 2 ) );
				sbHead.append( " . " );
			}
			Matcher mKeywords = pKeywords.matcher( textHead );
			while ( mKeywords.find() ) {
				sbHead.append( mKeywords.group( 1 ) );
				sbHead.append( " . " );
			}
			Matcher mDescription = pDescription.matcher( textHead );
			while ( mDescription.find() ) {
				sbHead.append( mDescription.group( 1 ) );
				sbHead.append( " . " );
			}
		}
		htmlText = mHead.replaceAll( " " );
		
		// extract image metadata from img tags
		Matcher mImage = pImage.matcher( htmlText );
		htmlText = mImage.replaceAll( " $1 " );
		
		// Remove style and javascript blocks
		Matcher mStyle = pStyle.matcher( htmlText );
		htmlText = mStyle.replaceAll( " . " );
		Matcher mScript = pScript.matcher( htmlText );
		htmlText = mScript.replaceAll( " . " );
		
		Matcher mTag = pTag.matcher( htmlText );
		htmlText = mTag.replaceAll( " " );
		
		// final clean
		String cleanText = sbHead.toString() + " " + htmlText;
		cleanText = StringEscapeUtils.unescapeXml( cleanText );
		cleanText = StringEscapeUtils.unescapeHtml3( cleanText );
		cleanText = StringEscapeUtils.unescapeHtml4( cleanText );
		cleanText = StringEscapeUtils.unescapeXml( cleanText );
		cleanText = StringEscapeUtils.unescapeHtml3( cleanText );
		cleanText = StringEscapeUtils.unescapeHtml4( cleanText );
		return cleanText;
		
	}
	
}
