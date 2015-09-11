package utils.numeric;

/**
 * A variable implementation of Long.
 * 
 * @author Jiepu Jiang 
 * @version Aug 20, 2012
 */
public class VarLong extends VarNumber implements Comparable<VarLong> {
	
	private static final long serialVersionUID = -3115702192723210496L;
	
	private long value;
	
	/**
	 * Create a VarLong object with initial value 0.
	 */
	public VarLong() {
		this(0l);
	}
	
	/**
	 * Create a VarLong object with the specified value v.
	 * 
	 * @param v
	 */
	public VarLong(long v) {
		this.value = v;
	}
	
	public VarLong clone() {
		return new VarLong(value);
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof VarLong){
			VarLong longobj = (VarLong) obj;
			return value==longobj.value;
		}
		return false;
	}
	
	public void set(int v) {
		set( (long)v );
	}
	
	public void set(long v) {
		value = v;
	}
	
	public void set(float v) {
		set( (long)v );
	}
	
	public void set(double v) {
		set( (long)v );
	}
	
	public int intValue() {
		return (int)value;
	}
	
	public long longValue() {
		return value;
	}
	
	public float floatValue() {
		return (float)value;
	}
	
	public double doubleValue() {
		return (double)value;
	}
	
	public int compareTo(VarLong anotherVarInteger) {
		long thisVal = this.value;
		long anotherVal = anotherVarInteger.value;
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}
	
}
