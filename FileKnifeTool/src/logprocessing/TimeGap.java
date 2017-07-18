package logprocessing;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logprocessing.StatisticDefinition.Block;
import logprocessing.StatisticDefinition.Line;

public class TimeGap extends DurationStatistic{

	public TimeGap(String name, Map<String, String> param) {
		super( ".*", name,-1);
		
		// TODO Auto-generated constructor stub
	}
	
	private Logger logger = LoggerFactory.getLogger(TimeGap.class);
	
	private String previous_timeframe="";
	
	public void calculate(String line, String[] splitline, String sampled_timeframe) { // LineProcessing
		long duration=0;
		Double counter=null;

		if (sampled_timeframe!=null){
			
		
		Line l = new Line(splitline[0]);
		l.setTime(sampled_timeframe);
		//adding new timestamp to the stats;

		Block block = (Block) stats.get(sampled_timeframe);
		if (block==null)
		block = new Block(sampled_timeframe);
		block.addLine(l);
	
		stats.put(sampled_timeframe, block);

		// retrieve previous timestamp to calculate timegap
		
		Block block2 = (Block) stats.get(previous_timeframe);
		
		
		try {
			 if (null!=block2){
				 block2.addLine(l);
				 duration = block2.getDuration();
				 counter = getStatValue(line, splitline, block2.id);

			 }
			 else duration=0;
			 
				 if (null!=counter) {
						double new_value = duration;
						// if (counter < new_value)
						updateStatValue(duration, block2.id);
						
					}
			 
		} catch (Exception exc) {
			logger.error(
					"Can't update duration,\nline: " + line + "\nsampled_timeframe: " + sampled_timeframe + "{}",
					exc.getMessage());
			throw exc;

		} finally {
			
			previous_timeframe=sampled_timeframe;
		}
		}
		
	}
}
