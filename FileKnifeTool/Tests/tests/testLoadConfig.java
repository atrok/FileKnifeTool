package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;

import cmdline.CommandParse;
import util.FilesUtil;
import java.net.URLClassLoader;

public class testLoadConfig {

	@Test
	public void testLoadConfig() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			
			ClassLoader cl = ClassLoader.getSystemClassLoader();

			URL[] urls = ((URLClassLoader)cl).getURLs();

				        for(URL url: urls){
				        	System.out.println(url.getFile());
				        }
			
			
			
			String filename = "statistic.properties";
			input=getClass().getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
				return;
			}
			System.out.println(Arrays.toString(FilesUtil.read(input)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
