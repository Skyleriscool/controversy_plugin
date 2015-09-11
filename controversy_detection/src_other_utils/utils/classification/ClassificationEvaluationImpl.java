package utils.classification;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ClassificationEvaluationImpl implements ClassificationEvaluation {
	
	protected String name;
	
	protected double accuracy;
	protected double[] avg_evals;
	protected Map<String, double[]> class_evals;
	
	public ClassificationEvaluationImpl() {
		this( null );
	}
	
	public ClassificationEvaluationImpl( String name ) {
		this.name = name;
	}
	
	public ClassificationEvaluationImpl( double accuracy, double[] avg_evals, Map<String, double[]> class_evals ) {
		this( null, accuracy, avg_evals, class_evals );
	}
	
	public ClassificationEvaluationImpl( String name, double accuracy, double[] avg_evals, Map<String, double[]> class_evals ) {
		this.name = name;
		this.accuracy = accuracy;
		this.avg_evals = avg_evals;
		this.class_evals = class_evals;
	}
	
	public String name() {
		return this.name;
	}
	
	public ClassificationEvaluationImpl setName( String name ) {
		this.name = name;
		return this;
	}
	
	public Set<String> labels() {
		return class_evals.keySet();
	}
	
	public double overallAccuracy() {
		return accuracy;
	}
	
	public double averageF1() {
		return avg_evals[0];
	}
	
	public double averagePrecision() {
		return avg_evals[1];
	}
	
	public double averageRecall() {
		return avg_evals[2];
	}
	
	public double averageAccuracy() {
		return avg_evals[3];
	}
	
	public double F1( String label ) {
		return class_evals.get( label )[0];
	}
	
	public double precision( String label ) {
		return class_evals.get( label )[1];
	}
	
	public double recall( String label ) {
		return class_evals.get( label )[2];
	}
	
	public double accuracy( String label ) {
		return class_evals.get( label )[3];
	}
	
	protected ClassificationEvaluationImpl calculateMetrics( double count_correct, double count_total, Map<String, double[]> class_counts ) {
		
		accuracy = count_total == 0 ? 0 : ( count_correct / count_total );
		
		avg_evals = new double[4];
		class_evals = new TreeMap<String, double[]>();
		for ( String label : class_counts.keySet() ) {
			double[] counts = class_counts.get( label );
			double prec = ( counts[0] + counts[1] == 0 ) ? 0 : ( counts[0] / ( counts[0] + counts[1] ) );
			double recall = ( counts[0] + counts[2] == 0 ) ? 0 : ( counts[0] / ( counts[0] + counts[2] ) );
			double f1 = ( prec + recall == 0 ) ? 0 : ( 2 * prec * recall / ( prec + recall ) );
			double acc = ( counts[0] + counts[1] + counts[2] + counts[3] == 0 ) ? 0 : ( ( counts[0] + counts[3] ) / ( counts[0] + counts[1] + counts[2] + counts[3] ) );
			class_evals.put( label, new double[] { f1, prec, recall, acc } );
			avg_evals[0] += f1;
			avg_evals[1] += prec;
			avg_evals[2] += recall;
			avg_evals[3] += acc;
		}
		
		avg_evals[0] /= class_evals.size();
		avg_evals[1] /= class_evals.size();
		avg_evals[2] /= class_evals.size();
		avg_evals[3] /= class_evals.size();
		
		return this;
		
	}
	
	public Double averageMetric( String metric ) {
		if ( metric.equalsIgnoreCase( "F1" ) ) {
			return averageF1();
		} else if ( metric.equalsIgnoreCase( "precision" ) || metric.equalsIgnoreCase( "prec" ) ) {
			return averagePrecision();
		} else if ( metric.equalsIgnoreCase( "recall" ) || metric.equalsIgnoreCase( "rec" ) ) {
			return averageRecall();
		} else if ( metric.equalsIgnoreCase( "accuracy" ) || metric.equalsIgnoreCase( "acc" ) ) {
			return averageAccuracy();
		}
		return null;
	}
	
	public Double metric( String label, String metric ) {
		if ( metric.equalsIgnoreCase( "F1" ) ) {
			return F1( label );
		} else if ( metric.equalsIgnoreCase( "precision" ) || metric.equalsIgnoreCase( "prec" ) ) {
			return precision( label );
		} else if ( metric.equalsIgnoreCase( "recall" ) || metric.equalsIgnoreCase( "rec" ) ) {
			return recall( label );
		} else if ( metric.equalsIgnoreCase( "accuracy" ) || metric.equalsIgnoreCase( "acc" ) ) {
			return accuracy( label );
		}
		return null;
	}
	
}
