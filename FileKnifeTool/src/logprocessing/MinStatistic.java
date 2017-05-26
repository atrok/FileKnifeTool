package logprocessing;

	import java.util.HashMap;
	import java.util.Map;
	import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinStatistic extends AggregatingStatistic { 


	

		private Double counter=0.0;
		private Map stats=new HashMap<String, HashMap>();
		

		private int aggregating_field;
		
		Pattern patternPunct=Pattern.compile(REGEXP.PUNCT);
		
		private Logger logger=LoggerFactory.getLogger(MinStatistic.class);

		public MinStatistic(String regexp,String name, String aggregating_field){
			super(regexp,name, aggregating_field);
			this.aggregating_field=Integer.valueOf(aggregating_field);
			
		}
		
	 

		public MinStatistic(String regexp, String name, int i) {
			// TODO Auto-generated constructor stub
			super(regexp,name,i);
			this.aggregating_field=i;
		}


		public MinStatistic(String name,Map<String,String> param){
			super(name, param);
			this.aggregating_field=Integer.valueOf(param.get(StatisticParamNaming.FIELD.toString()));
			
		}
		
		
		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe){
			super.calculate(line, splitline, sampled_timeframe);
			
			double new_value=toNumberFormat(value);
				counter=getStatValue(line, splitline, rowname);
				if (null!=counter)
					if (counter>new_value || counter==0)
					updateStatValue(new_value,rowname);
			
		}


	
}
