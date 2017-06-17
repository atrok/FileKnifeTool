package logprocessing;

import java.util.regex.Pattern;

public class REGEXP {
	
	public static final String REGEXP_TIMESTAMP_LN_BEGIN = "^([0-9].+[-:]){2,}"; //
	public static final String REGEXP_MSG_CATEGORIES = ".+(Trc|Std|Int|Dbg).+";
	public static final String SPACES = "\\s+";
	
	public static final String PATTERN_CHECK_POINT = "Check\\spoint\\s+([\\d]{4,4}(-[\\d]{2,2})+T[\\d]{2,2}:[\\d]{2,2}:[\\d]{2,2}.[\\d]{3,3}).?";
	public static final String CHECK_POINT = "Check point";
	public static final String PATTERN_SPLIT_LONG_TIMESTAMP="[T\\.:-]";
	public static final String PATTERN_SPLIT_SHORT_TIMESTAMP="[@:\\.T]";
	public static final String PATTERN_LOCAL_TIME = "^Local time";
	public static final String NO_PUNCTUATION_NOR_DIGIT = "^[^\\p{Digit}\\p{Punct}].+";//"^[^(\\p{Digit}|\\p{Punct})].+";
	public static final String PUNCT = "\\p{Punct}";
	
	
	public static final String Log_Processing_RemoveSomePunct="[;:,]";
	public static final String Log_Processing_RemoveAllPunct="[\\d\\p{Punct}]";
	
	public static final String Log_Processing_OpenBrackets="^[\\[\\(\"\']";
	public static final String Log_Processing_Commas="^[\"\'].+[\"\']$";
	public static final String Log_Processing_EndBrackets="[(\\s\\]|\\]|\\],)(\\)|\\),)\'\"]$";
	
	public static final String Log_Processing_MSGCFG="^MSGCFG.+[0-9]?";
	public static final String Log_Processing_PunctButCommas="[^\\w\\.\\,]";
	
	public static final String QuoteIdentifiers ="[\'\"]";
	public static final String DIGIT_PUNCT = "[\\d\\p{Punct}]";
}
