package test.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import org.apache.commons.math3.stat.StatUtils;

import utils.Sampling;


public class TestSampling {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testWReplacement() {
		
		double[] data = new double[1024*1024];
		for(int i=0;i<data.length;i++){
			data[i] = i;
		}
		double mean_population = StatUtils.mean(data);
		double var_population = StatUtils.variance(data);
		
		for(int loop=0;loop<100;loop++){
			double[] sample = Sampling.wReplacement(data, 512*1024);
			double mean_sample = StatUtils.mean(sample);
			double var_sample = StatUtils.variance(sample);
			Assert.assertEquals( mean_population, mean_sample, 0.01*mean_population );
			Assert.assertEquals( var_population, var_sample, 0.01*var_population );
		}
		
	}
	
	@Test
	public void testWoReplacement() {
		
		double[] data = new double[1024*1024];
		for(int i=0;i<data.length;i++){
			data[i] = i;
		}
		double mean_population = StatUtils.mean(data);
		double var_population = StatUtils.variance(data);
		
		for(int loop=0;loop<100;loop++){
			double[] sample = Sampling.woReplacement(data, 512*1024);
			double mean_sample = StatUtils.mean(sample);
			double var_sample = StatUtils.variance(sample);
			Assert.assertEquals( mean_population, mean_sample, 0.01*mean_population );
			Assert.assertEquals( var_population, var_sample, 0.01*var_population );
		}
		
	}
	
}
