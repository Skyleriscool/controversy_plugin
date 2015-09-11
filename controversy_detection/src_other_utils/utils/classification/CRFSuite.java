package utils.classification;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import utils.DumbOutputStream;
import utils.IOUtils;
import utils.SystemUtils;

/**
 * Interface for calling CRFSuite and handling results.
 * 
 * @author Jiepu Jiang
 * @version Dec 4, 2014
 */
public class CRFSuite {
	
	protected boolean silent;
	protected File bindir;
	
	public CRFSuite( String path_bindir, boolean silent ) throws IOException {
		this( new File( path_bindir ), silent );
	}
	
	public CRFSuite( File bindir, boolean silent ) throws IOException {
		this.bindir = bindir;
		this.silent = silent;
		if ( !isValidBinDir() ) {
			throw new IOException( "Cannot find CRFSuite files! Check the CRFSuite bin folder: " + bindir.getAbsolutePath() );
		}
	}
	
	/**
	 * Check whether the folder contains CRFSuite command.
	 * 
	 * @return
	 */
	private boolean isValidBinDir() {
		if ( bindir.exists() ) {
			if ( SystemUtils.isWindows() ) {
				if ( new File( bindir, "crfsuite.exe" ).exists() ) {
					return true;
				}
			} else if ( SystemUtils.isLinux() ) {
				if ( new File( bindir, "crfsuite" ).exists() ) {
					return true;
				}
			} else if ( SystemUtils.isMac() ) {
				
			}
		}
		return false;
	}
	
	public static String formatFeature( String fstr ) {
		return fstr.replaceAll( ":", "_COLON_" );
	}
	
	/**
	 * Get CRFSuite command according to your system.
	 * 
	 * @return
	 */
	private String getCommand() {
		if ( SystemUtils.isWindows() ) {
			return new File( bindir, "crfsuite.exe" ).getAbsolutePath();
		} else if ( SystemUtils.isLinux() ) {
			return new File( bindir, "crfsuite" ).getAbsolutePath();
		} else if ( SystemUtils.isMac() ) {
			
		}
		return null;
	}
	
	/**
	 * Train model using lbfgs method.
	 * 
	 * @param path_model
	 * @param path_feature
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void train( String path_model, String path_feature ) throws IOException, InterruptedException {
		train( "lbfgs", path_model, path_feature );
	}
	
	/**
	 * Train model.
	 * 
	 * @param method
	 *            One of the following: lbfgs, l2sgd, ap, pa, arow.
	 * @param path_model
	 * @param path_feature
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void train( String method, String path_model, String path_feature ) throws IOException, InterruptedException {
		
		CommandLine cmd = CommandLine.parse( getCommand() + " learn -a " + method + " -m \"" + path_model + "\" \"" + path_feature + "\"" );
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler handler = new PumpStreamHandler( ( silent ? DumbOutputStream.get() : System.out ), System.err );
		executor.setStreamHandler( handler );
		
		executor.execute( cmd );
		
	}
	
	/**
	 * Classify results and output.
	 * 
	 * @param path_model
	 * @param path_feature
	 * @param path_output
	 * @param printProbability
	 * @param printMarginal
	 * @throws IOException
	 */
	public void classify( String path_model, String path_feature, String path_output, boolean printProbability, boolean printMarginal ) throws IOException {
		
		CommandLine cmd = CommandLine.parse( getCommand() + " tag " + ( printProbability ? "-p " : "" ) + ( printMarginal ? "-i " : "" ) + "-m \"" + path_model + "\" \"" + path_feature + "\"" );
		DefaultExecutor executor = new DefaultExecutor();
		OutputStream fos = new FileOutputStream( path_output );
		PumpStreamHandler handler = new PumpStreamHandler( fos, System.err );
		executor.setStreamHandler( handler );
		
		executor.execute( cmd );
		fos.close();
		
	}
	
	/**
	 * Stores sequence prediction information from CRFSuite.
	 */
	public static class SequencePrediction {
		public double seq_prob;
		public List<String> predicted_labels;
		public List<Double> prob_predicted_labels;
	}
	
	/**
	 * Parse a CRFSuite prediction file.
	 * 
	 * @param file
	 * @param printProbability
	 * @param printMarginal
	 * @return
	 * @throws IOException
	 */
	public List<SequencePrediction> parsePrediction( File file, boolean printProbability, boolean printMarginal ) throws IOException {
		InputStream instream = new FileInputStream( file );
		List<SequencePrediction> predictions = parsePrediction( instream, printProbability, printMarginal );
		instream.close();
		return predictions;
	}
	
