package utils.classification;

import java.util.Set;

public interface ClassificationEvaluation {
	
	public Set<String> labels();
	
	public double overallAccuracy();
	
	public Double averageMetric( String metric );
	
	public double averageF1();
	
	public double averagePrecision();
	
	public double averageRecall();
	
	public double averageAccuracy();
	
	public Double metric( String label, String metric );
	
	public double F1( String label );
	
	public double precision( String label );
	
	public double recall( String label );
	
	public double accuracy( String label );
	
}
