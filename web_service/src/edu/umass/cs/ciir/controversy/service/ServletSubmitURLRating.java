package edu.umass.cs.ciir.controversy.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.umass.cs.ciir.controversy.database.dao.URLRatingDAO;
import edu.umass.cs.ciir.controversy.database.entity.URLRating;

/**
 * Servlet that handles users' ratings on URL's controversy.
 * 
 * @author Jiepu Jiang
 * @version May 30, 2015
 */
public class ServletSubmitURLRating extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public ServletSubmitURLRating() {
		super();
	}
	
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		
		long timestamp = System.currentTimeMillis();
		String requestid = request.getParameter( "requestid" );
		String rating = request.getParameter( "rating" );
		
		boolean success = false;
		StringBuilder errmsg = new StringBuilder();
		
		if ( requestid != null && rating != null && requestid.matches( "\\d+" ) && rating.matches( "\\d+" ) && Integer.parseInt( rating ) > 0 ) {
			try {
				URLRatingDAO.add( new URLRating( Long.parseLong( requestid ), timestamp, Integer.parseInt( rating ) ) );
				success = true;
			} catch ( SQLException e ) {
				e.printStackTrace();
				errmsg.append( "Server error: cannot store ratings into database." );
			}
		} else {
			errmsg.append( "Invalid request parameters: requestid = " + requestid + ", rating = " + rating + "." );
		}
		
		String err = errmsg.toString();
		response.getWriter().write( "{\"success\":" + success + "" + ( err.length() > 0 ? ", \"errmsg\":\"" + err + "\"" : "" ) + "}" );
		
	}
	
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		doGet( request, response );
	}
	
}
