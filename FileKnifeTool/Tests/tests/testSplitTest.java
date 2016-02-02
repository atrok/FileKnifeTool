package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import logprocessing.StatisticManager;
import util.Benchmark;

public class testSplitTest {
	
	Logger logger=LoggerFactory.getLogger(testSplitTest.class);
	
	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");
	private static final Splitter MY_SPLITTER = Splitter.on(' ')
		       .trimResults()
		       .omitEmptyStrings();
	
	@Test
	public void testFile(){
		try {
			char space=new Character(' ');
			Benchmark.tick();
			
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->splitTest(space,s));
			
			Benchmark.tock("handmade split");
			
			Benchmark.tick();
			
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->s.split("\\s+"));
			
			Benchmark.tock("original split");
			
			Benchmark.tick();
			
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->Iterables.toArray(MY_SPLITTER.split(s),String.class));
			
			Benchmark.tock("Guava split");

			Benchmark.tick();
			
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->MY_SPLITTER.splitToList(s));
			
			Benchmark.tock("Guava split to List");
			
			Benchmark.tick();
			
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->StringUtils.split(s));
			
			Benchmark.tock("Commons StringUtil split");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Ignore
	public void test() {
		String s = "2015-09-18T09:56:03.216 Trc 24301 Extended info : Client [2068] disconnected, application [Inbox_Uniclass_Prod], type [InteractionWorkspace], protocol [CFGLIB]";
		
		Benchmark.tick();
				
		String[] arr=splitTest(new Character(' '),s);
		Benchmark.tock("Alternate split");
		logger.info("{}",Arrays.toString(arr));
		Benchmark.tick();
		
		arr=s.split("\\s+");
		Benchmark.tock("Default split");
		logger.info("{}",Arrays.toString(arr));
		
	}
	
	
	
	private String[] splitTest(char splitchar, String s){
		char[] c = s.toCharArray();
		List<String> ll = new ArrayList<String>();
		int index = 0;

		for(int i=0;i<c.length;i++) {
		    if(c[i] == splitchar ||(i==c.length-1)) {
		    	String t=s.substring(index,i);
		    	if (!t.equals(splitchar)){
		    			ll.add(s.substring(index,i));
		    			index = i+1;
		    	}
		    }
		}

		//String[] arr = ll.toArray(new String[0]);
		return ll.toArray(new String[0]);

		
	}
}
