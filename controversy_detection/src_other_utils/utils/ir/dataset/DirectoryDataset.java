package utils.ir.dataset;

import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DirectoryDataset implements Dataset {
	
	protected List<File> files;
	protected Class<? extends Dataset> format;
	
	protected int nextix;
	protected Dataset dataset;
	
	public DirectoryDataset( File dir, Class<? extends Dataset> format ) throws IOException {
		this.format = format;
		this.files = new ArrayList<File>();
		scanAllFiles( dir );
		if ( nextix < files.size() ) {
			dataset = getDataset( files.get( nextix ) );
			nextix++;
		}
	}
	
	private Dataset getDataset( File file ) throws IOException {
		if ( format == TrecTextDataset.class ) {
			return file.getName().endsWith( ".gz" ) ? new TrecTextDataset( file, true ) : new TrecTextDataset( file, false );
		} else if ( format == TrecWarcDataset.class ) {
			return file.getName().endsWith( ".gz" ) ? new TrecWarcDataset( file, true ) : new TrecWarcDataset( file, false );
		} else if ( format == TrecWebDataset.class ) {
			return file.getName().endsWith( ".gz" ) ? new TrecWebDataset( file, true ) : new TrecWebDataset( file, false );
		}
		return null;
	}
	
	private void scanAllFiles( File f ) {
		if ( f.isDirectory() ) {
			for ( File file : f.listFiles() ) {
				scanAllFiles( file );
			}
		} else {
			files.add( f );
		}
	}
	
	public Map<String, String> next() throws IOException {
		if ( dataset == null ) {
			return null;
		}
		Map<String, String> doc = null;
		while ( true ) {
			doc = dataset.next();
			if ( doc != null ) {
				break;
			} else {
				dataset.close();
				if ( nextix < files.size() ) {
					dataset = getDataset( files.get( nextix ) );
					nextix++;
				} else {
					break;
				}
			}
		}
		return doc;
	}
	
	public void close() throws IOException {
		// do nothing
	}
	
}
