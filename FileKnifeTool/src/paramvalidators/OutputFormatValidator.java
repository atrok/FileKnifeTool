package paramvalidators;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import garbagecleaner.ENUMERATIONS;

public class OutputFormatValidator implements IParameterValidator {
	
	
	public void validate(String name, String value) throws ParameterException {
		List<String> options=Arrays.asList(
				ENUMERATIONS.FORMAT_CSV,
				ENUMERATIONS.FORMAT_SQL,
				ENUMERATIONS.FORMAT_STAT,
				ENUMERATIONS.FORMAT_BLOCK,
				ENUMERATIONS.FORMAT_TABLE);
		// TODO Auto-generated method stub
		if (value instanceof String){
			if (!options.contains(value))
				throw new ParameterException(" Entered value of -format is incorrect: "+value+", expected values are ("
			+ENUMERATIONS.FORMAT_CSV+"|"
			+ENUMERATIONS.FORMAT_SQL+"|"
			+ENUMERATIONS.FORMAT_STAT+"|"
			+ENUMERATIONS.FORMAT_BLOCK+"|"
			+ENUMERATIONS.FORMAT_TABLE+")"
					);
			return;
		}
		//throw new ParameterException(" Entered type of Action is incorrect: "+value+", expected values are (delete|print)");
	}

}