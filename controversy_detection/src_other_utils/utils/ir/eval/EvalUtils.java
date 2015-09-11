package utils.ir.eval;

import java.util.*;

/**
 * Utilities for evaluation, such as cross-validation.
 * 
 * @author Jiepu Jiang
 * @date Sep 4, 2013
 */
public class EvalUtils {
	
	public static Map<String, double[]> eval_avg_instances( Map<String, Map<String, double[]>> para_instance_evals, Set<String> target_instances ) {
		Map<String, double[]> para_avgs = new TreeMap<String, double[]>();
		for ( String para : para_instance_evals.keySet() ) {
			double[] avg = new double[para_instance_evals.values().iterator().next().values().iterator().next().length];
			double count = 0;
			for ( String instance : para_instance_evals.get( para ).keySet() ) {
				if ( target_instances == null || target_instances.contains( instance ) ) {
					for ( int ix = 0 ; ix < avg.length ; ix++ ) {
						avg[ix] += para_instance_evals.get( para ).get( instance )[ix];
					}
				}
				count++;
			}
			for ( int ix = 0 ; ix < avg.length ; ix++ ) {
				avg[ix] = avg[ix] / count;
			}
			para_avgs.put( para, avg );
		}
		return para_avgs;
	}
	
	/**
	 * Generate x-fold cross validation evaluation results (each fold's results were evaluated according to the tuned best parameter from other folds).
	 * para_evals stores all parameter values' evaluation results on each instances. Multiple evaluation metrics' results (double[]) can be provided. It will be
	 * optimized according to the first metric and then the second and so on.
	 * 
	 * @param para_instance_evals
	 *            a map storing all parameter's evaluation values on each evaluation instances, i.e. a map of para-instance-value.
	 * @param x
	 *            x for x-fold cross validation.
	 * @return
	 */
	public static Map<String, double[]> eval_xvalidation( Map<String, Map<String, double[]>> para_instance_evals, int x ) {
		
		int num_eval_values = 0;
		Set<String> all_instances = new TreeSet<String>();
		
		for ( String para : para_instance_evals.keySet() ) {
			for ( String instance : para_instance_evals.get( para ).keySet() ) {
				all_instances.add( instance );
				num_eval_values = para_instance_evals.get( para ).get( instance ).length;
			}
		}
		
		Map<String, Integer> instances_fold = new TreeMap<String, Integer>();
		Map<Integer, Set<String>> fold_instances = new TreeMap<Integer, Set<String>>();
		
		{
			int count = 0;
			for ( String instance : all_instances ) {
				int fold = count % x;
				instances_fold.put( instance, fold );
				if ( !fold_instances.containsKey( fold ) ) {
					fold_instances.put( fold, new TreeSet<String>() );
				}
				fold_instances.get( fold ).add( instance );
				count++;
			}
		}
		
		Map<String, double[]> xval_results = new TreeMap<String, double[]>();
		
		for ( int fold : fold_instances.keySet() ) {
			
			String best_parameter = null;
			double[] best_parameter_evals = new double[num_eval_values];
			
			for ( String para : para_instance_evals.keySet() ) {
				// calculate the parameter value's eval value for all other folds
				int num_instances = 0;
				double[] sumed_eval_value = new double[num_eval_values];
				for ( int otherfold : fold_instances.keySet() ) {
					if ( otherfold != fold ) {
						for ( String instance : fold_instances.get( otherfold ) ) {
							double[] eval_value = new double[num_eval_values];
							if ( para_instance_evals.containsKey( para ) && para_instance_evals.get( para ).containsKey( instance ) ) {
								eval_value = para_instance_evals.get( para ).get( instance );
							}
							for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
								sumed_eval_value[ix] = sumed_eval_value[ix] + eval_value[ix];
							}
							num_instances++;
						}
					}
				}
				for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
					sumed_eval_value[ix] = sumed_eval_value[ix] / num_instances;
				}
				boolean replace = false;
				for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
					if ( sumed_eval_value[ix] > best_parameter_evals[ix] ) {
						replace = true;
						break;
					} else if ( sumed_eval_value[ix] < best_parameter_evals[ix] ) {
						replace = false;
						break;
					} else {
						// sumed_eval_value[ix] == best_parameter_evals[ix]
						// go to the next loop and choose alternative metrics
					}
				}
				if ( replace ) {
					best_parameter = para;
					best_parameter_evals = sumed_eval_value;
				}
			}
			
			// generate the target fold's eval values using the best_parameter on other folds
			for ( String instance : fold_instances.get( fold ) ) {
				double[] evals = new double[num_eval_values];
				if ( para_instance_evals.get( best_parameter ).containsKey( instance ) ) {
					evals = para_instance_evals.get( best_parameter ).get( instance );
				}
				xval_results.put( instance, evals );
			}
			
		}
		
		return xval_results;
		
	}
	
	/**
	 * Select the best parameter and evaluate results. The returned map contains only one entry: the best parameter and the corresponding results on each
	 * instances.
	 * 
	 * @param para_instance_evals
	 * @param x
	 * @return
	 */
	public static Map<String, Map<String, double[]>> eval_best_parameter( Map<String, Map<String, double[]>> para_instance_evals ) {
		
		int num_eval_values = 0;
		for ( String para : para_instance_evals.keySet() ) {
			for ( String instance : para_instance_evals.get( para ).keySet() ) {
				num_eval_values = para_instance_evals.get( para ).get( instance ).length;
			}
		}
		
		String best_parameter = null;
		double[] best_parameter_evals = new double[num_eval_values];
		
		for ( String para : para_instance_evals.keySet() ) {
			int num_instances = 0;
			double[] sumed_eval_value = new double[num_eval_values];
			for ( String instance : para_instance_evals.get( para ).keySet() ) {
				double[] eval_value = new double[num_eval_values];
				if ( para_instance_evals.containsKey( para ) && para_instance_evals.get( para ).containsKey( instance ) ) {
					eval_value = para_instance_evals.get( para ).get( instance );
				}
				for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
					sumed_eval_value[ix] = sumed_eval_value[ix] + eval_value[ix];
				}
				num_instances++;
			}
			for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
				sumed_eval_value[ix] = sumed_eval_value[ix] / num_instances;
			}
			boolean replace = false;
			for ( int ix = 0 ; ix < num_eval_values ; ix++ ) {
				if ( sumed_eval_value[ix] > best_parameter_evals[ix] ) {
					replace = true;
					break;
				} else if ( sumed_eval_value[ix] < best_parameter_evals[ix] ) {
					replace = false;
					break;
				} else {
					// sumed_eval_value[ix]==best_parameter_evals[ix]
					// go to next loop and choose alternative metric for selecting parameters
				}
			}
			if ( replace ) {
				best_parameter = para;
				best_parameter_evals = sumed_eval_value;
			}
		}
		
		Map<String, Map<String, double[]>> best = new TreeMap<String, Map<String, double[]>>();
		best.put( best_parameter, para_instance_evals.get( best_parameter ) );
		
		return best;
		
	}
	
}
