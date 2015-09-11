package utils.crawl;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Random;

import org.apache.http.client.methods.HttpGet;

/**
 * <p>
 * HttpRequestHeaderInfo stores http request header information. It contains methods of setting headers to simulate frequently used clients (e.g. IE, firefox,
 * Chrome, Opera). It can also automatically generate headers that look like a real browser's header info. Some of the websites will check the client's header
 * information and only allow visit from acceptable clients (e.g. web browsers). In such case, this class can be used to disguise the crawler as a valid client.
 * </p>
 * <p>
 * One can use edu.pitt.sis.iris.utils.crawl.BrowserHeaderDetector to track down the header information of web browsers.
 * </p>
 * 
 * @author Jiepu Jiang
 * @version Mar 1, 2013
 * @see utils.crawl.BrowserHeaderDetector
 */
public class HttpRequestHeaderInfo {
	
	/** Types of clients. */
	public enum ClientType {
		IE, Firefox, Chrome, Opera, Safari
	}
	
	private String user_agent;
	private String accept;
	private String accept_language;
	private String accept_encoding;
	private String connection;
	
	public String userAgent() {
		return this.user_agent;
	}
	
	public String accept() {
		return this.accept;
	}
	
	public String acceptLanguage() {
		return this.accept_language;
	}
	
	public String acceptEncoding() {
		return this.accept_encoding;
	}
	
	public String connection() {
		return this.connection;
	}
	
	private HttpRequestHeaderInfo() {
	}
	
	/**
	 * Fill an http get request with the header information.
	 * 
	 * @param get
	 *            An http get request
	 */
	public void fill( HttpGet get ) {
		get.addHeader( "User-Agent", user_agent );
		get.addHeader( "Accept", accept );
		get.addHeader( "Accept-Language", accept_language );
		get.addHeader( "Accept-Encoding", accept_encoding );
		get.addHeader( "Connection", connection );
	}
	
	/**
	 * Create an http get request with the header information to the specified URI.
	 * 
	 * @param uri
	 *            URI of resource
	 * @return An http get request
	 */
	public HttpGet create( java.net.URI uri ) {
		HttpGet get = new HttpGet();
		fill( get );
		get.setURI( uri );
		return get;
	}
	
	/**
	 * Create an http get request with the header information to the specified URI.
	 * 
	 * @param url
	 *            URL of resource in String
	 * @return An http get request
	 * @throws URISyntaxException
	 */
	public HttpGet create( String url ) throws URISyntaxException {
		return create( new URI( url ) );
	}
	
	/**
	 * @return A default http request header information
	 */
	public static HttpRequestHeaderInfo get() {
		return get( false );
	}
	
	/** Random generator for client type. */
	protected static Random rdm_client_type = new Random();
	
	/**
	 * @param random_client
	 *            Whether to randomize the client's header information
	 * @return An http request header information for randomized client
	 */
	public static HttpRequestHeaderInfo get( boolean random_client ) {
		ClientType[] clients = new ClientType[] {
				ClientType.IE, ClientType.Firefox, ClientType.Chrome, ClientType.Opera, ClientType.Safari
		};
		ClientType client = random_client ? clients[rdm_client_type.nextInt( clients.length )] : clients[0];
		return get( client, random_client );
	}
	
	/**
	 * @param client
	 *            Type of client
	 * @return Http request header information for the specified browser (will not randomize header info).
	 */
	public static HttpRequestHeaderInfo get( ClientType client ) {
		return get( client, false );
	}
	
	/**
	 * @param client
	 *            Type of client.
	 * @param random_client
	 *            Whether to randomize user_agent info.
	 * @return Http request header information for the specified browser.
	 */
	public static HttpRequestHeaderInfo get( ClientType client, boolean random_client ) {
		if ( client == ClientType.IE ) {
			return getIE( random_client );
		} else if ( client == ClientType.Firefox ) {
			return getFirefox( random_client );
		} else if ( client == ClientType.Chrome ) {
			return getChrome( random_client );
		} else if ( client == ClientType.Opera ) {
			return getOpera( random_client );
		} else if ( client == ClientType.Safari ) {
			return getSafari( random_client );
		}
		return null;
	}
	
	/** Random generator for headers simulating IE. */
	protected static Random rdm_ie = new Random();
	
	/**
	 * @param randomize
	 *            Whether to randomize user_agent info.
	 * @return Http request header information for IE browser.
	 */
	private static HttpRequestHeaderInfo getIE( boolean randomize ) {
		String[] ie_version = new String[] {
				"MSIE 9.0", "MSIE 8.0", "MSIE 7.0"
		};
		String[] os_version = new String[] {
				"Windows NT 6.1", null, "Windows NT 6.0", "Windows NT 5.2", "Windows NT 5.1"
		};
		String[] os_64bit = new String[] {
				"WOW64", null, "WIN32"
		};
		String val_ie_version = randomize ? ie_version[rdm_ie.nextInt( ie_version.length )] : ie_version[0];
		String val_os_version = randomize ? os_version[rdm_ie.nextInt( os_version.length )] : os_version[0];
		String val_os_64bit = randomize ? os_64bit[rdm_ie.nextInt( os_64bit.length )] : os_64bit[0];
		HttpRequestHeaderInfo info = new HttpRequestHeaderInfo();
		info.user_agent = "Mozilla/5.0 (compatible";
		if ( val_ie_version != null ) {
			info.user_agent = info.user_agent + "; " + val_ie_version;
		}
		if ( val_os_version != null ) {
			info.user_agent = info.user_agent + "; " + val_os_version;
		}
		if ( val_os_64bit != null ) {
			info.user_agent = info.user_agent + "; " + val_os_64bit;
		}
		info.user_agent = info.user_agent + "; Trident/5.0)";
		info.accept = "text/html, application/xhtml+xml, */*";
		info.accept_language = "en-US";
		// info.accept_encoding = "gzip, deflate";
		info.accept_encoding = "gzip";
		info.connection = "Keep-Alive";
		return info;
	}
	