	/**
	 * Parse a CRFSuite prediction file.
	 * 
	 * @param instream
	 * @param printProbability
	 * @param printMarginal
	 * @return
	 * @throws IOException
	 */
	public List<SequencePrediction> parsePrediction( InputStream instream, boolean printProbability, boolean printMarginal ) throws IOException {
		List<SequencePrediction> predictions = new ArrayList<SequencePrediction>();
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		List<String> buffer = new ArrayList<String>();
		String line = reader.readLine();
		while ( line != null ) {
			line = line.trim();
			if ( line.length() == 0 ) {
				if ( buffer.size() > 0 ) {
					SequencePrediction prediction = new SequencePrediction();
					prediction.predicted_labels = new ArrayList<String>();
					if ( printMarginal ) {
						prediction.prob_predicted_labels = new ArrayList<Double>();
					}
					int startix = 0;
					if ( printProbability ) {
						startix = 1;
						String str = buffer.get( 0 ).replace( "@probability", "" ).replace( "\\s+", "" ).trim();
						prediction.seq_prob = Double.parseDouble( str );
					}
					for ( int ix = startix ; ix < buffer.size() ; ix++ ) {
						String str = buffer.get( ix ).trim();
						if ( !printMarginal ) {
							prediction.predicted_labels.add( str );
						} else {
							String[] splits = str.split( ":" );
							prediction.predicted_labels.add( splits[0].trim() );
							prediction.prob_predicted_labels.add( Double.parseDouble( splits[1].trim() ) );
						}
					}
					predictions.add( prediction );
					buffer.clear();
				}
			} else {
				buffer.add( line );
			}
			line = reader.readLine();
		}
		if ( buffer.size() > 0 ) {
			SequencePrediction prediction = new SequencePrediction();
			prediction.predicted_labels = new ArrayList<String>();
			if ( printMarginal ) {
				prediction.prob_predicted_labels = new ArrayList<Double>();
			}
			int startix = 0;
			if ( printProbability ) {
				startix = 1;
				String str = buffer.get( 0 ).replace( "@probability", "" ).replace( "\\s+", "" ).trim();
				prediction.seq_prob = Double.parseDouble( str );
			}
			for ( int ix = startix ; ix < buffer.size() ; ix++ ) {
				String str = buffer.get( ix ).trim();
				if ( !printMarginal ) {
					prediction.predicted_labels.add( str );
				} else {
					String[] splits = str.split( ":" );
					prediction.predicted_labels.add( splits[0].trim() );
					prediction.prob_predicted_labels.add( Double.parseDouble( splits[1].trim() ) );
				}
			}
			predictions.add( prediction );
		}
		reader.close();
		return predictions;
	}
	
	/**
	 * Load the label feature weights from the model file.
	 * 
	 * @param path_model
	 * @return
	 * @throws IOException
	 */
	public Map<String, Map<String, Double>> dumpModel( String path_model ) throws IOException {
		return dumpModel( new File( path_model ) );
	}
	
	/**
	 * Load the label feature weights from the model file.
	 * 
	 * @param path_model
	 * @return
	 * @throws IOException
	 */
	public Map<String, Map<String, Double>> dumpModel( File fmodel ) throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		CommandLine cmd = CommandLine.parse( getCommand() + " dump " + fmodel.getAbsolutePath() );
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler handler = new PumpStreamHandler( bos, System.err );
		executor.setStreamHandler( handler );
		executor.execute( cmd );
		
		String content = bos.toString( "UTF-8" );
		bos.close();
		
		Map<String, Map<String, Double>> label_feature_weights = new TreeMap<>();
		BufferedReader reader = new BufferedReader( new StringReader( content ) );
		String line = reader.readLine();
		Pattern p = Pattern.compile( "  \\(0\\) ([^\\s]+) \\-\\-> ([^:\\s]+): ([^\\s]+)", Pattern.DOTALL + Pattern.CASE_INSENSITIVE + Pattern.MULTILINE );
		while ( line != null ) {
			Matcher m = p.matcher( line );
			if ( m.matches() ) {
				String feature = m.group( 1 ).trim();
				String label = m.group( 2 ).trim();
				double weight = Double.parseDouble( m.group( 3 ) );
				if ( !label_feature_weights.containsKey( label ) ) {
					label_feature_weights.put( label, new TreeMap<String, Double>() );
				}
				label_feature_weights.get( label ).put( feature, weight );
			}
			line = reader.readLine();
		}
		reader.close();
		
