package utils.crawl;

import java.io.*;
import java.net.*;

/**
 * <p>
 * This is a simple program that monitors localhost at port 80 and outputs all request information. 
 * When running, it will literally print out all the http-request message to command line. It is used 
 * here to record the default requesting headers for different browsers, so that we can use the recorded message to simulate browser behavior.
 * </p>
 * <p>
 * How to use:
 * </p>
 * <ol>
 * 	<li>Set up a listening port and start the program</li>
 * 	<li>If you want to know the header information of a browser, use the browser to visit http://localhost:port and the http request header will be printed to command line</li>
 * </ol>
 * 
 * @author Jiepu Jiang
 * @version Aug 20, 2012
 */
public class BrowserHeaderDetector {
	
	public static void main(String[] args) {
		try{
			
			// set up the listening port here
			int port_listen = 8080;
			
			ServerSocket server = new ServerSocket(port_listen);
			Socket socket = server.accept();
			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while(line!=null){
				System.out.println(line);
				line = reader.readLine();
			}
			reader.close();
			socket.close();
			server.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
