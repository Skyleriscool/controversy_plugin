package utils.numeric;

/**
 * A variable implementaion of Double.
 * 
 * @author Jiepu Jiang 
 * @version Aug 21, 2012
 */
public class VarDouble extends VarNumber implements Comparable<VarDouble> {
	
	private static final long serialVersionUID = 4385869352363327648L;
	
	private double value;
	
	/**
	 * Create a VarDouble object with default value 0.
	 */
	public VarDouble() {
		this(0);
	}
	
	/**
	 * Create a VarDouble object with the value v.
	 * 
	 * @param v
	 */
	public VarDouble(double v) {
		this.value = v;
	}
	
	public VarDouble clone() {
		return new VarDouble(value);
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof VarDouble){
			VarDouble doubobj = (VarDouble) obj;
			return value==doubobj.value;
		}
		return false;
	}
	
	public void set(int v) {
		set( (double)v );
	}
	
	public void set(long v) {
		set( (double)v );
	}
	
	public void set(float v) {
		set( (double)v );
	}
	
	public void set(double v) {
		value = v;
	}
	
	public int intValue() {
		return (int)value;
	}
	
	public long longValue() {
		return (long)value;
	}
	
	public float floatValue() {
		return (float)value;
	}
	
	public double doubleValue() {
		return value;
	}
	
	public int compareTo(VarDouble anotherVarDouble) {
		return Double.compare(value, anotherVarDouble.value);
	}
	
}
