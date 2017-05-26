package logprocessing;

import java.util.ArrayList;
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

import com.beust.jcommander.ParameterException;

import garbagecleaner.ENUMERATIONS;
import util.DateTime;
import util.FilesUtil;
import jregex.*;

public abstract class StatisticDefinition {

	private Logger logger=LoggerFactory.getLogger(StatisticDefinition.class);
	
	private String regexp="";
	private String varname="";
	private String name="";
	private String column="simple";
	
	private Map rate = new TreeMap<String, HashMap>();
	private jregex.Pattern patternLineMatcher;
	private Pattern patternRemoveSomePunct=Pattern.compile("[;:,]");
	private Pattern patterRemoveAllPunct=Pattern.compile(REGEXP.Log_Processing_RemoveSomePunct);
	private Pattern patternNoPunct=Pattern.compile(REGEXP.NO_PUNCTUATION_NOR_DIGIT);
	//private jregex.Pattern patternEndBrackets=new jregex.Pattern("([\\[\\(].+)|(.*[(\\s\\]|\\]|\\],)(\\)|\\),)]){1}");
	private jregex.Pattern patternOpenBrackets=new jregex.Pattern(REGEXP.Log_Processing_OpenBrackets);
	private jregex.Pattern patternCommas=new jregex.Pattern(REGEXP.Log_Processing_Commas);
	private jregex.Pattern patternEndBrackets=new jregex.Pattern(REGEXP.Log_Processing_EndBrackets);
	
	private Pattern patternMSGCFG=Pattern.compile(REGEXP.Log_Processing_MSGCFG);
	private Pattern patternPunct=Pattern.compile(REGEXP.PUNCT);
	private Pattern patternPunctButCommas=Pattern.compile(REGEXP.Log_Processing_PunctButCommas);
	
	private Pattern patternDigitPunct=Pattern.compile(REGEXP.DIGIT_PUNCT);

	private Matcher matcher;
	private Matcher matcherBracket;
	private Matcher matcherMSGCFG;
	private Matcher matcherSomePunct;
	private long timenormalizing;
	private long timegetstatvalue;
	private String[] nm;
	private jregex.Matcher jMatcher;
	private String[] foundGroups;
	
	protected boolean useFilename=false;
	
	private char[][] brackets=new char[][]{
			new char[]{'[',']'},
			new char[]{'(',')'}
	};
	private Character closingBracket=new Character(' ');
	
	int bracketArray=-1;
	int closingBracketInd;

	protected String filename;
	
	public StatisticDefinition(String regexp,String name){
		
		this.regexp=regexp;
		this.varname=this.name=name;
		this.patternLineMatcher=new jregex.Pattern(getRegexp());
		nm=varname.split(REGEXP.SPACES);
		
		
		
		
	}
	
	public StatisticDefinition(String name, Map<String,String> parameters){
		
		this.regexp=parameters.get(StatisticParamNaming.REGEXP.toString());
		
		/*
		 * if parameter column is specified in stat properties, it means we want to use a specific name for a column (for instance with format=simple)
		 * then we will check updatestat requests to see if the call to update stat value comes with sampled_timeframe or not
		 * if it's null we put value of column variable instead. 
		 * 
		 * if request has sampled_timeframe empty, but column field is not defined in stat properties then "simple" name is used by default;
		 * 
		 */
		
		String c=parameters.get(StatisticParamNaming.COLUMN.toString());
		if (c!=null)
			this.column=c;
		
		this.varname=this.name=name;
		this.patternLineMatcher=new jregex.Pattern(getRegexp());
		nm=varname.split(REGEXP.SPACES);
		
		String rowname=parameters.get(StatisticParamNaming.ROWNAME.toString());
		if (null!=rowname)
			if(!FilesUtil.isNumeric(rowname)){
			if(rowname.equals(ENUMERATIONS.STATDEF_FILENAME)){
				useFilename=true;
			}else{
				throw new ParameterException("Value of 'field' parameter in statistic "+name+" could be 'filename' or numeric. Instead we got '"+rowname+"'");
			}
		}
	
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
 *   or
 *   "#ObjectSent" $name ">1000" 
 *   where $name represents the name of the regex group (?<name>X)
 *   
 */
	private void generateMsgID(String[] line){
		char dollar='$';
		String variable = null;
		String msgID=null;
		
		//String message=normalize(line);
		
		//String[] nm=varname.split(REGEXP.SPACES);
		
		
		for (int i=0;i<nm.length; i++){
			
			if (dollar == nm[i].charAt(0)){// find variable
				variable=nm[i].substring(1);		
			
				if (variable.toLowerCase().equals(ENUMERATIONS.STATDEF_MSGID)){
					
					
					//if (Pattern.matches(".+Message.+",line[3]))
						name=normalize(line);
				}
				
				if (variable.toLowerCase().equals(ENUMERATIONS.STATDEF_FILENAME)){
					
					
					//if (Pattern.matches(".+Message.+",line[3]))
						name=filename;
				}
		
			
				if (FilesUtil.isNumeric(variable)){
					if(jMatcher.groupCount()>1){//found groups in matched regex
						variable=foundGroups[new Integer(variable)];
					}else{
						variable=line[new Integer(variable)]; // numeric variable is a position of the word in the line, we need to substitute it now
						//matcherSomePunct=patternRemoveSomePunct.matcher(variable);
						variable=patternRemoveSomePunct.matcher(variable).replaceAll(""); //remove all ,.;:
					
					}
					name=varname.replace(nm[i], variable);
					
				}

			}
		}
		
//		if (null==msgID){// there was no variables in the name of statistic so we take it as is
//			msgID=name;
//		}
		
		
	}
	private String checkColumn(String name){
		if(null==name)
			return column;
		return name;
	}
	
	
	protected void updateStatValue(double new_value, String column){
		
			//rate.put(getName(), ((Map)rate.get(getName())).put(sampled_timeframe,value));
			
			Map t= (Map)rate.get(getName());
			t.put(checkColumn(column), new_value);
			rate.put(getName(),t);
			
	}
	/*
	 * returns last value of requested statistic or null if not found
	 */
	protected Double getStatValue(String line, String[] splitline, String sampled_timeframe){
		
		String column=checkColumn(sampled_timeframe);
		filename=splitline[splitline.length-1];
		
		String[] stripped_splitline=Arrays.copyOfRange(splitline, 0, splitline.length-1); // get rid of filename in last cell;
		
		long start=System.nanoTime();
		
		Map<String, Double> t;

		//jMatcher=patternLineMatcher.matcher(line);
		
		//if(jMatcher.matches()){
			
			generateMsgID(stripped_splitline);
			
			if (!rate.containsKey(getName())){// create new hashmap for sampled time statistics
				rate.put(getName(),new HashMap<String,Double>());
			}
			
			t=(Map) rate.get(getName());
			
			if (!t.containsKey(column)){
				t.put(column, (double) 0);
			}
			timegetstatvalue=System.nanoTime()-start;
			return t.get(column);
		//}
		//timegetstatvalue=System.nanoTime()-start;
		//return null;

		
	}
	
	protected boolean isMatched(String line){
		
		jMatcher=patternLineMatcher.matcher(line);
		boolean b=jMatcher.matches();
		if(b)
			foundGroups=jMatcher.groups();
		
		return b;
		
	}
	
	private String normalize(String[] line){
		long sec=System.nanoTime();
		logger.trace("Normalizing starts");
		
		StringBuilder sb=new StringBuilder();
		StringBuilder variable=new StringBuilder();
		if (line.length>=3){
		sb.append(line[1]+" "+line[2]);// assumption based on timestamp based line
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
			
			if(!isBracketFound(ss)){
				String a = ss;
				if (!matcherMSGCFG.matches()){
					a = patterRemoveAllPunct.matcher(line[i]).replaceAll("");
				}
				if (a.length()>0 && !patternDigitPunct.matcher(a).find())
					sb.append(" " + a);
				logger.trace("String {} normalized to {}", ss, a);
     	   }
		}
		}else{
			for (int i=0; i<line.length;i++)
				sb.append(patterRemoveAllPunct.matcher(Arrays.toString(line)).replaceAll(""));
			
		}
		timenormalizing=System.nanoTime()-sec;
   		logger.trace("normalization result {}, took: {}",sb.toString(), TimeUnit.SECONDS.convert(timenormalizing, TimeUnit.NANOSECONDS));
		
		return sb.toString();
    
	}
	public abstract void calculate(String line, String[] splitline, String sampled_timeframe);
	
