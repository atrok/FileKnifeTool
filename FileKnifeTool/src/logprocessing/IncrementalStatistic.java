package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IncrementalStatistic extends StatisticDefinition {

	private Double counter=0.0;
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
		String row;
		if (useFilename)
			row=splitline[splitline.length-1]; // filename in last cell
		else
			row=sampled_timeframe;
		
		counter=getStatValue(line, splitline, row);
		if (null!=counter)
			updateStatValue(counter+1,row);
	}
}