	/** Random generator for headers simulating Firefox. */
	protected static Random rdm_ff = new Random();
	
	/**
	 * @param randomize
	 *            Whether to randomize user_agent info.
	 * @return Http request header information for Firefox browser.
	 */
	private static HttpRequestHeaderInfo getFirefox( boolean randomize ) {
		String[] ff_version = new String[] {
				"Firefox/11.0", "Firefox/10.0", "Firefox/9.0", "Firefox/8.0", "Firefox/7.0", "Firefox/6.0",
				"Firefox/5.0", "Firefox/4.0.1", "Firefox/4.0.0", "Firefox/3.7.3", "Firefox/3.6.2",
				"Firefox/3.6.1", "Firefox/3.5.0", "Firefox/3.3.2", "Firefox/3.1.2", "Firefox/3.0.3"
		};
		String[] os_version = new String[] {
				"X11; Linux x86_64; rv:5.0", null, "Windows NT 6.1; WOW64; rv:2.0.1", "Windows NT 6.1; Win32; rv:2.0.1",
				"Windows NT 6.0; WOW64; rv:2.0.1", "Windows NT 6.0; Win32; rv:2.0.1", "Windows NT 5.2; WOW64; rv:2.0.1",
				"Windows NT 5.2; Win32; rv:2.0.1", "Windows NT 5.1; WOW64; rv:2.0.1", "Windows NT 5.1; Win32; rv:2.0.1"
		};
		String val_os_version = randomize ? os_version[rdm_ff.nextInt( os_version.length )] : os_version[0];
		String val_ff_version = randomize ? ff_version[rdm_ff.nextInt( ff_version.length )] : ff_version[0];
		HttpRequestHeaderInfo info = new HttpRequestHeaderInfo();
		info.user_agent = "Mozilla/5.0 (" + val_os_version + ") Gecko/20100101 " + val_ff_version;
		info.accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
		info.accept_language = "en-us,en;q=0.5";
		// info.accept_encoding = "gzip, deflate";
		info.accept_encoding = "gzip";
		info.connection = "Keep-Alive";
		return info;
	}
	
	/** Random generator for headers simulating Chrome. */
	protected static Random rdm_chrome = new Random();
	
	/**
	 * @param randomize
	 *            Whether to randomize user_agent info.
	 * @return Http request header information for Chrome browser.
	 */
	private static HttpRequestHeaderInfo getChrome( boolean randomize ) {
		String[] os_version = new String[] {
				"X11; Linux x86_64", null, "Windows NT 6.1; WOW64", "Windows NT 6.1; Win32",
				"Windows NT 6.0; WOW64", "Windows NT 6.0; Win32", "Windows NT 5.2; WOW64",
				"Windows NT 5.2; Win32", "Windows NT 5.1; WOW64", "Windows NT 5.1; Win32",
		};
		String val_os_version = randomize ? os_version[rdm_chrome.nextInt( os_version.length )] : os_version[0];
		HttpRequestHeaderInfo info = new HttpRequestHeaderInfo();
		info.user_agent = "Mozilla/5.0 (" + val_os_version + ") AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.112 Safari/534.30";
		info.accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
		info.accept_language = "en-US,en;q=0.8";
		// info.accept_encoding = "gzip, deflate";
		info.accept_encoding = "gzip";
		info.connection = "Keep-Alive";
		return info;
	}
	
	/** Random generator for headers simulating opera. */
	protected static Random rdm_opera = new Random();
	
	/**
	 * @param randomize
	 *            Whether to randomize user_agent info (not implemented, will always return the same info).
	 * @return Http request header information for Opera browser.
	 */
	private static HttpRequestHeaderInfo getOpera( boolean randomize ) {
		String[] os_version = new String[] {
				"X11; Linux x86_64", null, "Windows NT 6.1; WOW64", "Windows NT 6.1; Win32",
				"Windows NT 6.0; WOW64", "Windows NT 6.0; Win32", "Windows NT 5.2; WOW64",
				"Windows NT 5.2; Win32", "Windows NT 5.1; WOW64", "Windows NT 5.1; Win32",
		};
		String val_os_version = randomize ? os_version[rdm_opera.nextInt( os_version.length )] : os_version[0];
		HttpRequestHeaderInfo info = new HttpRequestHeaderInfo();
		info.user_agent = "Opera/9.80 (X11; " + val_os_version + "; U; en) Presto/2.9.168 Version/11.50";
		info.accept = "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1";
		info.accept_language = "en-US,en;q=0.9";
		// info.accept_encoding = "gzip, deflate";
		info.accept_encoding = "gzip";
		info.connection = "Keep-Alive";
		return info;
	}
	
	/**
	 * @param randomize
	 *            Whether to randomize user_agent info (not implemented, will always return the same info).
	 * @return Http request header information for Safari browser.
	 */
	private static HttpRequestHeaderInfo getSafari( boolean randomize ) {
		HttpRequestHeaderInfo info = new HttpRequestHeaderInfo();
		info.user_agent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1";
		info.accept = "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
		info.accept_language = "en-US";
		// info.accept_encoding = "gzip, deflate";
		info.accept_encoding = "gzip";
		info.connection = "Keep-Alive";
		return info;
	}
	
}
