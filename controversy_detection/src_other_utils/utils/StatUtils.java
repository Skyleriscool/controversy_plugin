package utils;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import jsc.onesample.SignTest;
import jsc.datastructures.PairedData;
import jsc.correlation.KendallCorrelation;
import jsc.correlation.LinearCorrelation;

/**
 * Utility functions related to statistics. To keep consistency, whenever possible, we use the statistical test
 * implemented in apache commons math. JSC will be used only if apache commons math did not implement some of the
 * method.
 * 
 * @author Jiepu Jiang
 * @version Feb 25, 2013
 */
public class StatUtils {
	
	/**
	 * Get the sample mean of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double mean( double[] values ) {
		return org.apache.commons.math3.stat.StatUtils.mean( values );
	}
	
	/**
	 * Get the sample mean of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double mean( int[] values ) {
		return org.apache.commons.math3.stat.StatUtils.mean( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the sample mean of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double mean( long[] values ) {
		return org.apache.commons.math3.stat.StatUtils.mean( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the sample mean of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double mean( Collection<? extends Number> values ) {
		return org.apache.commons.math3.stat.StatUtils.mean( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the maximum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double max( double[] values ) {
		return org.apache.commons.math3.stat.StatUtils.max( values );
	}
	
	/**
	 * Get the maximum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double max( int[] values ) {
		return org.apache.commons.math3.stat.StatUtils.max( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the maximum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double max( long[] values ) {
		return org.apache.commons.math3.stat.StatUtils.max( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the maximum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double max( Collection<? extends Number> values ) {
		return org.apache.commons.math3.stat.StatUtils.max( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the minimum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double min( double[] values ) {
		return org.apache.commons.math3.stat.StatUtils.min( values );
	}
	
	/**
	 * Get the minimum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double min( int[] values ) {
		return org.apache.commons.math3.stat.StatUtils.min( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the minimum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double min( long[] values ) {
		return org.apache.commons.math3.stat.StatUtils.min( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the minimum value of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double min( Collection<? extends Number> values ) {
		return org.apache.commons.math3.stat.StatUtils.min( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the unbiased sample variance of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double var( double[] values ) {
		return org.apache.commons.math3.stat.StatUtils.variance( values );
	}
	
	/**
	 * Get the unbiased sample variance of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double var( int[] values ) {
		return org.apache.commons.math3.stat.StatUtils.variance( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the unbiased sample variance of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double var( long[] values ) {
		return org.apache.commons.math3.stat.StatUtils.variance( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the unbiased sample variance of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double var( Collection<? extends Number> values ) {
		return org.apache.commons.math3.stat.StatUtils.variance( ArrayUtils.toDoubleArray( values ) );
	}
	
	/**
	 * Get the corrected sample standard deviation of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sd( double[] values ) {
		return Math.pow( var( values ), 0.5 );
	}
	
	/**
	 * Get the corrected sample standard deviation of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sd( int[] values ) {
		return Math.pow( var( values ), 0.5 );
	}
	
	/**
	 * Get the corrected sample standard deviation of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sd( long[] values ) {
		return Math.pow( var( values ), 0.5 );
	}
	
	/**
	 * Get the corrected sample standard deviation of the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sd( Collection<? extends Number> values ) {
		return Math.pow( var( values ), 0.5 );
	}
	
	/**
	 * Get the standard error of the mean for the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sem( double[] values ) {
		return Math.pow( var( values ) / values.length, 0.5 );
	}
	
	/**
	 * Get the standard error of the mean for the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sem( int[] values ) {
		return Math.pow( var( values ) / values.length, 0.5 );
	}
	
	/**
	 * Get the standard error of the mean for the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sem( long[] values ) {
		return Math.pow( var( values ) / values.length, 0.5 );
	}
	
	/**
	 * Get the standard error of the mean for the observations.
	 * 
	 * @param values
	 * @return
	 */
	public static double sem( Collection<? extends Number> values ) {
		return Math.pow( var( values ) / values.size(), 0.5 );
	}
	
