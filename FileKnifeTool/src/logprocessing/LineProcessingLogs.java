package logprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import util.Benchmark;
import jregex.*;
public class LineProcessingLogs implements LineProcessing{
	private String line;
	private String[] split_line;
	private List<String> split_line_list=new ArrayList<String>();
	private String sec = "";
	private Pattern patternTimestamp=Pattern.compile(REGEXP.REGEXP_TIMESTAMP_LN_BEGIN);
	private Pattern patternCheckpoint=Pattern.compile(REGEXP.PATTERN_CHECK_POINT);
	private Pattern patternLocalTime=Pattern.compile(REGEXP.PATTERN_LOCAL_TIME);
	private Pattern patternReplaceAt=Pattern.compile("@");
	private Pattern patternMatcherStart=Pattern.compile("^\\[.+");
	private Pattern patternMatcherEnd=Pattern.compile("(.+(\\]|\\]"+REGEXP.PUNCT+")|(\\]\\)|\\]"+REGEXP.PUNCT+"))$");

	private Matcher matcher;
	private Matcher matcherCheckpoint;
	private Matcher matcherLocalTime;
	
	private jregex.Matcher jMatcher;
	private jregex.Matcher jMatcherCheckpoint;
	private jregex.Matcher jMatcherLocalTime;
	//private jregex.Pattern jPatternTimestamp=new jregex.Pattern(REGEXP.REGEXP_TIMESTAMP_LN_BEGIN);
	private jregex.Pattern jPatternTimestamp=new jregex.Pattern("^[0-9]{2,}[-:]");
	private jregex.Pattern jPatternCheckpoint=new jregex.Pattern(REGEXP.PATTERN_CHECK_POINT);
	private jregex.Pattern jPatternLocalTime=new jregex.Pattern(REGEXP.PATTERN_LOCAL_TIME);
	private jregex.Pattern jPatternMatcherEnd=new jregex.Pattern("(.+(\\]|\\]"+REGEXP.PUNCT+")|(\\]\\)|\\]"+REGEXP.PUNCT+"))$");

	

	private long timeprocessing;
	private long timenormalizing;
	private long timegetstatvalue;
	private long timesplitline;
	
	private static final Splitter SPACE_SPLITTER = Splitter.on(' ')
		       .trimResults()
		       .omitEmptyStrings();
	
	
	private static final Splitter TIMESTAMP_SPLITTER = Splitter.on(REGEXP.PATTERN_SPLIT_LONG_TIMESTAMP)
		       .trimResults()
		       .omitEmptyStrings();
	
	
	private Map<String, String[]> time = new HashMap<String, String[]>();

	
	private int sampling = 10;
	private StatisticManager sm;
	
	private Logger logger=LoggerFactory.getLogger(LineProcessingLogs.class);

	public LineProcessingLogs(int sampling, StatisticManager sm) {
		this.sampling = sampling;
		time.put("start_time", new String[7]);
		time.put("end_time", new String[7]);
		this.sm = sm;
		// statistics=sm.getStatistics().toArray(new
		// StatisticDefinition[sm.getStatistics().size()]);
	}

	public void processLine(String ln) {
		this.line = ln;
		
		//split_line = line.split(REGEXP.SPACES); // split line by spaces
		//split_line=Iterables.toArray(SPACE_SPLITTER.split(line), String.class);
		//split_line_list=SPACE_SPLITTER.splitToList(line);
		Benchmark.tick();
		//split_line=StringUtils.split(line);//TODO
		split_line=splitSmart(line);
		timesplitline+=Benchmark.tack();
		
		try {

			// if (b){// if line begins with timestamp we may start processing
			// the line
			if (time_processing(sampling)){
				
			
			//matcher=patternTimestamp.matcher(split_line[0]);
			
			//jMatcher=jPatternTimestamp.matcher(split_line[0]);22.03.16 why we need this check?
			
			//if (matcher.find()) {
			//if (jMatcher.find()) {//22.03.16
				sec = sampling(sampling);

				logger.trace("Obtained sampled timestamp:{}", sec);
				
				try{
				Iterator<?> m = sm.getStatisticsList().iterator();
				
				while (m.hasNext()) {
					StatisticDefinition p=(StatisticDefinition) m.next();
					if (p.isMatched(ln)) 
						p.calculate(ln, split_line, sec);
					
					timenormalizing+=p.getTimenormalizing();
					timegetstatvalue+=p.getTimegetstatvalue();
				}
				}catch(Exception e){
					System.out.println("Statistic Manager is likely empty: please add Statistic Definitions\n");
					logger.error(ln);
					logger.error("Statistic Manager is likely empty or one of statistics is defined incorrectly {}",e);
					System.exit(0);
				}
			}
		 //}// 22.03.16
		} catch (PatternSyntaxException e) {
			logger.error("Pattern error", e);
		}
	}

