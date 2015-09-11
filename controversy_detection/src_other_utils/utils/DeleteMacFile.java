package utils;

import java.io.File;

public class DeleteMacFile {
	
	public static void main( String[] args ) {
		try {
			
			args = new String[] { "C:/" };
			process( new File( args[0] ) );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	static void process( File f ) {
		// System.out.println( f.getAbsolutePath() );
		if ( f.isDirectory() && f.listFiles() != null ) {
			for ( File file : f.listFiles() ) {
				process( file );
			}
		} else {
			if ( f.getName().contains( "DS_Store" ) ) {
				try {
					f.delete();
					System.out.println( "Delete " + f.getAbsolutePath() );
				} catch ( Exception e ) {
					System.err.println( "Cannot delete " + f.getAbsolutePath() );
					e.printStackTrace();
				}
			}
		}
	}
	
}
