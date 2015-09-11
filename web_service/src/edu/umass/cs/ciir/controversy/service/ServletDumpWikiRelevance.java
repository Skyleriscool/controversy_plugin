package edu.umass.cs.ciir.controversy.service;

import java.io.IOException;
import java.io.BufferedWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.IOUtils;
import edu.umass.cs.ciir.controversy.data.DumpWebpageRelevance;

public class ServletDumpWikiRelevance extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		
		boolean useShiri = false;
		boolean useMyungha = false;
		boolean useDatabase = false;
		
		if ( request.getParameter( "useShiri" ) != null && request.getParameter( "useShiri" ).equalsIgnoreCase( "true" ) ) {
			useShiri = true;
		}
		if ( request.getParameter( "useMyungha" ) != null && request.getParameter( "useMyungha" ).equalsIgnoreCase( "true" ) ) {
			useMyungha = true;
		}
		if ( request.getParameter( "useDatabase" ) != null && request.getParameter( "useDatabase" ).equalsIgnoreCase( "true" ) ) {
			useDatabase = true;
		}
		
		String data = "";
		try {
			data = DumpWebpageRelevance.combineData( useShiri, useMyungha, useDatabase );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		response.setHeader( "Content-Disposition", "attachment; filename=\"wiki_entry_relevance.txt\"" );
		BufferedWriter writer = IOUtils.getBufferedWriter( response.getOutputStream() );
		writer.write( data );
		writer.close();
		
	}
	
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		doGet( request, response );
	}
	
}
