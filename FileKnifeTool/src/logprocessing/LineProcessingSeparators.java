package logprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineProcessingSeparators implements LineProcessing{

	private Logger logger=LoggerFactory.getLogger(LineProcessingSeparators.class);
	private Pattern semicolon=Pattern.compile("(^[;].*|^$)");
	private Pattern quotes=Pattern.compile(REGEXP.QuoteIdentifiers);
	
	private Matcher quoteMatcher;
	private Matcher matcher;
	private char separator;
	private Map result=new HashMap();
	
	public LineProcessingSeparators(char separator){
		this.separator=separator;
	}
	@Override
	public void processLine(String ln) {
		// TODO Auto-generated method stub
		
		String[] split_line;
		matcher=semicolon.matcher(ln);
		if (!matcher.find()){
			ln=quotes.matcher(ln).replaceAll(""); // clean up the string to remove all quote identifiers
			split_line=StringUtils.split(ln, separator);
			if(!(split_line.length<3 || Pattern.matches(REGEXP.SPACES, split_line[0]))){
				result.put(split_line, 0);
			}
		}
		
	}
	
	public Map getData() {
		// TODO Auto-generated method stub
		
		return result;
	}
	
	public void getReport() {
		// TODO Auto-generated method stub
		
		logger.info("Lines with separators have been processed succesfully");
	}
	@Override
	public void processLine(String ln, String[] params) {//this is private case of adding file name as a value to the string to be parsed
		// TODO Auto-generated method stub
		processLine(ln+separator+params[0]);		
	}

}
