package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregatingStatistic extends StatisticDefinition {

	
		private Double counter=0.0;
		//private String regexp="";
		private Map stats=new HashMap<String, HashMap>();
		//private String statname="";
		private int aggregating_field;
		
		Pattern patternPunct=Pattern.compile(REGEXP.PUNCT);
		
		private Logger logger=LoggerFactory.getLogger(AggregatingStatistic.class);
		
		public AggregatingStatistic(String regexp,String name, String aggregating_field){
			super(regexp,name);
			this.aggregating_field=Integer.valueOf(aggregating_field);
			
		}
		
	 

		public AggregatingStatistic(String regexp, String name, int i) {
			// TODO Auto-generated constructor stub
			super(regexp,name);
			this.aggregating_field=i;
		}


		public AggregatingStatistic(String name,Map<String,String> param){
			super(name, param);
			this.aggregating_field=Integer.valueOf(param.get(StatisticParamNaming.FIELD.toString()));
			
		}

		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe){
			
			
			String value=splitline[aggregating_field];
			
				counter=getStatValue(line, splitline, sampled_timeframe);
				if (null!=counter)
					updateStatValue(toNumberFormat(value),sampled_timeframe);
			
		}
		

	}

