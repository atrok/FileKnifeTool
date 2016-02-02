package logprocessing;

import java.util.HashMap;
import java.util.Map;

public class AggregatingStatistic extends StatisticDefinition {

	
		private Integer counter=0;
		//private String regexp="";
		private Map stats=new HashMap<String, HashMap>();
		//private String statname="";
		private int aggregating_field;
		
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
			this.aggregating_field=Integer.valueOf(param.get(StatisticParamNaming.FIELD));
			
		}

		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe) {
			
			counter=getStatValue(line, splitline, sampled_timeframe);
			if (null!=counter)
				updateStatValue(Integer.valueOf(splitline[aggregating_field]),sampled_timeframe);
		}
	}