    // time that tick() was called
    static long tickTime;

    // called at start of operation, for timing
    static void tick () {
        tickTime = System.nanoTime();
    }

    // called at end of operation, prints message and time since tick().
    static void tock (String action) {
        long mstime = (System.nanoTime() - tickTime) / 1000000;
        System.out.println(action + ": " + mstime + "ms");
    }	
    
    
    /*
	 * 
	 * #this function is to be called on per line basis #it parses the line to
	 * retrieve timestamp and returns array[]{yyyy,mm,dd,hh,mm,sec,msec} #use
	 * this array to feed to calc_time_scalar # if provided $value then it would
	 * parse it into array # in opposite case it parses $0 and derives date from
	 * there # $sampling is needed if we need to round time to some predefined
	 * value
	 * 
	 * #1 - 1 min sampling #10 - 10 min sampling
	 * 
	 * returns true if following processing is needed
	 * returns false if processing could be avoided
	 */
	
    
	private boolean time_processing(int sampling) {
		/*
		 * # 13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54 # 0 1 2 3 4
		 * 5
		 */
		
		long start=System.nanoTime();
		
		if (split_line.length>0){
		String[] time_temp = new String[7];
		String timestamp = split_line[0];
		//timestamp.replace("@", "");
		patternReplaceAt.matcher(timestamp).replaceAll("");
/*
		matcher=patternTimestamp.matcher(timestamp);
		matcherCheckpoint=patternCheckpoint.matcher(line);
		matcherLocalTime=patternLocalTime.matcher(line);
		//tick();
*/		
		

		jMatcher=jPatternTimestamp.matcher(timestamp);
		jMatcherCheckpoint=jPatternCheckpoint.matcher(line);
		jMatcherLocalTime=jPatternLocalTime.matcher(line);
		//tick();

		
		//if (matcher.find()) {
		if (jMatcher.find()) {
			
		//tock("patternTimestamp found:");
		
		
			if (jMatcherCheckpoint.find()) {
				
				int checkInd=getIndexForArrElement(split_line,"Check");
				
				if (checkInd!=-1){// properly determined index position
					checkInd=checkInd+2;
				}else {
					
					logger.error(" 'Check' word isn't found where expected, interrupting!! {}",split_line);
					return false;
				};
				
				List<String> test = new ArrayList<String>(
						Arrays.asList(
								split_line[checkInd].split(REGEXP.PATTERN_SPLIT_LONG_TIMESTAMP)
								));
				 
				//List<String> test=TIMESTAMP_SPLITTER.splitToList(split_line[5]);
		
				String[] t = split_line[0].split(REGEXP.PATTERN_SPLIT_LONG_TIMESTAMP);
		//tock("split_line[0].split :");
		
				if (t.length == 4 && t[3] != null)
					test.add(t[3]); // # 13:16:54.058 Trc 04120 Check point	2014-09-16T13:16:54
									// take ms from ^ and put it into temp_time[6]
				test.toArray(time_temp);
			} else {

				if (13 < timestamp.length()) { //// 2015-09-17T19:32:29.778
					
					time_temp = timestamp.split(REGEXP.PATTERN_SPLIT_LONG_TIMESTAMP);
		//tock("long timestamp split :");		
				} else {

					// 13:16:54.215 Trc 04541 Message MSGCFG_GETOBJECTINFO
					// received from 224 (CCView 'CCPulse_701')
					// 1
					// print "debug: " name

					String[] arr_new_time = timestamp.split(REGEXP.PATTERN_SPLIT_SHORT_TIMESTAMP);
		//tock("short timestamp split :");			
					for (int i = 0; i < arr_new_time.length; i++) {
						time_temp[i + 3] = arr_new_time[i]; // i+3 is offset
															// because of
															// asort_new_time
															// has no date but
															// time only, first
															// 3 indices refer
															// to dd mm yy
					}

				}
			}
			putTime(time, "end_time", time_temp);
			
			//tock("timeprocessing finished in:");
			return true;
		}else{
			/*
			 * # it will work in case file has a predefined Genesys format 
			 * Local time: 2014-09-16T13:14:58.465 
			 * # 1 2
			 */
			if (jMatcherLocalTime.matches()) {
		//tock("matcherLocalTime.matches() :");
				time_temp = split_line[2].split(REGEXP.PATTERN_SPLIT_LONG_TIMESTAMP);
		//tock("long timestamp split :");
				putTime(time, "start_time", time_temp);
				putTime(time, "end_time", time_temp);
			}
		}
		//tock("timeprocessing finished in:");
		}
		
		timeprocessing+=System.nanoTime()-start;
		return false;
	}