	public Map<String,Map> getStatistics(){
		return rate;
	}

	protected double toNumberFormat(String s){
		Matcher matcher=patternPunctButCommas.matcher(s);
		s=matcher.replaceAll("");	
		double v=0;
		try{
		v=Double.valueOf(s);
		
		}catch(NumberFormatException e){
			String ss="Cannot convert to number the value '"+s+" for Statistic /"+getName()+"/";
			logger.error(ss,e);
		}
		return v;
		
	}

	public long getTimenormalizing() {
		return timenormalizing;
	}



	public long getTimegetstatvalue() {
		return timegetstatvalue;
	};

	private boolean isBracketFound(String s){
		
		s=s.toLowerCase();
		
		boolean result=false;
		char[] ch=s.toCharArray();
		Arrays.sort(ch);
		for (int i=0;i<brackets.length;i++){
			
			for(char c:brackets[i]){
				int ind=Arrays.binarySearch(ch, c);
				if(ind>=0){
					
						if(bracketArray==-1){
							closingBracket=brackets[i][1];
							bracketArray=i;
						}
						
						if(c==closingBracket && bracketArray==i){
							bracketArray=-1;
							closingBracket=' ';
						}
						
						result=true;
				}
			}
			
		}
		
		if (bracketArray!=-1) result=true;
		
		if (patternCommas.matcher(s).matches())result=true;
		
		return result;
	}

	private boolean _isBracketFound(String s){
		jMatcher=patternOpenBrackets.matcher(s);
		boolean result=false;
		if (jMatcher.find()) {
			bracketArray=1;
			result=true;
		}
		jMatcher=patternEndBrackets.matcher(s);
		if (jMatcher.find()){
			bracketArray=-1;
			result=true;
		}
		
		if (result==false&&bracketArray!=-1) result=true;
		
		return result;
		
		
	}
	
	protected String[] getRegexGroups(){
		
		return jMatcher.groups();
		
	}
	
	public String toString(){
		return "Statistic:{\nstattype\t"+this.getClass().getName()
		+"\nheader\t"+getName()
		+"\nregexp\t"+this.regexp
		+"\ncolumn\t"+this.column
		+"\nuseFilename\t"+useFilename;
		
	}
	
	class Block{
		
		String id;
		
		ArrayList<Line> lines=new ArrayList<Line>();
		
		Block (String id){
			this.id=id;
		}
		public Line getLine(int i){
			return lines.get(i);
		}
		
		public boolean addLine(Line l){
			return lines.add(l);
		}
		
		public int getSize(){
			
			return lines.size();
			
		}
		public long getDuration() {
			// TODO Auto-generated method stub
			
			long duration=DateTime.getTimeDifference(
					lines.get(0).getTime(), 
					lines.get(getSize()-1).getTime());
			return duration;
		}
		
	}
	
	class Line{
		
		String time;

		String[] splitline;

		Line(String... splitline){
			this.splitline=splitline;
		}
		
		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}


	}
}