	/**
	 * Perform a two-tail paired t-test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double pairedTTest( double[] sample1, double[] sample2 ) {
		TTest ttest = new TTest();
		return ttest.pairedTTest( sample1, sample2 );
	}
	
	/**
	 * Perform a two-tail paired t-test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double pairedTTest( int[] sample1, int[] sample2 ) {
		return pairedTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a two-tail paired t-test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double pairedTTest( Collection<? extends Number> sample1, Collection<? extends Number> sample2 ) {
		return pairedTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a two-tail Welch t-test on the observed samples and report the p-value. It does not require the
	 * equal-variance assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double welchTTest( double[] sample1, double[] sample2 ) {
		TTest ttest = new TTest();
		return ttest.tTest( sample1, sample2 );
	}
	
	/**
	 * Perform a two-tail Welch t-test on the observed samples and report the p-value. It does not require the
	 * equal-variance assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double welchTTest( int[] sample1, int[] sample2 ) {
		return welchTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a two-tail Welch t-test on the observed samples and report the p-value. It does not require the
	 * equal-variance assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double welchTTest( Collection<? extends Number> sample1, Collection<? extends Number> sample2 ) {
		return welchTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a plain two-tail t-test on the observed samples and report the p-value. It requires the equal-variance
	 * assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double equalVarTTest( double[] sample1, double[] sample2 ) {
		TTest ttest = new TTest();
		return ttest.homoscedasticTTest( sample1, sample2 );
	}
	
	/**
	 * Perform a plain two-tail t-test on the observed samples and report the p-value. It requires the equal-variance
	 * assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double equalVarTTest( int[] sample1, int[] sample2 ) {
		return equalVarTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a plain two-tail t-test on the observed samples and report the p-value. It requires the equal-variance
	 * assumption.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double equalVarTTest( Collection<? extends Number> sample1, Collection<? extends Number> sample2 ) {
		return equalVarTTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a Wilcoxon signed rank test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double wilcoxonSignedRankTest( double[] sample1, double[] sample2 ) {
		WilcoxonSignedRankTest wilcoxon = new WilcoxonSignedRankTest();
		return wilcoxon.wilcoxonSignedRankTest( sample1, sample2, false );
	}
	
	/**
	 * Perform a Wilcoxon signed rank test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double wilcoxonSignedRankTest( int[] sample1, int[] sample2 ) {
		return wilcoxonSignedRankTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a Wilcoxon signed rank test on the observed samples and report the p-value.
	 * 
	 * @param sample1
	 * @param sample2
	 * @return p-value
	 */
	public static double wilcoxonSignedRankTest( Collection<? extends Number> sample1, Collection<? extends Number> sample2 ) {
		return wilcoxonSignedRankTest( ArrayUtils.toDoubleArray( sample1 ), ArrayUtils.toDoubleArray( sample2 ) );
	}
	
	/**
	 * Perform a chi-square test on the observed data.
	 * 
	 * @param expected
	 * @param observed
	 * @return
	 */
	public static double chiSquareTest( double[] expected, int[] observed ) {
		ChiSquareTest chi = new ChiSquareTest();
		return chi.chiSquareTest( expected, ArrayUtils.toLongArray( observed ) );
	}
	
	/**
	 * Perform a chi-square test on two paired observations.
	 * 
	 * @param expected
	 * @param observed
	 * @return
	 */
	public static double chiSquareTest( int[] observed1, int[] observed2 ) {
		ChiSquareTest chi = new ChiSquareTest();
		return chi.chiSquareDataSetsComparison( ArrayUtils.toLongArray( observed1 ), ArrayUtils.toLongArray( observed2 ) );
	}
	
	/**
	 * Perform a chi-square test on the observed two-way samples.
	 * 
	 * @param expected
	 * @param observed
	 * @return
	 */
	public static double chiSquareTest( int[][] observed ) {
		long[][] data = new long[observed.length][];
		for ( int i = 0; i < observed.length; i++ ) {
			data[i] = new long[observed[i].length];
			for ( int j = 0; j < data[i].length; j++ ) {
				data[i][j] = observed[i][j];
			}
		}
		ChiSquareTest chi = new ChiSquareTest();
		return chi.chiSquareTest( data );
	}
	
	/**
	 * Perform a sign test on the sample mean.
	 * 
	 * @param sample
	 * @param mean
	 * @return
	 */
	public static double signTest( double[] sample, double mean ) {
		SignTest sign = new SignTest( sample, mean );
		return sign.getSP();
	}
	
	/**
	 * Perform a sign test on the sample mean.
	 * 
	 * @param sample
	 * @param mean
	 * @return
	 */
	public static double signTest( int[] sample, double mean ) {
		return signTest( ArrayUtils.toDoubleArray( sample ), mean );
	}
	
