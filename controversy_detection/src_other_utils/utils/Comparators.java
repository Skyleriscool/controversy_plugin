package utils;

import java.io.*;
import java.util.*;

/**
 * Frequently used comparators.
 * 
 * @author Jiepu Jiang
 * @version Feb 28, 2013
 */
public class Comparators {
	
	/** Rank integers in ascending order. */
	public static Comparator<Integer> IntegerAsc = new Comparator<Integer>() {
		public int compare(Integer i1, Integer i2) {
			return i1.compareTo(i2);
		}
	};
	
	/** Rank integers in descending order. */
	public static Comparator<Integer> IntegerDesc = new Comparator<Integer>() {
		public int compare(Integer i1, Integer i2) {
			return i2.compareTo(i1);
		}
	};
	
	/** Rank long integers in ascending order. */
	public static Comparator<Long> LongAsc = new Comparator<Long>() {
		public int compare(Long l1, Long l2) {
			return l1.compareTo(l2);
		}
	};
	
	/** Rank long integers in descending order. */
	public static Comparator<Long> LongDesc = new Comparator<Long>() {
		public int compare(Long l1, Long l2) {
			return l2.compareTo(l1);
		}
	};
	
	/** Rank floats in ascending order. */
	public static Comparator<Float> FloatAsc = new Comparator<Float>() {
		public int compare(Float f1, Float f2) {
			return f1.compareTo(f2);
		}
	};
	
	/** Rank floats in descending order. */
	public static Comparator<Float> FloatDesc = new Comparator<Float>() {
		public int compare(Float f1, Float f2) {
			return f2.compareTo(f1);
		}
	};
	
	/** Rank doubles in ascending order. */
	public static Comparator<Double> DoubleAsc = new Comparator<Double>() {
		public int compare(Double d1, Double d2) {
			return d1.compareTo(d2);
		}
	};
	
	/** Rank doubles in descending order. */
	public static Comparator<Double> DoubleDesc = new Comparator<Double>() {
		public int compare(Double d1, Double d2) {
			return d2.compareTo(d1);
		}
	};
	
	/** Rank Strings in ascending order. */
	public static Comparator<String> StringAsc = new Comparator<String>() {
		public int compare(String s1, String s2) {
			return s1.compareTo(s2);
		}
	};
	
	/** Rank Strings by descending order. */
	public static Comparator<String> StringDesc = new Comparator<String>() {
		public int compare(String s2, String s1) {
			return s2.compareTo(s1);
		}
	};
	
	/** Rank number Strings (e.g. integers and floats) by the actual numbers in ascending order. */
	public static Comparator<String> StringAsNumberAsc = new Comparator<String>() {
		public int compare(String s1, String s2) {
			Double val1 = Double.parseDouble(s1);
			Double val2 = Double.parseDouble(s2);
			return val1.compareTo(val2);
		}
	};
	
	/** Rank number Strings (e.g. integers and floats) by the actual numbers in ascending order. */
	public static Comparator<String> StringAsNumberDesc = new Comparator<String>() {
		public int compare(String s2, String s1) {
			Double val1 = Double.parseDouble(s1);
			Double val2 = Double.parseDouble(s2);
			return val2.compareTo(val1);
		}
	};
	
	/** Rank files by files' names in ascending order */
	public static Comparator<File> FileNameAsc = new Comparator<File>() {
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
	};
	
	/** Rank files by files' names in descending order */
	public static Comparator<File> FileNameDesc = new Comparator<File>() {
		public int compare(File f1, File f2) {
			return f2.getName().compareTo(f1.getName());
		}
	};
	
}
