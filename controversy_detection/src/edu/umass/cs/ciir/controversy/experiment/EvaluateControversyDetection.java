package edu.umass.cs.ciir.controversy.experiment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import utils.IOUtils;
import utils.StatUtils;

public class EvaluateControversyDetection {
	
	public static void main( String[] args ) {
		try {
			
			File dir_results = new File( "D:/controversy/experiments/score_index_random05" );
			
			System.out.println( "topwords\ttopentries\tcutoff\taccuracy\tP_F1\tP_prec\tP_rec\tN_F1\tN_prec\tN_rec" );
			Map<String, Boolean> groundtruth = loadGroundtruth();
			for ( File file : dir_results.listFiles() ) {
				evaluate( groundtruth, file, 10, "P_F1" );
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static Map<String, Boolean> loadGroundtruth() throws IOException {
		
		Map<String, List<Double>> url_ratings = new TreeMap<>();
		
		InputStream instream = new EvaluateControversyDetection().getClass().getResourceAsStream( "url_rating" );
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String line = reader.readLine(); // skip the first line in the file
		while ( ( line = reader.readLine() ) != null ) {
			String[] splits = line.split( "\t" );
			String url = splits[3].toLowerCase();
			double rating = Double.parseDouble( splits[5] );
			if ( !url.startsWith( "http://en.wikipedia.org" ) ) {
				if ( !url_ratings.containsKey( url ) ) {
					url_ratings.put( url, new ArrayList<Double>() );
				}
				url_ratings.get( url ).add( rating );
			}
		}
		reader.close();
		instream.close();
		
		Map<String, Boolean> groundtruth = new TreeMap<String, Boolean>();
		for ( String url : url_ratings.keySet() ) {
			boolean controversy = false;
			if ( StatUtils.mean( url_ratings.get( url ) ) < 2.5 ) {
				controversy = true;
			}
			groundtruth.put( url, controversy );
		}
		
		return groundtruth;
		
	}
	
	/**
	 * Evaluate using xval, optimized for the specified metric.
	 * 
	 * @param groundtruth
	 * @param file
	 * @param numfolds
	 * @param optmetric
	 * @throws IOException
	 */
	private static void evaluate( Map<String, Boolean> groundtruth, File file, int numfolds, String optmetric ) throws IOException {
		
		Map<String, Double> url_scores = new TreeMap<>();
		List<String> all_urls = new ArrayList<>();
		
		BufferedReader reader = IOUtils.getBufferedReader( file );
		String line = reader.readLine();
		while ( line != null ) {
			String[] splits = line.split( "\t" );
			String url = splits[0];
			double score = Double.parseDouble( splits[2] );
			all_urls.add( url );
			url_scores.put( url, score );
			line = reader.readLine();
		}
		reader.close();
		
		double[] metrics = new double[7];
		
		for ( int foldid = 0 ; foldid < numfolds ; foldid++ ) {
			
			List<String> train = new ArrayList<String>();
			List<String> test = new ArrayList<String>();
			
			for ( int ix = 0 ; ix < all_urls.size() ; ix++ ) {
				if ( ix % numfolds == foldid ) {
					test.add( all_urls.get( ix ) );
				} else {
					train.add( all_urls.get( ix ) );
				}
			}
			
			double max_cutoff = find_cutoff( train, groundtruth, url_scores, optmetric );
			
			{
				// test
				
				double TP = 0;
				double TN = 0;
				double FP = 0;
				double FN = 0;
				
				for ( String url : test ) {
					
					boolean C_true = groundtruth.get( url );
					boolean C_pred = url_scores.get( url ) > max_cutoff;
					
					if ( C_true ) {
						if ( C_pred ) {
							TP++;
						} else {
							FN++;
						}
					} else {
						if ( C_pred ) {
							FP++;
						} else {
							TN++;
						}
					}
					
				}
				
				double accuracy = ( TP + TN ) / ( TP + TN + FP + FN );
				double P_prec = TP / ( TP + FP );
				double P_rec = TP / ( TP + FN );
				double P_F1 = ( P_prec * P_rec ) == 0 ? 0 : ( 2 * P_prec * P_rec / ( P_prec + P_rec ) );
				double N_prec = TN / ( TN + FN );
				double N_rec = TN / ( TN + FP );
				double N_F1 = ( N_prec * N_rec ) == 0 ? 0 : ( 2 * N_prec * N_rec / ( N_prec + N_rec ) );
				
				metrics[0] += accuracy;
				metrics[1] += P_F1;
				metrics[2] += P_prec;
				metrics[3] += P_rec;
				metrics[4] += N_F1;
				metrics[5] += N_prec;
				metrics[6] += N_rec;
				
			}
			
		}
		
		for ( int ix = 0 ; ix < metrics.length ; ix++ ) {
			metrics[ix] = metrics[ix] / numfolds;
		}
		
		System.out.println( file.getName().replace( "_", "\t" ) + "\t" + find_cutoff( all_urls, groundtruth, url_scores, optmetric ) + "\t" + metrics[0] + "\t" + metrics[1] + "\t" + metrics[2] + "\t" + metrics[3] + "\t" + metrics[4] + "\t" + metrics[5] + "\t" + metrics[6] );
		
	}
	
	private static double find_cutoff( List<String> instances, Map<String, Boolean> groundtruth, Map<String, Double> url_scores, String optmetric ) {
		
		double max_cutoff = 0;
		
		double TP = 0;
		double TN = 0;
		double FP = 0;
		double FN = 0;
		
		for ( String url : instances ) {
			if ( groundtruth.get( url ) ) {
				TP++;
			} else {
				FP++;
			}
		}
		
		Collections.sort( instances, new Comparator<String>() {
			public int compare( String url1, String url2 ) {
				return url_scores.get( url1 ).compareTo( url_scores.get( url2 ) );
			}
		} );
		
		double max_metric = 0;
		
		for ( int ix = 0 ; ix < instances.size() ; ix++ ) {
			
			String url = instances.get( ix );
			double cutoff = url_scores.get( url );
			if ( groundtruth.get( url ) ) {
				TP--;
				FN++;
			} else {
				FP--;
				TN++;
			}
			
			double accuracy = ( TP + TN ) / ( TP + TN + FP + FN );
			double P_precion = TP / ( TP + FP );
			double P_recall = TP / ( TP + FN );
			double N_precion = TN / ( TN + FN );
			double N_recall = TN / ( TN + FP );
			double P_F1 = ( P_precion * P_recall ) == 0 ? 0 : ( 2 * P_precion * P_recall / ( P_precion + P_recall ) );
			double N_F1 = ( N_precion * N_recall ) == 0 ? 0 : ( 2 * N_precion * N_recall / ( N_precion + N_recall ) );
			
			double metric = 0;
			if ( optmetric.equalsIgnoreCase( "accuracy" ) ) {
				metric = accuracy;
			} else if ( optmetric.equalsIgnoreCase( "P_prec" ) ) {
				metric = P_precion;
			} else if ( optmetric.equalsIgnoreCase( "P_rec" ) ) {
				metric = P_recall;
			} else if ( optmetric.equalsIgnoreCase( "N_prec" ) ) {
				metric = N_precion;
			} else if ( optmetric.equalsIgnoreCase( "N_rec" ) ) {
				metric = N_recall;
			} else if ( optmetric.equalsIgnoreCase( "P_F1" ) ) {
				metric = P_F1;
			} else if ( optmetric.equalsIgnoreCase( "N_F1" ) ) {
				metric = N_F1;
			}
			
			if ( metric > max_metric ) {
				max_metric = metric;
				max_cutoff = cutoff;
			}
			
		}
		
		return max_cutoff;
		
	}
	
}
