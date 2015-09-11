package utils.ir.dataset.topic.session;

/**
 * ResultClick stores click information of a result in a search session.
 * 
 * @author Jiepu Jiang
 * @version Sep 2, 2014
 */
public class TRECResultClick {
	
	protected TRECResultSummary result;
	protected double time_bg;
	protected double time_ed;
	
	/**
	 * Get the result being clicked.
	 */
	public TRECResultSummary getResult() {
		return this.result;
	}
	
	/**
	 * Check whether the dwelltime recorded in the log is valid or not.
	 */
	public boolean hasValidDwelltime() {
		if ( time_bg <= 0 || time_ed <= 0 || time_ed - time_bg <= 0 ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get the dwelltime of the click. If non-positive values are returned, it means something is going wrong and the dwelltime cannot be determined.
	 */
	public double getDwelltime() {
		return time_ed - time_bg;
	}
	
}
