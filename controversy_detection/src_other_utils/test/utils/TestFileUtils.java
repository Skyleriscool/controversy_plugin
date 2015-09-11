package test.utils;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import utils.FileUtils;
import utils.FileUtils.FileProcessor;


public class TestFileUtils {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void test() throws IOException {
		File dir = new File("/home/jiepu/afsdfdsi/iir9/jjfds/9mnv/9032");
		Assert.assertFalse( dir.exists() );
		FileUtils.mkdir(dir);
		Assert.assertTrue( dir.exists() );
		FileUtils.process( dir, new FileProcessor(){
			public void process(File f) throws IOException {
				System.out.println(f.getAbsolutePath());
			}
		});
		FileUtils.rm(dir);
		Assert.assertFalse( dir.exists() );
	}
	
}