	/*
	 * we need this method to update time[start_time] array members without
	 * rewriting its previous values. for instance timestamp could be in short or
	 * long form, if it's short formed then array of its values would look like
	 * [null, null,null, value2, value2, value2] if time contains previous
	 * timestamp in long form it would look like [value1,value1,
	 * value1,value1,value1] this method allows us to combine 2 arrays so that
	 * resulting array would look like [value1, value1,value1,
	 * value2,value2,value2]
	 */
	private void putTime(Map<String, String[]> dest, String index, String... source) {
		String[] t = (String[]) dest.get(index);
		for (int i = 0; i < source.length; i++) {
			if (source[i] != null && source[i].length() != 0)
				t[i] = source[i];

		}

		dest.put(index, t);
	}

	protected String sampling(int sampling) {
		String sec = "";
		switch (sampling) {
		
		case 0: //as is
			return time.get("end_time")[0] + "-" + time.get("end_time")[1] + "-" + time.get("end_time")[2] + " "
					+ time.get("end_time")[3] + ":" + time.get("end_time")[4]+":"+time.get("end_time")[5]+"."+time.get("end_time")[6];
		case 1:
			sec = time.get("end_time")[0] + "-" + time.get("end_time")[1] + "-" + time.get("end_time")[2] + " "
					+ time.get("end_time")[3] + ":" + time.get("end_time")[4]; // to
																				// minutes
			break;
		case 10:
			int t = (Integer.parseInt(time.get("end_time")[4]) / sampling) * 10;

			sec = time.get("end_time")[0] + "-" + time.get("end_time")[1] + "-" + time.get("end_time")[2] + " "
					+ time.get("end_time")[3] + ":" + Integer.toString(t);
			break;
		case 60:
			sec = time.get("end_time")[0] + "-" + time.get("end_time")[1] + "-" + time.get("end_time")[2] + " "
					+ time.get("end_time")[3] + ":" + "00";
			break;
		case 24:
			sec = time.get("end_time")[0] + "-" + time.get("end_time")[1] + "-" + time.get("end_time")[2] + " "
					+ "00" + ":" +"00";
			break;
		}
		return sec;
	}

	public long getTimeprocessing() {
		return timeprocessing;
	}

	public long getTimenormalizing() {
		return timenormalizing;
	}

	public long getTimegetstatvalue() {
		return timegetstatvalue;
	}

	public long getTimesplitline() {
		return timesplitline;
	}

	public void getReport() {
		logger.debug("timeprocessing: {} sec",TimeUnit.SECONDS.convert(getTimeprocessing(), TimeUnit.NANOSECONDS));
        logger.debug("normalizing: {} sec",TimeUnit.SECONDS.convert(getTimenormalizing(), TimeUnit.NANOSECONDS));
        logger.debug("getstatvalue: {} sec",TimeUnit.SECONDS.convert(getTimegetstatvalue(),TimeUnit.NANOSECONDS));
        logger.debug("getsplitvalue: {} sec",TimeUnit.SECONDS.convert(getTimesplitline(),TimeUnit.NANOSECONDS));
		
    	timeprocessing=timenormalizing=timegetstatvalue=timesplitline=0;
	}

	@Override
	public Map getData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String[] splitSmart(String s){

			
			
			String[] arr=StringUtils.split(s);
			List<String> list=new ArrayList<String>();
			StringBuilder sb=new StringBuilder();
			//StringBuilder variable=new StringBuilder();
			boolean flag=false;
			//list.add(arr);
			for (String ss: arr){
				sb.append(ss);			
				
			//	matcher=patternMatcherStart.matcher(ss);
			//	if (matcher.matches())
				if (ss.charAt(0)=='['||ss.charAt(0)=='\'')
					flag=true;
				
				if(flag)
					sb.append(" ");
				
					
				//jMatcher=jPatternMatcherEnd.matcher(ss);
				
				//if (jMatcher.matches()){
				
				if (ss.charAt(ss.length()-1)==']'
						||ss.charAt(ss.length()-1)=='\''
						||(ss.length()>=2&&ss.charAt(ss.length()-2)==']')
						||(ss.length()>=2&&ss.charAt(ss.length()-2)=='\'')
						
						){// replacement for bracket regexp
						
					flag=false;
					sb.deleteCharAt(sb.length()-1);
				}
				
				if (!flag){
					list.add(sb.toString());
					sb.delete(0, sb.length());
				}
					
			}
			return list.toArray(new String[]{});
		
	}
	
	private int getIndexForArrElement(String[] arr,String str){
		
		for(int i=0;i<arr.length;i++){
			if (arr[i].equals(str))
					return i;
		}
		
		return -1;
	}

	@Override
	public void processLine(String ln, String[] params) {
		// TODO Auto-generated method stub
		
		processLine(ln);
		
	}
}
