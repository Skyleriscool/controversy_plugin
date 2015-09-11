package utils.ir.dataset.topic.session;

/**
 * TREC session track task types.
 * 
 * @author Jiepu Jiang
 * @version Sep 2, 2014
 */
public enum TRECSessionTaskType {
	
	KnownItem, // factual + specific
	KnownSubject, // factual + amorphous
	Interpretive, // intellectual + specific
	Exploratory; // intellectual + amorphous
	
	/**
	 * Parse text into task type object.
	 * 
	 * @param text
	 * @return
	 */
	public static TRECSessionTaskType parse( String text ) {
		if ( text.equalsIgnoreCase( "known-item" ) || text.equalsIgnoreCase( "KnownItem" ) ) {
			return KnownItem;
		} else if ( text.equalsIgnoreCase( "known-subject" ) || text.equalsIgnoreCase( "KnownSubject" ) ) {
			return KnownSubject;
		} else if ( text.equalsIgnoreCase( "interpretive" ) ) {
			return Interpretive;
		} else if ( text.equalsIgnoreCase( "exploratory" ) ) {
			return Exploratory;
		}
		return null;
	}
	
	/**
	 * Task product type.
	 */
	public static enum Product {
		Factual, Intellectual,
	}
	
	/**
	 * Task goal type.
	 */
	public static enum Goal {
		Amorphous, Specific,
	}
	
	/**
	 * Get the product type of the specified task type.
	 * 
	 * @param type
	 * @return
	 */
	public static Product getProduct( TRECSessionTaskType type ) {
		if ( type.equals( KnownItem ) || type.equals( KnownSubject ) ) {
			return Product.Factual;
		} else if ( type.equals( Interpretive ) || type.equals( Exploratory ) ) {
			return Product.Intellectual;
		}
		return null;
	}
	
	/**
	 * Get the goal type of the specified task type.
	 * 
	 * @param type
	 * @return
	 */
	public static Goal getGoal( TRECSessionTaskType type ) {
		if ( type.equals( KnownItem ) || type.equals( Interpretive ) ) {
			return Goal.Specific;
		} else if ( type.equals( KnownSubject ) || type.equals( Exploratory ) ) {
			return Goal.Amorphous;
		}
		return null;
	}
	
	/**
	 * Get the corresponding task type with the specified product and goal type.
	 * 
	 * @param product
	 * @param goal
	 * @return
	 */
	public static TRECSessionTaskType getTaskType( Product product, Goal goal ) {
		if ( product.equals( Product.Factual ) && goal.equals( Goal.Specific ) ) {
			return TRECSessionTaskType.KnownItem;
		} else if ( product.equals( Product.Factual ) && goal.equals( Goal.Amorphous ) ) {
			return TRECSessionTaskType.KnownSubject;
		} else if ( product.equals( Product.Intellectual ) && goal.equals( Goal.Specific ) ) {
			return TRECSessionTaskType.Interpretive;
		} else if ( product.equals( Product.Intellectual ) && goal.equals( Goal.Amorphous ) ) {
			return TRECSessionTaskType.Exploratory;
		}
		return null;
	}
	
}
