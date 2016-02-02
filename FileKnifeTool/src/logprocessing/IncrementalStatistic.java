package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IncrementalStatistic extends StatisticDefinition {

	private Integer counter=0;
	//private String regexp="";
	private Map stats=new HashMap<String, HashMap>();
	//private String statname="";
	
/*	public IncrementalStatistic(String regexp,String name, String field){
		super(regexp,name, field);
		
	}
*/	
	public IncrementalStatistic(String regexp,String name){
		super(regexp,name);
		
	}

	public IncrementalStatistic(String name,Map<String,String> param){
		super(name, param);
		
	}
	
	
	@Override
	public void calculate(String line, String[] splitline, String sampled_timeframe) {
		
		counter=getStatValue(line, splitline, sampled_timeframe);
		if (null!=counter)
			updateStatValue(counter+1,sampled_timeframe);
	}
}
