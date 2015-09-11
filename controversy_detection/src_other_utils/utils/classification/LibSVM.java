package utils.classification;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import utils.IOUtils;
import utils.SystemUtils;
import utils.DumbOutputStream;

public class LibSVM {
	
	protected File dir;
	
	public LibSVM( String path_bindir ) throws IOException {
		this( new File( path_bindir ) );
	}
	
	public LibSVM( File bindir ) throws IOException {
		this.dir = bindir;
	}
	
	public String getPathCommandTrain() {
		if ( SystemUtils.getOS() == SystemUtils.OS.Linux ) {
			return new File( dir, "svm-train" ).getAbsolutePath();
		} else if ( SystemUtils.getOS() == SystemUtils.OS.Windows ) {
			return new File( dir, "svm-train.exe" ).getAbsolutePath();
		}
		return null;
	}
	
	public String getPathCommandPredict() {
		if ( SystemUtils.getOS() == SystemUtils.OS.Linux ) {
			return new File( dir, "svm-predict" ).getAbsolutePath();
		} else if ( SystemUtils.getOS() == SystemUtils.OS.Windows ) {
			return new File( dir, "svm-predict.exe" ).getAbsolutePath();
		}
		return null;
	}
	
	public void train( String path_feature, String path_model ) throws ExecuteException, IOException {
		train( new File( path_feature ), new File( path_model ) );
	}
	
	public void train( File f_feature, File f_model ) throws ExecuteException, IOException {
		train( f_feature, f_model, null, null, null );
	}
	
	public void train( String path_feature, String path_model, String svm_type, String kernal_type, Map<String, Double> parameters ) throws ExecuteException, IOException {
		train( new File( path_feature ), new File( path_model ), svm_type, kernal_type, parameters );
	}
	
	public void train( File f_feature, File f_model, String svm_type, String kernal_type, Map<String, Double> parameters ) throws ExecuteException, IOException {
		StringBuilder command = new StringBuilder();
		command.append( getPathCommandTrain() );
		if ( svm_type != null ) {
			command.append( " -s " + svm_type );
			if ( svm_type.equalsIgnoreCase( "0" ) ) {
				// C-SVC; parameter: cost
				if ( parameters != null ) {
					if ( parameters.containsKey( "cost" ) ) {
						command.append( " -c " + parameters.get( "cost" ) );
					}
				}
			}
			// TODO add parameters for other svm models
		}
		if ( kernal_type != null ) {
			command.append( " -t " + kernal_type );
			if ( kernal_type.equalsIgnoreCase( "0" ) ) {
				// linear; no parameter
			} else if ( kernal_type.equalsIgnoreCase( "1" ) ) {
				// polynomial; parameters: degree, gamma, coef0
				if ( parameters != null ) {
					if ( parameters.containsKey( "degree" ) ) {
						command.append( " -d " + parameters.get( "degree" ) );
					}
					if ( parameters.containsKey( "gamma" ) ) {
						command.append( " -g " + parameters.get( "gamma" ) );
					}
					if ( parameters.containsKey( "coef0" ) ) {
						command.append( " -r " + parameters.get( "coef0" ) );
					}
				}
			} else if ( kernal_type.equalsIgnoreCase( "2" ) ) {
				// radial basis function; parameters: gamma
				if ( parameters != null ) {
					if ( parameters.containsKey( "gamma" ) ) {
						command.append( " -g " + parameters.get( "gamma" ) );
					}
				}
			} else if ( kernal_type.equalsIgnoreCase( "2" ) ) {
				// sigmoid; parameters: gamma, coef0
				if ( parameters != null ) {
					if ( parameters.containsKey( "gamma" ) ) {
						command.append( " -g " + parameters.get( "gamma" ) );
					}
					if ( parameters.containsKey( "coef0" ) ) {
						command.append( " -r " + parameters.get( "coef0" ) );
					}
				}
			}
		}
		command.append( " " + f_feature.getAbsolutePath() + " " + f_model.getAbsolutePath() );
		CommandLine cmd = CommandLine.parse( command.toString() );
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler handler = new PumpStreamHandler( DumbOutputStream.get(), System.err );
		executor.setStreamHandler( handler );
		executor.execute( cmd );
	}
	
	public ClassificationEvaluationImpl predict( String path_feature, String path_model, String path_output ) throws ExecuteException, IOException {
		return predict( null, path_feature, path_model, path_output );
	}
	
	public ClassificationEvaluationImpl predict( Map<String, String> label_mapping, String path_feature, String path_model, String path_output ) throws ExecuteException, IOException {
		return predict( label_mapping, new File( path_feature ), new File( path_model ), new File( path_output ) );
	}
	
	public ClassificationEvaluationImpl predict( File f_feature, File f_model, File f_output ) throws ExecuteException, IOException {
		return predict( null, f_feature, f_model, f_output );
	}
	
