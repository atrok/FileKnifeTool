package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

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
			
			String f=param.get(StatisticParamNaming.FIELD.toString());
			if (null==f){
				throw new ParameterException("AggregationStatistic type require 'field' property configured. Expected parameters: [0-9]");
			}
			
		}
		protected String value;
		protected String rowname;
		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe){
			
			String[] regexgroups=getRegexGroups();
			
			if(regexgroups!=null&&regexgroups.length>1)
				value=regexgroups[aggregating_field];
			else 
				value=splitline[aggregating_field];
			
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
			
		}
		
		public String toString(){
			
			return super.toString()+"\nfield\t"+aggregating_field;
		}
		
	}

