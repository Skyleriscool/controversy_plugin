package utils.classification;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;

import utils.StatUtils;

public class ClassificationEvaluations implements ClassificationEvaluation {
	
	protected String name;
	
	protected List<Double> accuracy;
	protected List[] avg_evals;
	protected Map<String, List[]> class_evals;
	
	protected List<ClassificationEvaluation> evals;
	
	public ClassificationEvaluations() {
		this( null );
	}
	
	public ClassificationEvaluations( String name ) {
		this.name = name;
		this.accuracy = new ArrayList<Double>();
		this.avg_evals = new List[4];
		for ( int ix = 0 ; ix < this.avg_evals.length ; ix++ ) {
			this.avg_evals[ix] = new ArrayList<Double>();
		}
		this.class_evals = new TreeMap<String, List[]>();
		this.evals = new ArrayList<ClassificationEvaluation>();
	}
	
	public void addEvaluation( ClassificationEvaluation eval ) {
		evals.add( eval );
		this.accuracy.add( eval.overallAccuracy() );
		this.avg_evals[0].add( eval.averageF1() );
		this.avg_evals[1].add( eval.averagePrecision() );
		this.avg_evals[2].add( eval.averageRecall() );
		this.avg_evals[3].add( eval.averageAccuracy() );
		for ( String label : eval.labels() ) {
			if ( !class_evals.containsKey( label ) ) {
				class_evals.put( label, new List[4] );
				for ( int ix = 0 ; ix < class_evals.get( label ).length ; ix++ ) {
					class_evals.get( label )[ix] = new ArrayList<Double>();
				}
			}
			class_evals.get( label )[0].add( eval.F1( label ) );
			class_evals.get( label )[1].add( eval.precision( label ) );
			class_evals.get( label )[2].add( eval.recall( label ) );
			class_evals.get( label )[3].add( eval.accuracy( label ) );
		}
	}
	
	public int size() {
		return evals.size();
	}
	
	public ClassificationEvaluation get( int ix ) {
		return evals.get( ix );
	}
	
	public String name() {
		return this.name;
	}
	
	public ClassificationEvaluations setName( String name ) {
		this.name = name;
		return this;
	}
	
	public Set<String> labels() {
		return class_evals.keySet();
	}
	
	public double overallAccuracy() {
		return StatUtils.mean( accuracy );
	}
	
	public double averageF1() {
		return StatUtils.mean( avg_evals[0] );
	}
	
	public double averagePrecision() {
		return StatUtils.mean( avg_evals[1] );
	}
	
	public double averageRecall() {
		return StatUtils.mean( avg_evals[2] );
	}
	
	public double averageAccuracy() {
		return StatUtils.mean( avg_evals[3] );
	}
	
	public double F1( String label ) {
		return StatUtils.mean( class_evals.get( label )[0] );
	}
	
	public double precision( String label ) {
		return StatUtils.mean( class_evals.get( label )[1] );
	}
	
	public double recall( String label ) {
		return StatUtils.mean( class_evals.get( label )[2] );
	}
	
	public double accuracy( String label ) {
		return StatUtils.mean( class_evals.get( label )[3] );
	}
	
	public double SDOverallAccuracy() {
		return StatUtils.sd( accuracy );
	}
	
	public double SDAverageF1() {
		return StatUtils.sd( avg_evals[0] );
	}
	
	public double SDAveragePrecision() {
		return StatUtils.sd( avg_evals[1] );
	}
	
	public double SDAverageRecall() {
		return StatUtils.sd( avg_evals[2] );
	}
	
	public double SDAverageAccuracy() {
		return StatUtils.sd( avg_evals[3] );
	}
	
	public double SDF1( String label ) {
		return StatUtils.sd( class_evals.get( label )[0] );
	}
	
	public double SDPrecision( String label ) {
		return StatUtils.sd( class_evals.get( label )[1] );
	}
	
	public double SDRecall( String label ) {
		return StatUtils.sd( class_evals.get( label )[2] );
	}
	
	public double SDAccuracy( String label ) {
		return StatUtils.sd( class_evals.get( label )[3] );
	}
	
	public static double welchTTestOverallAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.welchTTest( evals1.accuracy, evals2.accuracy );
	}
	
	public static double welchTTestAverageF1( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.welchTTest( evals1.avg_evals[0], evals2.avg_evals[0] );
	}
	
	public static double welchTTestAveragePrecision( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.welchTTest( evals1.avg_evals[1], evals2.avg_evals[1] );
	}
	
	public static double welchTTestAverageRecall( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.welchTTest( evals1.avg_evals[2], evals2.avg_evals[2] );
	}
	
	public static double welchTTestAverageAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.welchTTest( evals1.avg_evals[3], evals2.avg_evals[3] );
	}
	
	public static double welchTTestF1( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.welchTTest( evals1.class_evals.get( label )[0], evals2.class_evals.get( label )[0] );
	}
	
	public static double welchTTestPrecision( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.welchTTest( evals1.class_evals.get( label )[1], evals2.class_evals.get( label )[1] );
	}
	
	public static double welchTTestRecall( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.welchTTest( evals1.class_evals.get( label )[2], evals2.class_evals.get( label )[2] );
	}
	
	public static double welchTTestAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.welchTTest( evals1.class_evals.get( label )[3], evals2.class_evals.get( label )[3] );
	}
	
	public static double pairedTTestOverallAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.pairedTTest( evals1.accuracy, evals2.accuracy );
	}
	
	public static double pairedTTestAverageF1( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.pairedTTest( evals1.avg_evals[0], evals2.avg_evals[0] );
	}
	
	public static double pairedTTestAveragePrecision( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.pairedTTest( evals1.avg_evals[1], evals2.avg_evals[1] );
	}
	
	public static double pairedTTestAverageRecall( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.pairedTTest( evals1.avg_evals[2], evals2.avg_evals[2] );
	}
	
	public static double pairedTTestAverageAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2 ) {
		return StatUtils.pairedTTest( evals1.avg_evals[3], evals2.avg_evals[3] );
	}
	
	public static double pairedTTestF1( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.pairedTTest( evals1.class_evals.get( label )[0], evals2.class_evals.get( label )[0] );
	}
	
	public static double pairedTTestPrecision( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.pairedTTest( evals1.class_evals.get( label )[1], evals2.class_evals.get( label )[1] );
	}
	
	public static double pairedTTestRecall( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.pairedTTest( evals1.class_evals.get( label )[2], evals2.class_evals.get( label )[2] );
	}
	
	public static double pairedTTestAccuracy( ClassificationEvaluations evals1, ClassificationEvaluations evals2, String label ) {
		return StatUtils.pairedTTest( evals1.class_evals.get( label )[3], evals2.class_evals.get( label )[3] );
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
