package utils.classification;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.exec.ExecuteException;

import utils.FileUtils;
import utils.IOUtils;

public class LibSVMUtils {
	
	/**
	 * Normalize all feature values into the range 0-1.
	 * 
	 * @param instance_feature_values
	 */
	public static Map<String, Map<String, Double>> normalizeMinMax( Map<String, Map<String, Double>> instance_feature_values ) {
		
		Set<String> features = new TreeSet<String>();
		for ( Map<String, Double> feature_values : instance_feature_values.values() ) {
			features.addAll( feature_values.keySet() );
		}
		
		Map<String, Map<String, Double>> new_feature_values = new TreeMap<String, Map<String, Double>>();
		for ( String feature : features ) {
			List<Double> all_values = new ArrayList<Double>();
			for ( String instance : instance_feature_values.keySet() ) {
				Map<String, Double> feature_values = instance_feature_values.get( instance );
				Double value = feature_values.get( feature );
				if ( value == null || Double.isNaN( value ) ) {
					System.err.println( "invalid value for instance \"" + instance + "\" feature \"" + feature + "\": " + value );
				} else {
					all_values.add( value );
				}
			}
			Collections.sort( all_values );
			double min = all_values.get( 0 );
			double max = all_values.get( all_values.size() - 1 );
			if ( min == max ) {
				System.out.println( "skip feature " + feature + " because all instances have the same feature value " + max );
				continue;
			}
			for ( String instance : instance_feature_values.keySet() ) {
				Map<String, Double> feature_values = instance_feature_values.get( instance );
				Double value = feature_values.get( feature );
				if ( value == null || Double.isNaN( value ) ) {
					// System.err.println( "invalid value for instance \"" + instance + "\" feature \"" + feature + "\": " + value );
				} else {
					if ( !new_feature_values.containsKey( instance ) ) {
						new_feature_values.put( instance, new TreeMap<String, Double>() );
					}
					double newvalue = ( value - min ) / ( max - min );
					new_feature_values.get( instance ).put( feature, newvalue );
				}
			}
		}
		
		return new_feature_values;
		
	}
	
	public static void writeLibSVM( String path, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values ) throws IOException {
		writeLibSVM( new File( path ), instance_labels, instance_feature_values );
	}
	
	public static void writeLibSVM( File fout, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values ) throws IOException {
		
		Set<String> features = new TreeSet<String>();
		for ( Map<String, Double> feature_values : instance_feature_values.values() ) {
			features.addAll( feature_values.keySet() );
		}
		
		BufferedWriter writer = IOUtils.getBufferedWriter( fout );
		for ( String instance : instance_labels.keySet() ) {
			Map<String, Double> feature_values = instance_feature_values.get( instance );
			writer.write( instance_labels.get( instance ) );
			int featureix = 1;
			for ( String feature : features ) {
				Double value = feature_values.get( feature );
				if ( value != null && !Double.isNaN( value ) ) {
					writer.write( " " + featureix + ":" + value );
				}
				featureix++;
			}
			writer.write( "\n" );
		}
		writer.close();
		
	}
	
	public static void writeTabSeparated( String path, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values ) throws IOException {
		writeTabSeparated( new File( path ), instance_labels, instance_feature_values, true );
	}
	
	public static void writeTabSeparated( String path, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values, boolean headline ) throws IOException {
		writeTabSeparated( new File( path ), instance_labels, instance_feature_values, headline );
	}
	
	public static void writeTabSeparated( File fout, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values ) throws IOException {
		writeTabSeparated( fout, instance_labels, instance_feature_values, true );
	}
	
	public static void writeTabSeparated( File fout, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values, boolean headline ) throws IOException {
		
		Set<String> features = new TreeSet<String>();
		for ( Map<String, Double> feature_values : instance_feature_values.values() ) {
			features.addAll( feature_values.keySet() );
		}
		
		BufferedWriter writer = IOUtils.getBufferedWriter( fout );
		
		if ( headline ) {
			writer.write( "label" );
			for ( String feature : features ) {
				writer.write( "\t" + feature );
			}
			writer.write( "\n" );
		}
		
		for ( String instance : instance_labels.keySet() ) {
			Map<String, Double> feature_values = instance_feature_values.get( instance );
			int label = Integer.parseInt( instance_labels.get( instance ) );
			if ( label < 0 ) {
				label = 0;
			}
			writer.write( Integer.toString( label ) );
			for ( String feature : features ) {
				Double value = feature_values.get( feature );
				writer.write( "\t" + value );
			}
			writer.write( "\n" );
		}
		
		writer.close();
		
	}
	
