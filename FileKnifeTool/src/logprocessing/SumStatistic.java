package logprocessing;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumStatistic extends AggregatingStatistic { 


	

		private Double counter=0.0;
		//private String regexp="";
		private Map stats=new HashMap<String, HashMap>();
		//private String statname="";
		
	/*	public IncrementalStatistic(String regexp,String name, String field){
			super(regexp,name, field);
			
		}
	*/	
		private int aggregating_field;
		
		Pattern patternPunct=Pattern.compile(REGEXP.PUNCT);
		
		private Logger logger=LoggerFactory.getLogger(MinStatistic.class);
		
		/*
		public SumStatistic(String regexp,String name, String aggregating_field){
			super(regexp,name);
			this.aggregating_field=Integer.valueOf(aggregating_field);
			
		}
		
	 

		public SumStatistic(String regexp, String name, int i) {
			// TODO Auto-generated constructor stub
			super(regexp,name);
			this.aggregating_field=i;
		}

*/
		public SumStatistic(String name,Map<String,String> param){
			super(name, param);
			this.aggregating_field=Integer.valueOf(param.get(StatisticParamNaming.FIELD.toString()));
			
		}
		
		
		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe){
			super.calculate(line, splitline, sampled_timeframe);
			
			try{
			double new_value;
			
			new_value=toNumberFormat(value);
				counter=getStatValue(line, splitline, rowname);
				if (null!=counter)
					
					updateStatValue(new_value+counter,rowname);
			}catch(Exception exc){
				logger.error("Can't calculate statistic value, statistic:"+toString(), exc);
				throw exc;
			}
			
		}



	
}
