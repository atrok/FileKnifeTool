package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.FilesUtil;

public abstract class StatisticDefinition {

	private Logger logger=LoggerFactory.getLogger(StatisticDefinition.class);
	
	private String regexp="";
	private String varname="";
	private String name="";
	private Map rate = new TreeMap<String, HashMap>();
	private Pattern patternLineMatcher;
	private Pattern patternRemovePunct=Pattern.compile("[\\d\\p{Punct}]");
	private Pattern patternNoPunct=Pattern.compile(REGEXP.NO_PUNCTUATION_NOR_DIGIT);
	private Pattern patternEndBrackets=Pattern.compile(".+[\\]\\)]$");
	private Matcher matcher;
	private Matcher matcherBracket;
	private long timenormalizing;
	private long timegetstatvalue;
	
	public StatisticDefinition(String regexp,String name){
		
		this.regexp=regexp;
		this.varname=this.name=name;
		this.patternLineMatcher=Pattern.compile(getRegexp());
		
		
		
		
	}
	
	public StatisticDefinition(String name, Map<String,String> parameters){
		
		this.regexp=parameters.get(StatisticParamNaming.REGEXP.toString());
		this.varname=this.name=name;
		this.patternLineMatcher=Pattern.compile(getRegexp());
		
		
		
		
	}
	
	public String getRegexp() {
		return regexp;
	}



	public String getName() {
		return name;
	}

	
/*
 *   generate msgID that would uniquely identify the message
 *   name could be complex string containing variables prepended by '$', like 
 *   "#ObjectSent" $9 ">1000"
 *   where $9 represents 9th element of array representing the line
 *   # 12:16:48.843 Trc 24215 There are [1] objects of type [CfgHost] sent to the client [1132] (application [Workspace], type [InteractionWorkspace ])
 *   	0			1		2	3	4	5	6		7	8		9	
 */
	private void generateMsgID(String[] line){
		char dollar='$';
		String variable = null;
		String msgID=null;
		
		//String message=normalize(line);
		
		String[] nm=varname.split(REGEXP.SPACES);
		
		
		for (int i=0;i<nm.length; i++){
			
			if (dollar == nm[i].charAt(0)){// find variable
				variable=nm[i].substring(1);		
			
				if (variable.equals("msgID")){
					
					
					//if (Pattern.matches(".+Message.+",line[3]))
						name=normalize(line);;
				}
		
			
				if (FilesUtil.isNumeric(variable)){
					variable=line[new Integer(variable)]; // numeric variable is a position of the word in the line, we need to substitute it now
					name=varname.replace(nm[i], variable);
					
				}

			}
		}
		
//		if (null==msgID){// there was no variables in the name of statistic so we take it as is
//			msgID=name;
//		}
		
		
	}
	
	protected void updateStatValue(Integer value, String sampled_timeframe){
		
			//rate.put(getName(), ((Map)rate.get(getName())).put(sampled_timeframe,value));
			
			Map t= (Map)rate.get(getName());
			t.put(sampled_timeframe, value);
			rate.put(getName(),t);
			
	}
	/*
	 * returns last value of requested statistic or null if not found
	 */
	protected Integer getStatValue(String line, String[] splitline, String sampled_timeframe){
		
		long start=System.nanoTime();
		
		Map<String, Integer> t;

		matcher=patternLineMatcher.matcher(line);
		
		if(matcher.matches()){
			
			generateMsgID(splitline);
			
			if (!rate.containsKey(getName())){// create new hashmap for sampled time statistics
				rate.put(getName(),new HashMap<String,Integer>());
			}
			
			t=(Map) rate.get(getName());
			
			if (!t.containsKey(sampled_timeframe)){
				t.put(sampled_timeframe, 0);
			}
			timegetstatvalue=System.nanoTime()-start;
			return t.get(sampled_timeframe);
		}
		timegetstatvalue=System.nanoTime()-start;
		return null;

		
	}
	
	private String normalize(String[] line){
		long sec=System.nanoTime();
		logger.trace("Normalizing starts");
		
		StringBuilder sb=new StringBuilder();
		StringBuilder variable=new StringBuilder();
		sb.append(line[1]+" "+line[2]);
		boolean variableFlag=false;
		for(int i=3;i<line.length;i++){ // getting rid of punctuation and variable parts of message to be able to print uniform message
			matcher=patternNoPunct.matcher(line[i]);
			matcherBracket=patternEndBrackets.matcher(line[i]);
			
            if (matcher.matches()&&variableFlag==false){// check if word begins with digit or punct sign
	       	 	String a=patternRemovePunct.matcher(line[i]).replaceAll(""); 
	       	 	sb.append(" "+a);
               }else{  // to handle phrase inside of bracket as a single word: [CAN iPhone at Austin]
            	   logger.debug("String {} starts with punct ",line[i]);
            	   variableFlag=true;
            	   
            	   if(matcherBracket.matches())
            		   variableFlag=false;
            	 
               }
		}
		
		timenormalizing=System.nanoTime()-sec;
   		logger.trace("normalization result {}, took: {}",sb.toString(), TimeUnit.SECONDS.convert(timenormalizing, TimeUnit.NANOSECONDS));
		
		return sb.toString();
    
	}
	public abstract void calculate(String line, String[] splitline, String sampled_timeframe);
	
	public Map<String,Map> getStatistics(){
		return rate;
	}



	public long getTimenormalizing() {
		return timenormalizing;
	}



	public long getTimegetstatvalue() {
		return timegetstatvalue;
	};

	
}
