package utils.crawl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import utils.StringUtils;

/**
 * CrawlerUtils provides convenient single-thread method of accessing page contents by sending http requests. It designed as a light-weighted and single-thread
 * crawler api, which is suitable for continuously crawling one site, or can be used when target websites disabled crawler APIs with certain http headers such
 * as heritrix.
 * 
 * @author Jiepu Jiang
 * @version Aug 20, 2012
 * @see utils.crawl.HttpRequestHeaderInfo
 */
public class CrawlerUtils {
	
	public static void main( String[] args ) {
		try {
			
			String url = "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=01002&MR=1";
			
			CrawlerUtils.getContentString( url, "UTF-8" );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get content of the resource as bytes using specified http header.
	 * 
	 * @param header
	 *            Http request header info
	 * @param url
	 *            Url of the resource
	 * @return Content bytes of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static byte[] getContentBytes( HttpRequestHeaderInfo header, String url ) throws IOException, URISyntaxException {
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = header.create( new URI( url ) );
		HttpResponse response = client.execute( get );
		PageCompression compression = detectCompressionMethod( response );
		
		byte[] data = null;
		
		HttpEntity en = response.getEntity();
		InputStream input = null;
		InputStream input_page = en.getContent();
		if ( compression == PageCompression.GZIP ) {
			input = new GZIPInputStream( input_page );
		} else {
			input = input_page;
		}
		data = IOUtils.toByteArray( input );
		input.close();
		
		return data;
		
	}
	
	/**
	 * Get content of the resource as bytes using ramdom client header or default one.
	 * 
	 * @param random_header
	 *            If true, it will connect using random client's http header; if false, the default one will be used
	 * @param url
	 *            Url of the resource
	 * @return Content bytes of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static byte[] getContentBytes( boolean random_header, String url ) throws IOException, URISyntaxException {
		return getContentBytes( HttpRequestHeaderInfo.get( random_header ), url );
	}
	
	/**
	 * Get content of the resource as bytes using the default client's http request header.
	 * 
	 * @param url
	 *            Url of the resource
	 * @return Content bytes of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static byte[] getContentBytes( String url ) throws IOException, URISyntaxException {
		return getContentBytes( false, url );
	}
	
	/**
	 * Get content of the resource as bytes (will automatically retry if the connection is failed).
	 * 
	 * @param header
	 *            Http request header info
	 * @param url
	 *            Url of the resource
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content bytes of the resource
	 * @throws InterruptedException
	 */
	public static byte[] getContentBytesAutoRetry( HttpRequestHeaderInfo header, String url, int maxRetry, int meanRetryTimeInterval ) throws InterruptedException {
		
		Random random = new Random();
		byte[] content = null;
		int num_retrial = -1;
		
		while ( content == null ) {
			try {
				num_retrial++;
				content = getContentBytes( header, url );
			} catch ( Exception e ) {
				System.err.println( " >> Cannot get content from " + url + ", will automatically retry ... " );
				double sleep_time = 1.0 * meanRetryTimeInterval / 2 + random.nextDouble() * meanRetryTimeInterval; // the expected interval is
																													// meanRetryTimeInterval
				Thread.sleep( (int) sleep_time );
			}
			if ( maxRetry >= 0 && num_retrial >= maxRetry ) { // if maxRetry < 0, will always retry until it has been accessed.
				break;
			}
		}
		
		return content;
		
	}
	
	/**
	 * Get content of the resource as bytes (will automatically retry if the connection is failed).
	 * 
	 * @param random_header
	 *            Whether to use random client's http request header info
	 * @param url
	 *            Url of the resource
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content bytes of the resource
	 * @throws InterruptedException
	 */
	public static byte[] getContentBytesAutoRetry( boolean random_header, String url, int maxRetry, int retryInterval ) throws InterruptedException {
		return getContentBytesAutoRetry( HttpRequestHeaderInfo.get( random_header ), url, maxRetry, retryInterval );
	}
	
	/**
	 * Get content of the resource as bytes (will automatically retry if the connection is failed).
	 * 
	 * @param url
	 *            Url of the resource
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content bytes of the resource
	 * @throws InterruptedException
	 */
	public static byte[] getContentBytesAutoRetry( String url, int maxRetry, int retryInterval ) throws InterruptedException {
		return getContentBytesAutoRetry( false, url, maxRetry, retryInterval );
	}
	
	/**
	 * Get content of the resource as String.
	 * 
	 * @param header
	 *            Http request header info
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset encoding to be used for the content
	 * @return Content String of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getContentString( HttpRequestHeaderInfo header, String url, String charset ) throws URISyntaxException, IOException {
		return new String( getContentBytes( header, url ), charset );
	}
	
	/**
	 * Get content of the resource as String.
	 * 
	 * @param random_header
	 *            Whether to use random client's http request header info
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset encoding to be used for the content
	 * @return Content String of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getContentString( boolean random_header, String url, String charset ) throws URISyntaxException, IOException {
		return getContentString( HttpRequestHeaderInfo.get( random_header ), url, charset );
	}
	
	/**
	 * Get content of the resource as String.
	 * 
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset encoding to be used for the content
	 * @return Content String of the resource
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getContentString( String url, String charset ) throws URISyntaxException, IOException {
		return getContentString( false, url, charset );
	}
	
	/**
	 * Get content of the resource as String (will automatically retry if the connection is failed).
	 * 
	 * @param header
	 *            Http request header info
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset for the content
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content String of the resource
	 * @throws InterruptedException
	 */
	public static String getContentStringAutoRetry( HttpRequestHeaderInfo header, String url, String charset, int maxRetry, int meanRetryTimeInterval ) throws URISyntaxException, IOException, InterruptedException {
		byte[] bytes = getContentBytesAutoRetry( header, url, maxRetry, meanRetryTimeInterval );
		if ( bytes != null ) {
			return new String( bytes, charset );
		}
		return null;
	}
	
	/**
	 * Get content of the resource as String (will automatically retry if the connection is failed).
	 * 
	 * @param random_header
	 *            Whether to use random client's http request header info
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset for the content
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content String of the resource
	 * @throws InterruptedException
	 */
	public static String getContentStringAutoRetry( boolean random_header, String url, String charset, int maxRetry, int retryInterval ) throws URISyntaxException, IOException, InterruptedException {
		return getContentStringAutoRetry( HttpRequestHeaderInfo.get( random_header ), url, charset, maxRetry, retryInterval );
	}
	
	/**
	 * Get content of the resource as String (will automatically retry if the connection is failed).
	 * 
	 * @param url
	 *            Url of the resource
	 * @param charset
	 *            The charset for the content
	 * @param maxRetry
	 *            Maximum retrial (if maxRetry<0, it will keep retrying until the content has been read)
	 * @param meanRetryTimeInterval
	 *            Mean time interval between each retrial
	 * @return Content String of the resource
	 * @throws InterruptedException
	 */
	public static String getContentStringAutoRetry( String url, String charset, int maxRetry, int retryInterval ) throws URISyntaxException, IOException, InterruptedException {
		return getContentStringAutoRetry( false, url, charset, maxRetry, retryInterval );
	}
	
	/**
	 * Enum for web server responses' compression methods (not compressed or gzip compressed). Many servers do require the client to support gzip information.
	 */
	public enum PageCompression {
		NOT_COMPRESSED,
		GZIP,
		DEFLATE,
	}
	
	/**
	 * Detect page compression from http response.
	 * 
	 * @param response
	 *            An http response
	 * @return Compression method specified in http response
	 */
	public static PageCompression detectCompressionMethod( HttpResponse response ) {
		if ( response != null && response.containsHeader( "Content-Encoding" ) ) {
			Header[] headers = response.getHeaders( "Content-Encoding" );
			for ( int i = 0 ; i < headers.length ; i++ ) {
				String val = headers[i].getValue().trim();
				System.out.println( val );
				if ( val.equalsIgnoreCase( "gzip" ) ) {
					return PageCompression.GZIP;
				} else if ( val.equalsIgnoreCase( "deflate" ) ) {
					return PageCompression.DEFLATE;
				}
			}
		}
		return PageCompression.NOT_COMPRESSED;
	}
	
	/**
	 * Detect charset information from http response. Note that pratically I find many of the charset encoding notified by the server is not the actual charset
	 * encoding being used for the webpage. Therefore, I would not suggest using this method to determine the charset encoding.
	 * 
	 * @param response
	 *            An http response
	 * @return Page charset specified in http response; or null if not detected
	 */
	public static String detectCharset( HttpResponse response ) {
		if ( response != null && response.containsHeader( "Content-Type" ) ) {
			Header[] headers = response.getHeaders( "Content-Type" );
			for ( int i = 0 ; i < headers.length ; i++ ) {
				String headerinfo = headers[i].getValue();
				String charset = StringUtils.extractFirst( headerinfo, "charset=([^;]+)", 1 );
				if ( charset != null && charset.length() > 0 ) {
					return charset;
				}
			}
		}
		return null;
	}
	
}
