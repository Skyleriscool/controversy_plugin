package test.utils;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;

import utils.IOUtils;


public class TestIOUtils {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testReadBytes() throws IOException {
		
		byte[] expected = new byte[64*1024*1024];
		for(int ix=0;ix<expected.length;ix++){
			if(ix%(1024*1024)==0){
				System.out.println(ix/(1024*1024));
			}
			expected[ix] = (byte)ix;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(expected);
		byte[] actual = out.toByteArray();
		out.close();
		
		System.out.println(" >> finished output");
		
		Assert.assertArrayEquals( expected, actual );
		
		ByteArrayInputStream in = new ByteArrayInputStream(expected);
		actual = IOUtils.readBytes(in);
		in.close();
		
		System.out.println(" >> finished reading");
		
		Assert.assertArrayEquals( expected, actual );
		
	}
	
	
	
}
