package utils.numeric;

/**
 * A variable implementaion of Float.
 * 
 * @author Jiepu Jiang 
 * @version Aug 21, 2012
 */
public class VarFloat extends VarNumber implements Comparable<VarFloat> {
	
	private static final long serialVersionUID = -3573531731923150833L;
	
	private float value;
	
	/**
	 * Create a VarDouble object with default value 0.
	 */
	public VarFloat() {
		this(0);
	}
	
	/**
	 * Create a VarDouble object with the value v.
	 * 
	 * @param v
	 */
	public VarFloat(float v) {
		this.value = v;
	}
	
	public VarFloat clone() {
		return new VarFloat(value);
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof VarFloat){
			VarFloat doubobj = (VarFloat) obj;
			return value==doubobj.value;
		}
		return false;
	}
	
	public void set(int v) {
		set( (float)v );
	}
	
	public void set(long v) {
		set( (float)v );
	}
	
	public void set(float v) {
		value = v;
	}
	
	public void set(double v) {
		set( (float)v );
	}
	
	public int intValue() {
		return (int)value;
	}
	
	public long longValue() {
		return (long)value;
	}
	
	public float floatValue() {
		return value;
	}
	
	public double doubleValue() {
		return (double)value;
	}
	
	public int compareTo(VarFloat anotherVarDouble) {
		return Float.compare(value, anotherVarDouble.value);
	}
	
}
