package utils;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Collections;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

/**
 * Utilities related to file operations.
 * 
 * @author Jiepu Jiang
 * @version Feb 27, 2013
 */
public class FileUtils {
	
	/**
	 * Create the specified directory.
	 * If the parent directories have not yet been created, it will also iteratively create the parent directories.
	 * 
	 * @param dir
	 */
	public static void mkdir(File dir) {
		if( !dir.getParentFile().exists() ) {
			mkdir( dir.getParentFile() );
		}
		dir.mkdir();
	}
	
	/**
	 * Iteratively delete a file or all files in a directory.
	 * 
	 * @param f Target file or directory.
	 */
	public static void rm(File f) {
		if( f.isFile() ) {
			f.delete();
		}else{
			for( File file:f.listFiles() ) {
				rm(file);
			}
			f.delete();
		}
	}
	
	/**
	 * A comparator that sort files by file name.
	 */
	public static final Comparator<File> sort_by_file_name = new Comparator<File>() {
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
	};
	
	/**
	 * Get an array of files in the directory.
	 * The files will be sorted by file name.
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] ls(File dir) {
		File[] files = dir.listFiles();
		Arrays.sort(files, sort_by_file_name);
		return files;
	}
	
	/**
	 * Sort an array of files by file name.
	 * 
	 * @param files
	 * @return
	 */
	public static File[] sortByName(File[] files) {
		Arrays.sort( files, sort_by_file_name );
		return files;
	}
	
	/**
	 * Sort a list of files by file name.
	 * 
	 * @param files
	 * @return
	 */
	public static List<File> sortByName(List<File> files) {
		Collections.sort( files, sort_by_file_name );
		return files;
	}
	
	/**
	 * An interface for iteratively processing files.
	 * 
	 * @author Jiepu Jiang
	 * @version Feb 27, 2013
	 */
	public interface FileProcessor {
		public abstract void process(File f) throws IOException ;
	}
	
	/**
	 * Process the specified file, or iteratively process each file 
	 * in the specified directory, using the specified processor.
	 * 
	 * @param f
	 * @param proc
	 */
	public static void process( File f, FileProcessor proc ) throws IOException {
		if( f.isDirectory() ) {
			for( File file:f.listFiles() ) {
				process(file, proc);
			}
		} else {
			proc.process(f);
		}
	}
	
	/**
	 * An interface for iteratively processing zip file entries.
	 * 
	 * @author Jiepu Jiang
	 * @version Feb 27, 2013
	 */
	public interface ZipEntryProcessor {
		public abstract void process( ZipEntry entry ) throws IOException;
	}
	
	/**
	 * Iteratively process each entry in the zip file by the specified processor.
	 * 
	 * @param zipf
	 * @param proc
	 * @throws IOException
	 */
	public static void process( ZipFile zipf, ZipEntryProcessor proc ) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipf.entries();
		while( entries.hasMoreElements() ) {
			ZipEntry entry = entries.nextElement();
			proc.process(entry);
		}
	}
	
}