	/**
	 * Perform a sign test on the sample mean.
	 * 
	 * @param sample
	 * @param mean
	 * @return
	 */
	public static double signTest( Collection<? extends Number> sample, double mean ) {
		return signTest( ArrayUtils.toDoubleArray( sample ), mean );
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( double[] var1, double[] var2, boolean remove_invalid ) {
		if ( remove_invalid ) {
			double[][] newvars = removeInvalid( var1, var2 );
			var1 = newvars[0];
			var2 = newvars[1];
		}
		LinearCorrelation pearson = new LinearCorrelation( new PairedData( var1, var2 ) );
		return new double[] { pearson.getTestStatistic(), pearson.getSP() };
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( double[] var1, double[] var2 ) {
		return pearsonsCorrelation( var1, var2, false );
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( int[] var1, int[] var2 ) {
		return pearsonsCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( int[] var1, int[] var2, boolean remove_invalid ) {
		return pearsonsCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2 ) {
		return pearsonsCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Pearson's product-moment correlation coefficients (Pearson's r). It returns the coefficient as well as
	 * the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] pearsonsCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2, boolean remove_invalid ) {
		return pearsonsCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( double[] var1, double[] var2, boolean remove_invalid ) {
		if ( remove_invalid ) {
			double[][] newvars = removeInvalid( var1, var2 );
			var1 = newvars[0];
			var2 = newvars[1];
		}
		KendallCorrelation kendall = new KendallCorrelation( new PairedData( var1, var2 ) );
		return new double[] { kendall.getTestStatistic(), kendall.getSP() };
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( double[] var1, double[] var2 ) {
		return spearmansCorrelation( var1, var2, false );
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( int[] var1, int[] var2, boolean remove_invalid ) {
		return spearmansCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( int[] var1, int[] var2 ) {
		return spearmansCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2, boolean remove_invalid ) {
		return spearmansCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Calculates Spearman's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] spearmansCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2 ) {
		return spearmansCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( double[] var1, double[] var2, boolean remove_invalid ) {
		if ( remove_invalid ) {
			double[][] newvars = removeInvalid( var1, var2 );
			var1 = newvars[0];
			var2 = newvars[1];
		}
		KendallCorrelation kendall = new KendallCorrelation( new PairedData( var1, var2 ) );
		return new double[] { kendall.getTestStatistic(), kendall.getSP() };
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( double[] var1, double[] var2 ) {
		return kendallCorrelation( var1, var2, false );
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( int[] var1, int[] var2 ) {
		return kendallCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( int[] var1, int[] var2, boolean remove_invalid ) {
		return kendallCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2 ) {
		return kendallCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ) );
	}
	
	/**
	 * Calculates Kendall's correlation coefficients. It returns the coefficient as well as the p value.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double[] kendallCorrelation( Collection<? extends Number> var1, Collection<? extends Number> var2, boolean remove_invalid ) {
		return kendallCorrelation( ArrayUtils.toDoubleArray( var1 ), ArrayUtils.toDoubleArray( var2 ), remove_invalid );
	}
	
	/**
	 * Remove invalid instances in paired observations.
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	private static double[][] removeInvalid( double[] var1, double[] var2 ) {
		Set<Integer> invalidPos = new TreeSet<Integer>();
		for ( int ix = 0; ix < var1.length; ix++ ) {
			if ( Double.isInfinite( var1[ix] ) || Double.isNaN( var1[ix] ) || Double.isInfinite( var2[ix] ) || Double.isNaN( var2[ix] ) ) {
				invalidPos.add( ix );
			}
		}
		double[] newvar1 = new double[var1.length - invalidPos.size()];
		double[] newvar2 = new double[var2.length - invalidPos.size()];
		int newix = 0;
		for ( int ix = 0; ix < var1.length; ix++ ) {
			if ( !invalidPos.contains( ix ) ) {
				newvar1[newix] = var1[ix];
				newvar2[newix] = var2[ix];
				newix++;
			}
		}
		return new double[][] { newvar1, newvar2 };
	}
	
	/**
	 * Get significance label: ** for p<0.01, * fro p<0.05, ∙ for p<0.1.
	 * 
	 * @param pval
	 * @return
	 */
	public static String getSigLabel( double pval ) {
		if ( pval < 0.001 ) {
			return "***";
		} else if ( pval < 0.01 ) {
			return "**";
		} else if ( pval < 0.05 ) {
			return "*";
		} else if ( pval < 0.1 ) {
			return "∙";
		}
		return "";
	}
	
}
