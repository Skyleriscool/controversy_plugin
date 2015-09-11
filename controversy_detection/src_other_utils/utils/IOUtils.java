package utils;

import java.io.*;
import java.util.zip.*;

/**
 * Utilities related to IO operations.
 * 
 * @author Jiepu Jiang
 * @version Feb 27, 2013
 */
public class IOUtils {
	
	/** Default charset encoding. */
	public static String DEFAULT_CHARSET = "UTF-8";
	
	/** Read buffer: 1MB. */
	private static final int read_buf_len = 1024 * 1024;
	
	/**
	 * Read all the content from the inputstream.
	 * 
	 * @param path
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readContent( String path, String charset ) throws IOException {
		return readContent( new File( path ), charset );
	}
	
	/**
	 * Read all the content from the inputstream.
	 * 
	 * @param file
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readContent( File file, String charset ) throws IOException {
		InputStream inputstream = new FileInputStream( file );
		String content = readContent( inputstream, charset );
		inputstream.close();
		return content;
	}
	
	/**
	 * Read all the content from the inputstream.
	 * 
	 * @param input
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readContent( InputStream input, String charset ) throws IOException {
		byte[] bytes = readBytes( input );
		String content = new String( bytes, charset );
		return content;
	}
	
	/**
	 * Read all available bytes from the inputstream.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes( String path ) throws IOException {
		return readBytes( new File( path ) );
	}
	
	/**
	 * Read all available bytes from the inputstream.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes( File file ) throws IOException {
		InputStream inputstream = new FileInputStream( file );
		byte[] content = readBytes( inputstream );
		inputstream.close();
		return content;
	}
	
	/**
	 * Read all available bytes from the inputstream.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes( InputStream input ) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] read_buffer = new byte[read_buf_len];
		int read_len = -1;
		while ( ( read_len = input.read( read_buffer ) ) != -1 ) {
			if ( read_len > 0 ) { //
				bos.write( read_buffer, 0, read_len );
			}
		}
		byte[] bytes = bos.toByteArray();
		bos.close();
		return bytes;
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param path
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( String path, String charset ) throws IOException {
		return new BufferedReader( new InputStreamReader( new FileInputStream( path ), charset ) );
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( String path ) throws IOException {
		return getBufferedReader( path, DEFAULT_CHARSET );
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param f
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( File f, String charset ) throws IOException {
		return new BufferedReader( new InputStreamReader( new FileInputStream( f ), charset ) );
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( File f ) throws IOException {
		return getBufferedReader( f, DEFAULT_CHARSET );
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param instream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( InputStream instream, String charset ) throws IOException {
		return new BufferedReader( new InputStreamReader( instream, charset ) );
	}
	
	/**
	 * Get a buffered reader for the specified file.
	 * 
	 * @param instream
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader( InputStream instream ) throws IOException {
		return getBufferedReader( instream, DEFAULT_CHARSET );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param path
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( String path, String charset ) throws IOException {
		return new BufferedWriter( new OutputStreamWriter( new FileOutputStream( path ), charset ) );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( String path ) throws IOException {
		return getBufferedWriter( path, DEFAULT_CHARSET );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param f
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( File f, String charset ) throws IOException {
		return new BufferedWriter( new OutputStreamWriter( new FileOutputStream( f ), charset ) );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( File f ) throws IOException {
		return getBufferedWriter( f, DEFAULT_CHARSET );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param outstream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( OutputStream outstream, String charset ) throws IOException {
		return new BufferedWriter( new OutputStreamWriter( outstream, charset ) );
	}
	
	/**
	 * Get a buffered writer for the specified file.
	 * 
	 * @param outstream
	 * @return
	 * @throws IOException
	 */
	public static BufferedWriter getBufferedWriter( OutputStream outstream ) throws IOException {
		return getBufferedWriter( outstream, DEFAULT_CHARSET );
	}
	
	/**
	 * Unzip the provided inputstream.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static InputStream ungzip( InputStream in ) throws IOException {
		return new GZIPInputStream( in );
	}
	
	/**
	 * Gzip the provided outputstream.
	 * 
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static OutputStream gzip( OutputStream output ) throws IOException {
		return new GZIPOutputStream( output );
	}
	
}
