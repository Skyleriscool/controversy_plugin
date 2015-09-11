package edu.umass.cs.ciir.controversy.knn;

import java.util.Map;
import java.util.TreeMap;

/**
 * This is a class storing an object and its related information (stored as a map).
 * 
 * @author Jiepu Jiang
 * @version May 25, 2015
 * @param <T>
 *            Type of the object
 */
public class ObjectInfo<T> {
	
	protected T object;
	protected Map<String, Object> info;
	
	public ObjectInfo() {
		this.info = new TreeMap<>();
	}
	
	public ObjectInfo<T> setObject( T object ) {
		this.object = object;
		return this;
	}
	
	public T getObject() {
		return this.object;
	}
	
	public ObjectInfo<T> setInfo( String key, Object value ) {
		this.info.put( key, value );
		return this;
	}
	
	public Object getInfo( String key ) {
		return this.info.get( key );
	}
	
	public Map<String, Object> getInfo() {
		return this.info;
	}
	
}
