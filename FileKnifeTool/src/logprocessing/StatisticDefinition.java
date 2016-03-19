package logprocessing;

import java.util.Arrays;
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
import jregex.*;

public abstract class StatisticDefinition {

	private Logger logger=LoggerFactory.getLogger(StatisticDefinition.class);
	
	private String regexp="";
	private String varname="";
	private String name="";
	private Map rate = new TreeMap<String, HashMap>();
	private jregex.Pattern patternLineMatcher;
	private Pattern patternRemoveSomePunct=Pattern.compile("[;:,]");
	private Pattern patterRemoveAllPunct=Pattern.compile("[\\d\\p{Punct}]");
	private Pattern patternNoPunct=Pattern.compile(REGEXP.NO_PUNCTUATION_NOR_DIGIT);
	private Pattern patternEndBrackets=Pattern.compile("([\\[\\(].+)|(.*[(\\s\\]|\\]|\\],)(\\)|\\),)]){1}");
	private Pattern patternMSGCFG=Pattern.compile("^MSGCFG.+[0-9]?");
	private Matcher matcher;
	private Matcher matcherBracket;
	private Matcher matcherMSGCFG;
	private Matcher matcherSomePunct;
	private long timenormalizing;
	private long timegetstatvalue;
	
	private jregex.Matcher jMatcher;
	
	private char[] brackets=new char[]{'[','(',']',')'};
	
	public StatisticDefinition(String regexp,String name){
		
		this.regexp=regexp;
		this.varname=this.name=name;
		this.patternLineMatcher=new jregex.Pattern(getRegexp());
		
		
		
		
	}
	
	public StatisticDefinition(String name, Map<String,String> parameters){
		
		this.regexp=parameters.get(StatisticParamNaming.REGEXP.toString());
		this.varname=this.name=name;
		this.patternLineMatcher=new jregex.Pattern(getRegexp());
		
		
		
		
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
						name=normalize(line);
				}
		
			
				if (FilesUtil.isNumeric(variable)){
					variable=line[new Integer(variable)]; // numeric variable is a position of the word in the line, we need to substitute it now
					//matcherSomePunct=patternRemoveSomePunct.matcher(variable);
					variable=patternRemoveSomePunct.matcher(variable).replaceAll(""); //remove all ,.;: 
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

		jMatcher=patternLineMatcher.matcher(line);
		
		if(jMatcher.matches()){
			
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
			//matcher=patternNoPunct.matcher(line[i]);
			//matcherBracket=patternEndBrackets.matcher(line[i]);
			matcherMSGCFG=patternMSGCFG.matcher(line[i]);// leave MSGCFG messages unchanged
			matcherSomePunct=patternRemoveSomePunct.matcher(line[i]);
			line[i]=matcherSomePunct.replaceAll(""); //remove all ,.;: 
			String ss=line[i];
/*            if (matcher.matches()&&variableFlag==false){// check if word begins with digit or punct sign
            	String a=line[i];
            	if(!matcherMSGCFG.matches())	       	 	
		            	a=patterRemoveAllPunct.matcher(line[i]).replaceAll(""); 
			    sb.append(" "+a);
			    logger.trace("String {} normalized to {}",line[i],a);
	       	 	
               }else{  // to handle phrase inside of bracket as a single word: [CAN iPhone at Austin]
            	   logger.trace("String {} have punct ",line[i]);
            	   variableFlag=true;
            	   
            	   if(matcherBracket.matches())
            		   variableFlag=false;
            	 
               }*/
			
     	   //if(!matcherBracket.matches()){
			/*int l=ss.length();
			if(l>=2)
			if (!(ss.charAt(l-1)==']'
					||ss.charAt(l-2)==']'
					||ss.charAt(l-1)==')'
					||ss.charAt(l-2)==')'
					||ss.charAt(0)=='('
					||ss.charAt(0)=='['
					)){// replacement for bracket regexp
			*/	
			if(!isBracketFound(ss,brackets)){
				String a = ss;
				if (!matcherMSGCFG.matches())
					a = patterRemoveAllPunct.matcher(line[i]).replaceAll("");
				if (a.length()>0)
					sb.append(" " + a);
				logger.trace("String {} normalized to {}", ss, a);
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

	private boolean isBracketFound(String s,char[] brackets){
		char[] ch=s.toCharArray();
		Arrays.sort(ch);
		for (char c:brackets){
		if(Arrays.binarySearch(ch, c)!=-1)
			return true;
		}
		return false;
	}
	
}
