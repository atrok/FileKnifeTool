package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import garbagecleaner.ENUMERATIONS;
import logprocessing.LineProcessingLogs;
import statmanager.StatisticManager;
import statmanager.UnsupportedStatFormatException;
import util.Benchmark;


public class TestStreams {

	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");// "Tests/resources/cs85.20151223_145909_388.log");
	
	static Collection<String> linePatterns = new ArrayList<String>(Arrays.asList("Local time:       2014-09-16T13:14:58.465",
			"13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
			"13:16:54.215 Trc 04541 Message MSGCFG_GETOBJECTINFO received from 224 (CCView 'CCPulse_701')"
)
	);
	
	@Test
	public void testLine() {
		LineProcessingLogs ln;
		try {

			loop(new Serial());

			loop(new Parallel());

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	

	private void loop(test t){
		
		List result=new ArrayList();
		for (int i=0;i<10;i++){
		Benchmark.tick();
			t.run();
		result.add(Benchmark.tack()/ 1000000);
		
		}
		
		double avg=result
		.stream()
		.mapToLong(p->(long)p)
		.average()
		.getAsDouble();
		
		long min=result
				.stream()
				.mapToLong(p->(long)p)
				.min()
				.getAsLong();
	
		long max=result
				.stream()
				.mapToLong(p->(long)p)
				.max()
				.getAsLong();
				
		System.out.println("AVG:"+avg+", MIN:"+min+", MAX:"+max);
	}
	
	private interface test{
		
		public void run();
	}
	
	private class Serial implements test{
		
		public void run(){
			
			try {
				List<Line> l=Files.lines(file, StandardCharsets.ISO_8859_1)
						.map(p->new Line(p))
						.collect(Collectors.toList());
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private class Parallel implements test{
		
		public void run(){
			
			try {
				List<Line> l=Files.lines(file, StandardCharsets.ISO_8859_1)
						.parallel()
						.map(p->new Line(p))
						.collect(Collectors.toList());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private class LogProcessTest{
		
		public Line processLine(String str){
			
			return new Line(str);
		}
		
	}
	
	private class Line{
		private String[] str;
		public Line(String line){
			str=StringUtils.split(line);
		}
		
		public int size(){
			return str.length;
		}
		
		public String[] getArray(){
			return str;
		}
	}

	class Averager implements IntConsumer
	{
	    private int total = 0;
	    private int count = 0;
	        
	    public double average() {
	        return count > 0 ? ((double) total)/count : 0;
	    }
	        
	    public void accept(int i) { total += i; count++; }
	    public void combine(Averager other) {
	        total += other.total;
	        count += other.count;
	    }
	}

}
