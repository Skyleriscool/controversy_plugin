package utils.numeric;

/**
 * VarNumber implements a variable number object (the stored value can be updated).
 * 
 * @author Jiepu Jiang
 * @version Aug 20, 2012
 * 
 * @see edu.pitt.sis.iris.util.VarInteger
 * @see edu.pitt.sis.iris.util.VarLong
 * @see edu.pitt.sis.iris.util.VarDouble
 * @see edu.pitt.sis.iris.util.VarFloat
 */
public abstract class VarNumber extends Number {
	
	private static final long serialVersionUID = -6116273825659425069L;
	
	/**
	 * Set a new value to the number.
	 * 
	 * @param v
	 */
	public abstract void set(int v);
	
	/**
	 * Set a new value to the number.
	 * 
	 * @param v
	 */
	public abstract void set(long v);
	
	/**
	 * Set a new value to the number.
	 * 
	 * @param v
	 */
	public abstract void set(float v);
	
	/**
	 * Set a new value to the number.
	 * 
	 * @param v
	 */
	public abstract void set(double v);
	
	/**
	 * Add the current value by v.
	 * 
	 * @param v
	 */
	public void add(int v) {
		set( v + longValue() );
	}
	
	/**
	 * Add the current value by v.
	 * 
	 * @param v
	 */
	public void add(long v) {
		set( v + longValue() );
	}
	
	/**
	 * Add the current value by v.
	 * 
	 * @param v
	 */
	public void add(float v) {
		set( v + doubleValue() );
	}
	
	/**
	 * Add the current value by v.
	 * 
	 * @param v
	 */
	public void add(double v) {
		set( v + doubleValue() );
	}
	
	/**
	 * Substract v from the current value.
	 * 
	 * @param v
	 */
	public void minus(int v) { add(-v); }
	
	/**
	 * Substract v from the current value.
	 * 
	 * @param v
	 */
	public void minus(long v) { add(-v); }
	
	/**
	 * Substract v from the current value.
	 * 
	 * @param v
	 */
	public void minus(float v) { add(-v); }
	
	/**
	 * Substract v from the current value.
	 * 
	 * @param v
	 */
	public void minus(double v) { add(-v); }
	
	/**
	 * Multiply the current value by v.
	 * 
	 * @param v
	 */
	public void multiply(int v) {
		set( v * intValue() );
	}
	
	/**
	 * Multiply the current value by v.
	 * 
	 * @param v
	 */
	public void multiply(long v) {
		set( v * longValue() );
	}
	
	/**
	 * Multiply the current value by v.
	 * 
	 * @param v
	 */
	public void multiply(float v) {
		set( v * doubleValue() );
	}
	
	/**
	 * Multiply the current value by v.
	 * 
	 * @param v
	 */
	public void multiply(double v) {
		set( v * doubleValue() );
	}
	
	/**
	 * Increase the stored integer value by 1.
	 */
	public void increase() {
		add(1);
	}
	
	/**
	 * Decrease the stored integer value by 1.
	 */
	public void decrease() {
		minus(1);
	}
	
}
