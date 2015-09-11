package utils.numeric;

/**
 * A variable implementation of Integer.
 * 
 * @author Jiepu Jiang 
 * @version Aug 20, 2012
 */
public class VarInteger extends VarNumber implements Comparable<VarInteger> {
	
	private static final long serialVersionUID = 1415397551193727076L;
	
	private int value;
	
	/**
	 * Create a VarInteger object with initial value 0.
	 */
	public VarInteger() {
		this(0);
	}
	
	/**
	 * Create a VarInteger object with the specified value v.
	 * 
	 * @param v
	 */
	public VarInteger(int v) {
		this.value = v;
	}
	
	public VarInteger clone() {
		return new VarInteger(value);
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof VarInteger){
			VarInteger intobj = (VarInteger) obj;
			return value==intobj.value;
		}
		return false;
	}
	
	public void set(int v) {
		value = v;
	}
	
	public void set(long v) {
		set( (int)v );
	}
	
	public void set(float v) {
		set( (int)v );
	}
	
	public void set(double v) {
		set( (int)v );
	}
	
	public int intValue() {
		return value;
	}
	
	public long longValue() {
		return (long)value;
	}
	
	public float floatValue() {
		return (float)value;
	}
	
	public double doubleValue() {
		return (double)value;
	}
	
	public int compareTo(VarInteger anotherVarInteger) {
		int thisVal = this.value;
		int anotherVal = anotherVarInteger.value;
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}
	
}