	/**
	 * Classify and evaluate. labels stores the label mapping, e.g., "1" -> "relevant", "-1" -> "non-relevant". It can be null, in which case: 1) the whole set
	 * of labels appeared in the data will be considered the full set of labels when calculating average measures; 2) it will not store semantic labels in evals
	 * and will simply use the numeric labels.
	 */
	public ClassificationEvaluationImpl predict( Map<String, String> label_mapping, File f_feature, File f_model, File f_output ) throws ExecuteException, IOException {
		CommandLine cmd = CommandLine.parse( getPathCommandPredict() + " " + f_feature.getAbsolutePath() + " " + f_model.getAbsolutePath() + " " + f_output.getAbsolutePath() );
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler handler = new PumpStreamHandler( DumbOutputStream.get(), System.err );
		executor.setStreamHandler( handler );
		executor.execute( cmd );
		return evaluate( label_mapping, f_feature, f_output );
	}
	
	/**
	 * Evaluate the prediction based on the specified groudtruth data. labels stores the label mapping, e.g., "1" -> "relevant", "-1" -> "non-relevant". It can
	 * be null, in which case: 1) the whole set of labels appeared in the data will be considered the full set of labels when calculating average measures; 2)
	 * it will not store semantic labels in evals and will simply use the numeric labels.
	 * 
	 * @param labels
	 * @param f_feature
	 * @param f_output
	 * @return
	 * @throws IOException
	 */
	public ClassificationEvaluationImpl evaluate( String path_feature, String path_output ) throws IOException {
		return evaluate( new File( path_feature ), new File( path_output ) );
	}
	
	/**
	 * Evaluate the prediction based on the specified groudtruth data. labels stores the label mapping, e.g., "1" -> "relevant", "-1" -> "non-relevant". It can
	 * be null, in which case: 1) the whole set of labels appeared in the data will be considered the full set of labels when calculating average measures; 2)
	 * it will not store semantic labels in evals and will simply use the numeric labels.
	 * 
	 * @param label_mapping
	 * @param f_feature
	 * @param f_output
	 * @return
	 * @throws IOException
	 */
	public ClassificationEvaluationImpl evaluate( Map<String, String> label_mapping, String path_feature, String path_output ) throws IOException {
		return evaluate( label_mapping, new File( path_feature ), new File( path_output ) );
	}
	
	/**
	 * Evaluate the prediction based on the specified groudtruth data. labels stores the label mapping, e.g., "1" -> "relevant", "-1" -> "non-relevant". It can
	 * be null, in which case: 1) the whole set of labels appeared in the data will be considered the full set of labels when calculating average measures; 2)
	 * it will not store semantic labels in evals and will simply use the numeric labels.
	 * 
	 * @param labels
	 * @param f_feature
	 * @param f_output
	 * @return
	 * @throws IOException
	 */
	public ClassificationEvaluationImpl evaluate( File f_feature, File f_output ) throws IOException {
		return evaluate( null, f_feature, f_output );
	}
	
	/**
	 * Evaluate the prediction based on the specified groudtruth data. labels stores the label mapping, e.g., "1" -> "relevant", "-1" -> "non-relevant". It can
	 * be null, in which case: 1) the whole set of labels appeared in the data will be considered the full set of labels when calculating average measures; 2)
	 * it will not store semantic labels in evals and will simply use the numeric labels.
	 * 
	 * @param label_mapping
	 * @param f_feature
	 * @param f_output
	 * @return
	 * @throws IOException
	 */
	public ClassificationEvaluationImpl evaluate( Map<String, String> label_mapping, File f_feature, File f_output ) throws IOException {
		
		List<String> truth = loadLabels( f_feature );
		List<String> predicted = loadLabels( f_output );
		
		if ( label_mapping == null ) {
			label_mapping = new TreeMap<String, String>();
			for ( String label : truth ) {
				label_mapping.put( label, label );
			}
			for ( String label : predicted ) {
				label_mapping.put( label, label );
			}
		}
		
		double count_correct = 0;
		double count_total = 0;
		Map<String, double[]> class_counts = new TreeMap<String, double[]>();
		for ( String label : label_mapping.values() ) {
			class_counts.put( label, new double[4] );
		}
		
		for ( int ix = 0 ; ix < truth.size() ; ix++ ) {
			
			String label_truth = label_mapping.get( truth.get( ix ) );
			String label_predicted = label_mapping.get( predicted.get( ix ) );
			
			if ( label_truth.equalsIgnoreCase( label_predicted ) ) {
				count_correct++;
				class_counts.get( label_truth )[0]++; // TP
				for ( String label : label_mapping.values() ) {
					if ( !label.equalsIgnoreCase( label_truth ) ) {
						class_counts.get( label )[3]++; // TN
					}
				}
			} else {
				class_counts.get( label_predicted )[1]++; // FP
				class_counts.get( label_truth )[2]++; // FN
			}
			
			count_total++;
			
		}
		
		return new ClassificationEvaluationImpl().calculateMetrics( count_correct, count_total, class_counts );
		
	}
	
	private List<String> loadLabels( File f ) throws IOException {
		List<String> labels = new ArrayList<String>();
		BufferedReader reader = IOUtils.getBufferedReader( f );
		String line = reader.readLine();
		while ( line != null ) {
			String label = line.split( "\\s+" )[0].trim();
			label = Integer.toString( Integer.parseInt( label ) );
			labels.add( label );
			line = reader.readLine();
		}
		reader.close();
		return labels;
	}
	
}
