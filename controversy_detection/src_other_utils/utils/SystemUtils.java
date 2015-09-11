package utils;

import java.io.File;

/**
 * System-related utilities.
 * 
 * @author Jiepu Jiang
 * @version Feb 28, 2013
 */
public class SystemUtils {
	
	public static final long B = 1l;
	public static final long KB = B * 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;
	public static final long TB = GB * 1024;
	
	/**
	 * Return a String reporting statistics on JVM memory usage.
	 * 
	 * @return
	 */
	public static String jvmMem() {
		return jvmMem( Runtime.getRuntime() );
	}
	
	/**
	 * Return a String reporting statistics on JVM memory usage.
	 * 
	 * @param rt
	 * @return
	 */
	public static String jvmMem( Runtime rt ) {
		double free = 1.0 * rt.freeMemory() / MB;
		double current = 1.0 * rt.totalMemory() / MB;
		double used = current - free;
		double max = 1.0 * rt.maxMemory() / MB;
		return StringUtils.format( "Memory usage: %.2f MB / %.2f MB, maximum %.2f MB", used, current, max );
	}
	
	public enum OS {
		Windows, Linux, Unix, Mac
	}
	
	public static OS getOS() {
		if ( isWindows() ) {
			return OS.Windows;
		} else if ( isMac() ) {
			return OS.Mac;
		} else if ( isLinux() ) {
			return OS.Linux;
		} else if ( isUnix() ) {
			return OS.Unix;
		}
		return null;
	}
	
	public static String getUsername() {
		return System.getProperty( "user.name" );
	}
	
	/**
	 * @return Is the jvm running on a windows machine?
	 */
	public static boolean isWindows() {
		String os = System.getProperty( "os.name" ).toLowerCase();
		return os.contains( "win" );
	}
	
	/**
	 * @return Is the jvm running on a mac machine?
	 */
	public static boolean isMac() {
		String os = System.getProperty( "os.name" ).toLowerCase();
		return os.contains( "mac" );
	}
	
	/**
	 * @return Is the jvm running on a linux machine?
	 */
	public static boolean isLinux() {
		String os = System.getProperty( "os.name" ).toLowerCase();
		return os.contains( "linux" );
	}
	
	/**
	 * @return Is the jvm running on a unix machine?
	 */
	public static boolean isUnix() {
		String os = System.getProperty( "os.name" ).toLowerCase();
		return os.contains( "unix" );
	}
	
	/**
	 * Automatically transform system independent path to the correct system path. It is assumed that "~/" indicates the
	 * home directory, which will be automatically translated according to the current system directory.
	 * 
	 * @param path
	 * @return
	 */
	public static String getPath( String path ) {
		return path.replaceAll( "\\\\", "/" ).replace( "~/", getPathHome() );
	}
	
	/**
	 * Automatically detect the OS and return the home directory.
	 * 
	 * @return
	 */
	public static String getPathHome() {
		String username = SystemUtils.getUsername();
		switch ( SystemUtils.getOS() ) {
			case Linux: {
				if ( new File( "/home/" + username + "/" ).exists() ) {
					return "/home/" + username + "/";
				}
			}
			case Unix: {
				if ( new File( "/home/" + username + "/" ).exists() ) {
					return "/home/" + username + "/";
				}
			}
			case Mac: {
				if ( new File( "/Users/" + username + "/" ).exists() ) {
					return "/Users/" + username + "/";
				}
			}
			case Windows: {
				if ( new File( "C:/Users/" + username + "/" ).exists() ) {
					return "C:/Users/" + username + "/";
				}
			}
			default:
				return null;
		}
	}
	
	/**
	 * Automatically transform system independent path to the correct system path. It is assumed that "~/" indicates the
	 * home directory, which will be automatically translated according to the current system directory.
	 * 
	 * @param path
	 * @return
	 */
	public static File getFile( String path ) {
		return new File( getPath( path ) );
	}
	
	/**
	 * Automatically detect the OS and return the home directory.
	 * 
	 * @return
	 */
	public static File getFileHome() {
		String pathHome = getPathHome();
		if ( pathHome == null ) {
			return null;
		}
		return new File( pathHome );
	}
	
}
