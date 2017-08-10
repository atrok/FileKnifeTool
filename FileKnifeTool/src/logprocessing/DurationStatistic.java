package logprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

import garbagecleaner.ENUMERATIONS;
import jregex.Matcher;
import util.FilesUtil;
import util.Generator;

public class DurationStatistic extends StatisticDefinition {

	private Double counter = 0.0;
	// private String regexp="";
	protected Map stats = new HashMap<String, Block>();

	// private String statname="";

	private Block block;

	/*
	 * public IncrementalStatistic(String regexp,String name, String field){
	 * super(regexp,name, field);
	 * 
	 * }
	 */
	private int aggregating_field;

	Pattern patternPunct = Pattern.compile(REGEXP.PUNCT);

	private Logger logger = LoggerFactory.getLogger(DurationStatistic.class);

	public DurationStatistic(String regexp, String name, String aggregating_field) {
		super(regexp, name);
		this.aggregating_field = Integer.valueOf(aggregating_field);

	}

	public DurationStatistic(String regexp, String name, int i) {
		// TODO Auto-generated constructor stub
		super(regexp, name);
		this.aggregating_field = i;
	}

	public DurationStatistic(String name, Map<String, String> param) {
		super(name, param);
		String f = param.get(StatisticParamNaming.FIELD.toString());
		String r = param.get(StatisticParamNaming.ROWNAME.toString());

		String startwith = param.get(StatisticParamNaming.STARTWITH.toString());
		String endwith = param.get(StatisticParamNaming.ENDWITH.toString());



		if (null == f && null == r) {
			throw new ParameterException(
					"DurationStatistic require either 'field=<0-9>' or 'rowname=(filename|<any string>)' properties configured.");
		}

		if (patternLineMatcher == null & (startwith == null || endwith == null))
			throw new ParameterException(
					"DurationStatistic require either 'regexp' or ('startwith'|'endwith') properties configured.");
		else{
			if (patternLineMatcher == null ){
			startwithLineMatcher = new jregex.Pattern(startwith);
			endwithLineMatcher = new jregex.Pattern(endwith);
			}
		}

		if (FilesUtil.isNumeric(f))
			this.aggregating_field = Integer.valueOf(f);

	}

	Matcher startwithMatcher;
	Matcher endwithMatcher;
	jregex.Pattern startwithLineMatcher;
	jregex.Pattern endwithLineMatcher;

	boolean start = false;
	boolean end = false;
	@Override
	protected boolean isMatched(String line) {

		boolean b = false;

		if(patternLineMatcher!=null){
		jMatcher = patternLineMatcher.matcher(line);


		b = jMatcher.matches();
		}
		else {
			startwithMatcher = startwithLineMatcher.matcher(line);
			endwithMatcher = endwithLineMatcher.matcher(line);

				start = startwithMatcher.matches();
				end = endwithMatcher.matches();
		}
		if (b){
			foundGroups = jMatcher.groups();
			return b;
		}
		if (start){
			foundGroups = startwithMatcher.groups();
			return start;
		}
		if (end){
			foundGroups = endwithMatcher.groups();
			return end;
		}
		return false;

	}
	
	Map <String, String> ids =new HashMap<String, String>();


	@Override
	public void calculate(String line, String[] splitline, String sampled_timeframe) { // LineProcessing
																						// sampling
																						// should
																						// be
																						// 0
																						// to
																						// get
																						// not
																						// sampled
																						// timestamp

		String[] regexgroups = getRegexGroups();

		if (null != sampled_timeframe) {
			long duration = 0;
			String value;
			if (useFilename)
				value = splitline[splitline.length - 1]; // filename in last
			else if (regexgroups != null && regexgroups.length > 1)
				value = regexgroups[aggregating_field];
			else
				value = splitline[aggregating_field];

			
			String uniqueId="";
			
			ids.put(Generator.getID().toString(), value);
			
			Set<String> keys = ids.keySet();
			
			for (String key : keys) {

				String blockId = ids.get(key);
				uniqueId=key;
				if (blockId.equals(value)) {
					block = (Block) stats.get(key);
					if (block!=null){
						if(!block.finished)							
							break;
					}
				}
			}
			
			if (block == null)
				block = new Block(value);
			
			Line l = new Line(splitline);
			l.setTime(sampled_timeframe);
			block.addLine(l);

			if (start)
				block.started=true;
			if (end)
				block.ended=true;
			if(block.started&block.ended){
				block.finished=true;
				if (!ids.isEmpty())
					ids.entrySet().removeIf(e->e.getValue().equals(value));
			}
			start=end=false;
			
			//String idMD5=Generator.getMD5();
			stats.put(uniqueId, block);

			try {
				// if (block.getSize() > 1)
				duration = block.getDuration();
				// else duration=-1;
			} catch (Exception exc) {
				logger.error(
						"Can't update duration,\nline: " + line + "\nsampled_timeframe: " + sampled_timeframe + "{}",
						exc.getMessage());
				throw exc;

			} finally {
				counter = getStatValue(line, splitline, uniqueId);
				if (null != counter) {
					double new_value = duration;
					// if (counter < new_value)
					updateStatValue(duration, uniqueId);
				}
			}
		}
	}

	private Map<String,String> cleanMap(Map map, String value){
		
		try{
		Set<String> keys=map.keySet();
		
		for(String key : keys){
			if (map.get(key).equals(value))
				map.remove(key);
		}
		}catch (Exception exc){
			logger.error("Error!",exc);
		}
		return map;
	}
	public Map<String, Map> getStatistics() {

		TreeMap<String, Map> rate = (TreeMap) super.getStatistics();

		Map blockIds = new HashMap<String,String>();
		Map timeStart = new HashMap<String, String>();
		Map timeEnd = new HashMap<String, String>();
		Map finished= new HashMap<String, String>();
		Map started= new HashMap<String, String>();
		Map ended= new HashMap<String, String>();
		
		Set<String> keys = stats.keySet();

		if (stats.size() > 0) {
			for (String uniqueId : keys) {

				Block block = (Block) stats.get(uniqueId);
				blockIds.put(uniqueId, block.id);
				timeStart.put(uniqueId, block.getLine(0).time);
				timeEnd.put(uniqueId, block.getLine(block.getSize() - 1).time);
				
				if(patternLineMatcher==null){// in case we use startwith/endwith tags
					finished.put(uniqueId, String.valueOf(block.finished));
					started.put(uniqueId, String.valueOf(block.started));
					ended.put(uniqueId, String.valueOf(block.ended));
				}

			}
			rate.put("stat_id", blockIds);
			rate.put("time_start", timeStart);
			rate.put("time_end", timeEnd);

			if(patternLineMatcher==null){ // in case we use startwith/endwith tags
				rate.put(getName()+" _finished", finished);
				rate.put(getName()+" _started", started);
				rate.put(getName()+" _ended", ended);
			}
			
			

		}

		return rate;
	}

}
