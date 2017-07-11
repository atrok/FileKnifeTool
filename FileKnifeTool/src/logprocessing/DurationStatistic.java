package logprocessing;

	import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

import garbagecleaner.ENUMERATIONS;
import util.FilesUtil;

public class DurationStatistic extends StatisticDefinition { 


	

		private Double counter=0.0;
		//private String regexp="";
		private Map stats=new HashMap<String, Block>();
		
		//private String statname="";
		
		private Block block;
		
	/*	public IncrementalStatistic(String regexp,String name, String field){
			super(regexp,name, field);
			
		}
	*/	
		private int aggregating_field;
		
		Pattern patternPunct=Pattern.compile(REGEXP.PUNCT);
		
		private Logger logger=LoggerFactory.getLogger(DurationStatistic.class);
		
		
		public DurationStatistic(String regexp,String name, String aggregating_field){
			super(regexp,name);
			this.aggregating_field=Integer.valueOf(aggregating_field);
			
		}
		
	 

		public DurationStatistic(String regexp, String name, int i) {
			// TODO Auto-generated constructor stub
			super(regexp,name);
			this.aggregating_field=i;
		}


		public DurationStatistic(String name,Map<String,String> param){
			super(name, param);
			String f=param.get(StatisticParamNaming.FIELD.toString());
			String r=param.get(StatisticParamNaming.ROWNAME.toString());
			if (null==f&&null==r){
				throw new ParameterException("DurationStatistic require either 'field=<0-9>' or 'rowname=(filename|<any string>)' properties configured.");
			}
			
			if(FilesUtil.isNumeric(f))
				this.aggregating_field=Integer.valueOf(f);
			
		}
		
		
		@Override
		public void calculate(String line, String[] splitline, String sampled_timeframe){ // LineProcessing sampling should be 0 to get not sampled timestamp 
			
		String[] regexgroups = getRegexGroups();

		if (null != sampled_timeframe) {
			long duration = 0;
			String value;
			if (useFilename)
				value = splitline[splitline.length - 1]; // filename in last
			else
				if(regexgroups!=null&&regexgroups.length>1)
					value=regexgroups[aggregating_field];
				else 
					value=splitline[aggregating_field];
			
			block = (Block) stats.get(value);

			if (block == null)
				block = new Block(value);

			Line l = new Line(splitline);
			l.setTime(sampled_timeframe);
			block.addLine(l);

			stats.put(value, block);

			try {
				//if (block.getSize() > 1)
					duration = block.getDuration();
				//else duration=-1;
			} catch (Exception exc) {
				logger.error(
						"Can't update duration,\nline: " + line + "\nsampled_timeframe: " + sampled_timeframe + "{}",
						exc.getMessage());
				throw exc;

			} finally {
				counter = getStatValue(line, splitline, block.id);
				if (null != counter) {
					double new_value = duration;
					//if (counter < new_value)
						updateStatValue(duration, block.id);
				}
			}
		}
		}


		public Map<String,Map> getStatistics(){
			
			TreeMap<String,Map> rate=(TreeMap)super.getStatistics();
			


			Map timeStart=new HashMap<String,String>();
			Map timeEnd=new HashMap<String,String>();
			
			Set<String> keys=stats.keySet();
			
			if (stats.size()>0){
				for(String blockID: keys){
					
					Block block=(Block)stats.get(blockID);
					
					timeStart.put(blockID, block.getLine(0).time);
					timeEnd.put(blockID, block.getLine(block.getSize()-1).time);
					
				}
				rate.put("time_start",timeStart);
				rate.put("time_end",timeEnd);
				
			}
			
			return rate;
		}
		
}
