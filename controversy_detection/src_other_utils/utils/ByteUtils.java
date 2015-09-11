package utils;

/**
 * Operations related to byte. The implementations are from DataOutputStream.
 * Currently it contains only transformation between bytes and long integers.
 * 
 * @author Jiepu Jiang
 * @version Feb 27, 2013
 */
public class ByteUtils {
	
	/**
	 * Transform a long integer value to a bytes array.
	 * 
	 * @param v 		A long integer
	 * @return 			A bytes array of the long integer
	 */
	public static byte[] toBytes( long v ) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte)(v >>> 56);
		bytes[1] = (byte)(v >>> 48);
		bytes[2] = (byte)(v >>> 40);
		bytes[3] = (byte)(v >>> 32);
		bytes[4] = (byte)(v >>> 24);
		bytes[5] = (byte)(v >>> 16);
		bytes[6] = (byte)(v >>>  8);
		bytes[7] = (byte)(v >>>  0);
		return bytes;
	}
	
	/**
	 * Transform a long integer value to a bytes array.
	 * 
	 * @param v 		A long integer
	 * @param bytes 	A bytes array to store the long integer
	 * @param pos 		The start position of the array to store the long integer
	 */
	public static void toBytes( long v, byte[] bytes, int pos ) {
		bytes[pos] = (byte)(v >>> 56);
		bytes[pos+1] = (byte)(v >>> 48);
		bytes[pos+2] = (byte)(v >>> 40);
		bytes[pos+3] = (byte)(v >>> 32);
		bytes[pos+4] = (byte)(v >>> 24);
		bytes[pos+5] = (byte)(v >>> 16);
		bytes[pos+6] = (byte)(v >>>  8);
		bytes[pos+7] = (byte)(v >>>  0);
	}
	
	/**
	 * Transform a bytes array to a long integer value.
	 * 
	 * @param bytes 	An bytes array that includes the long integer
	 * @return 			The long integer value
	 */
	public static long toLong( byte[] bytes ) {
		return (((long)bytes[0] << 56) + 
			   ((long)(bytes[1] & 255) << 48) + 
			   ((long)(bytes[2] & 255) << 40) + 
			   ((long)(bytes[3] & 255) << 32) + 
			   ((long)(bytes[4] & 255) << 24) + 
			   ((bytes[5] & 255) << 16) + 
			   ((bytes[6] & 255) <<  8) + 
			   ((bytes[7] & 255) <<  0));
	}
	
	/**
	 * Transform a bytes array to a long integer value.
	 * 
	 * @param bytes 	An bytes array that includes the long integer
	 * @param pos 		The start position of the long integer stored in the bytes array
	 * @return 			The long integer value
	 */
	public static long toLong( byte[] bytes, int pos ) {
		return (((long)bytes[pos] << 56) + 
               ((long)(bytes[pos+1] & 255) << 48) + 
               ((long)(bytes[pos+2] & 255) << 40) + 
               ((long)(bytes[pos+3] & 255) << 32) + 
               ((long)(bytes[pos+4] & 255) << 24) + 
               ((bytes[pos+5] & 255) << 16) + 
               ((bytes[pos+6] & 255) <<  8) + 
               ((bytes[pos+7] & 255) <<  0));
	}
	
}
