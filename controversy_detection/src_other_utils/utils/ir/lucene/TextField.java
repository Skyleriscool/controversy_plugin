package utils.ir.lucene;

import java.io.Reader;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.analysis.TokenStream;

/**
 * Extension of standard Lucene TextField. Add options for storing document vector.
 * 
 * @author Jiepu Jiang
 * @version Feb 16, 2015
 */
public class TextField extends Field {
	
	/** Indexed, tokenized, not stored. */
	public static final FieldType TYPE_NOT_STORED = new FieldType();
	
	/** Indexed, tokenized, stored. */
	public static final FieldType TYPE_STORED = new FieldType();
	
	/** Indexed, tokenized, not stored, store document vector. */
	public static final FieldType TYPE_NOT_STORED_VECT = new FieldType();
	
	/** Indexed, tokenized, stored, store document vector. */
	public static final FieldType TYPE_STORED_VECT = new FieldType();
	
	/** Indexed, tokenized, not stored, store document vector and position. */
	public static final FieldType TYPE_NOT_STORED_VECT_POS = new FieldType();
	
	/** Indexed, tokenized, stored, store document vector and position. */
	public static final FieldType TYPE_STORED_VECT_POS = new FieldType();
	
	static {
		TYPE_NOT_STORED.setIndexed( true );
		TYPE_NOT_STORED.setTokenized( true );
		TYPE_NOT_STORED.freeze();
		
		TYPE_STORED.setIndexed( true );
		TYPE_STORED.setTokenized( true );
		TYPE_STORED.setStored( true );
		TYPE_STORED.freeze();
		
		TYPE_NOT_STORED_VECT.setIndexed( true );
		TYPE_NOT_STORED_VECT.setTokenized( true );
		TYPE_NOT_STORED_VECT.setStoreTermVectors( true );
		TYPE_NOT_STORED_VECT.freeze();
		
		TYPE_STORED_VECT.setIndexed( true );
		TYPE_STORED_VECT.setTokenized( true );
		TYPE_STORED_VECT.setStored( true );
		TYPE_STORED_VECT.setStoreTermVectors( true );
		TYPE_STORED_VECT.freeze();
		
		TYPE_NOT_STORED_VECT_POS.setIndexed( true );
		TYPE_NOT_STORED_VECT_POS.setTokenized( true );
		TYPE_NOT_STORED_VECT_POS.setStoreTermVectors( true );
		TYPE_NOT_STORED_VECT_POS.setStoreTermVectorPositions( true );
		TYPE_NOT_STORED_VECT_POS.freeze();
		
		TYPE_STORED_VECT_POS.setIndexed( true );
		TYPE_STORED_VECT_POS.setTokenized( true );
		TYPE_STORED_VECT_POS.setStored( true );
		TYPE_STORED_VECT_POS.setStoreTermVectors( true );
		TYPE_STORED_VECT_POS.setStoreTermVectorPositions( true );
		TYPE_STORED_VECT_POS.freeze();
	}
	
	/**
	 * Creates a new un-stored TextField with Reader value.
	 * 
	 * @param name
	 * @param reader
	 */
	public TextField( String name, Reader reader ) {
		super( name, reader, TYPE_NOT_STORED );
	}
	
	/**
	 * Creates a new un-stored TextField with TokenStream value.
	 * 
	 * @param name
	 * @param stream
	 */
	public TextField( String name, TokenStream stream ) {
		super( name, stream, TYPE_NOT_STORED );
	}
	
	/**
	 * Creates a new TextField with String value.
	 * 
	 * @param name
	 * @param value
	 * @param store
	 * @param storeTermVector
	 * @param storeTermPosition
	 */
	public TextField( String name, String value, boolean store, boolean storeTermVector, boolean storeTermPosition ) {
		super( name, value, getFieldType( store, storeTermVector, storeTermPosition ) );
	}
	
	public static FieldType getFieldType( boolean store, boolean storeTermVector, boolean storeTermPosition ) {
		if ( !storeTermVector && !storeTermPosition ) {
			return store ? TYPE_STORED : TYPE_NOT_STORED;
		}
		if ( storeTermVector && !storeTermPosition ) {
			return store ? TYPE_STORED_VECT : TYPE_NOT_STORED_VECT;
		}
		if ( storeTermVector && storeTermPosition ) {
			return store ? TYPE_STORED_VECT_POS : TYPE_NOT_STORED_VECT_POS;
		}
		return null;
	}
	
}
