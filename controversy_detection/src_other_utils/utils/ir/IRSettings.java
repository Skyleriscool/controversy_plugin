package utils.ir;

import org.apache.lucene.util.Version;

/**
 * Environmental settings.
 * 
 * @author Jiepu Jiang
 * @version Feb 8, 2015
 */
public class IRSettings {
	
	/** The default charset encoding is UTF-8. */
	public static String CHARSET = "UTF-8";
	
	/** [OOV] is the token used for stopwords (to be compatible with indri). */
	public static String TOKEN_STOPWORDS = "[OOV]";
	
	/** Default apache Lucene version. */
	public static Version LUCENE_VERSION = Version.LUCENE_4_10_3;
	
}