		return label_feature_weights;
		
	}
	
	/**
	 * Evaluate the prediction file with the groundtruth file. Returning F1, precision and recall.
	 * 
	 * @param fgroundtruth
	 * @param fpredict
	 * @param targets
	 * @return
	 * @throws IOException
	 */
	public Map<String, double[]> evaluatePrediction( File fgroundtruth, File fpredict, String... targets ) throws IOException {
		Set<String> targetset = new TreeSet<String>();
		for ( String target : targets ) {
			targetset.add( target );
		}
		return evaluatePrediction( fgroundtruth, fpredict, targetset );
	}
	
	/**
	 * Evaluate the prediction file with the groundtruth file. Returning F1, precision and recall.
	 * 
	 * @param fgroundtruth
	 * @param fpredict
	 * @param targets
	 * @return
	 * @throws IOException
	 */
	public Map<String, double[]> evaluatePrediction( File fgroundtruth, File fpredict, Set<String> targets ) throws IOException {
		Map<String, double[]> label_metrics = new TreeMap<String, double[]>();
		Map<String, Set<String>> label_groundtruth = loadLabels( fgroundtruth, targets );
		Map<String, Set<String>> label_predict = loadLabels( fpredict, targets );
		for ( String label : label_groundtruth.keySet() ) {
			Set<String> groundtruth = label_groundtruth.get( label );
			Set<String> predict = label_predict.get( label );
			double precision = 0;
			double recall = 0;
			double f1 = 0;
			if ( groundtruth != null && predict != null ) {
				double common = 0;
				for ( String occurrence : predict ) {
					if ( groundtruth.contains( occurrence ) ) {
						common++;
					}
				}
				precision = common / predict.size();
				recall = common / groundtruth.size();
				if ( precision + recall > 0 ) {
					f1 = 2 * precision * recall / ( precision + recall );
				}
			}
			label_metrics.put( label, new double[] { f1, precision, recall } );
		}
		return label_metrics;
	}
	
	/**
	 * Load occurrences of the target label from the specified file. Each line of the file starts with the label, or it is an empty line.
	 * 
	 * @param file
	 * @param targets
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> loadLabels( File file, String... targets ) throws IOException {
		Set<String> targetset = new TreeSet<String>();
		for ( String target : targets ) {
			targetset.add( target );
		}
		return loadLabels( file, targetset );
	}
	
	/**
	 * Load occurrences of the target label from the specified file. Each line of the file starts with the label, or it is an empty line.
	 * 
	 * @param file
	 * @param targets
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Set<String>> loadLabels( File file, Set<String> targets ) throws IOException {
		Map<String, Set<String>> labels = new TreeMap<String, Set<String>>();
		BufferedReader reader = IOUtils.getBufferedReader( file );
		String line = reader.readLine();
		int linenumber = 0;
		String entity = null;
		int entity_start = -1;
		while ( line != null ) {
			line = line.trim();
			String[] splits = null;
			if ( line.length() > 0 ) {
				splits = line.split( "\\s+" );
			}
			if ( splits != null && splits.length > 0 && targets.contains( splits[0] ) ) {
				if ( entity == null ) {
					entity = splits[0];
					entity_start = linenumber;
				} else {
					if ( !entity.equals( splits[0] ) ) {
						String occurrence = entity_start + "-" + ( linenumber - 1 );
						if ( !labels.containsKey( entity ) ) {
							labels.put( entity, new TreeSet<String>() );
						}
						labels.get( entity ).add( occurrence );
						entity = splits[0];
						entity_start = linenumber;
					}
				}
			} else {
				if ( entity != null ) {
					String occurrence = entity_start + "-" + ( linenumber - 1 );
					if ( !labels.containsKey( entity ) ) {
						labels.put( entity, new TreeSet<String>() );
					}
					labels.get( entity ).add( occurrence );
					entity = null;
					entity_start = -1;
				}
			}
			linenumber++;
			line = reader.readLine();
		}
		if ( entity != null ) {
			String occurrence = entity_start + "-" + ( linenumber - 1 );
			if ( !labels.containsKey( entity ) ) {
				labels.put( entity, new TreeSet<String>() );
			}
			labels.get( entity ).add( occurrence );
		}
		reader.close();
		return labels;
	}
	
}