	public static ClassificationEvaluations evaluate( File dir_libsvm, File dir_tmp, Map<String, String> instance_labels, Map<String, Map<String, Double>> instance_feature_values, int num_folds, int num_partitions, String svm_type, String kernal_type, Map<String, Double> parameters ) throws ExecuteException, IOException {
		
		File file_train = new File( dir_tmp, "train.tmp" );
		File file_model = new File( dir_tmp, "model.tmp" );
		File file_test = new File( dir_tmp, "test.tmp" );
		File file_predict = new File( dir_tmp, "predict.tmp" );
		
		ClassificationEvaluations evals = new ClassificationEvaluations();
		
		if ( !dir_tmp.exists() ) {
			dir_tmp.mkdirs();
		}
		
		for ( int partid = 0 ; partid < num_partitions ; partid++ ) {
			
			List<String> instances = new ArrayList<String>();
			instances.addAll( instance_feature_values.keySet() );
			Collections.shuffle( instances, new Random( partid ) );
			
			for ( int foldid = 0 ; foldid < num_folds ; foldid++ ) {
				
				String runname = ( partid + 1 ) + "-" + ( foldid + 1 );
				
				Map<String, String> train_labels = new TreeMap<String, String>();
				Map<String, String> test_labels = new TreeMap<String, String>();
				Map<String, Map<String, Double>> train_feature_values = new TreeMap<String, Map<String, Double>>();
				Map<String, Map<String, Double>> test_feature_values = new TreeMap<String, Map<String, Double>>();
				for ( int ix = 0 ; ix < instances.size() ; ix++ ) {
					String instance = instances.get( ix );
					if ( ix % num_folds == foldid ) {
						test_labels.put( instance, instance_labels.get( instance ) );
						test_feature_values.put( instance, instance_feature_values.get( instance ) );
					} else {
						train_labels.put( instance, instance_labels.get( instance ) );
						train_feature_values.put( instance, instance_feature_values.get( instance ) );
					}
				}
				
				BufferedWriter writer_train = IOUtils.getBufferedWriter( file_train );
				BufferedWriter writer_test = IOUtils.getBufferedWriter( file_test );
				LibSVMUtils.writeLibSVM( file_train, train_labels, train_feature_values );
				LibSVMUtils.writeLibSVM( file_test, test_labels, test_feature_values );
				writer_test.close();
				writer_train.close();
				
				LibSVM svm = new LibSVM( dir_libsvm );
				svm.train( file_train, file_model, svm_type, kernal_type, parameters );
				evals.addEvaluation( svm.predict( file_test, file_model, file_predict ).setName( runname ) );
				
			}
			
		}
		
		FileUtils.rm( file_train );
		FileUtils.rm( file_model );
		FileUtils.rm( file_test );
		FileUtils.rm( file_predict );
		
		return evals;
		
	}
	
	public static ClassificationEvaluation evaluateMostFrequent( Map<String, String> instance_labels ) throws IOException {
		
		Map<String, int[]> label_count = new TreeMap<String, int[]>();
		for ( String doc : instance_labels.keySet() ) {
			String label = instance_labels.get( doc );
			if ( !label_count.containsKey( label ) ) {
				label_count.put( label, new int[1] );
			}
			label_count.get( label )[0]++;
		}
		
		String max_label = null;
		double max = 0;
		double sum = 0;
		for ( String label : label_count.keySet() ) {
			double count = label_count.get( label )[0];
			if ( count > max ) {
				max = count;
				max_label = label;
			}
			sum = sum + count;
		}
		
		double accuracy = max / sum;
		double[] avg_evals = null;
		Map<String, double[]> class_evals = new TreeMap<String, double[]>();
		
		// max label
		double prec = max / sum;
		double recall = 1;
		double f1 = 2 * prec * recall / ( prec + recall );
		double acc = 1;
		class_evals.put( max_label, new double[] { f1, prec, recall, acc } );
		avg_evals = new double[] { f1 / label_count.size(), prec / label_count.size(), recall / label_count.size(), acc / label_count.size() };
		for ( String label : label_count.keySet() ) {
			if ( !label.equalsIgnoreCase( max_label ) ) {
				class_evals.put( label, new double[] { 0, 0, 0, 0 } );
			}
		}
		
		return new ClassificationEvaluationImpl( "mostfreq", accuracy, avg_evals, class_evals );
		
	}
	
}
