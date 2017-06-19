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
	
	protected String rowname;
	
	@Override
	public void calculate(String line, String[] splitline, String sampled_timeframe) {
		//String row;
		
		String[] regexgroups=getRegexGroups();
		
		if(useFilename)
			rowname=filename=splitline[splitline.length-1];
		else
			if(useGroupForRowname){
				if(regexgroups!=null&&regexgroups.length>1)
					rowname=regexgroups[rowname_group_index];
				else
					rowname=splitline[rowname_group_index];
			}else
				rowname=sampled_timeframe;
		
		counter=getStatValue(line, splitline, rowname);
		if (null!=counter)
			updateStatValue(counter+1,rowname);
	}
}
