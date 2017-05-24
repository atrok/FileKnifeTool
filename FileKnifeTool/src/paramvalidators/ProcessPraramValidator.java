package paramvalidators;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class ProcessPraramValidator implements IParameterValidator {
	
	
	public void validate(String name, String value) throws ParameterException {
		List<String> options=Arrays.asList("simple","record");
		// TODO Auto-generated method stub
		if (value instanceof String){
			if (!options.contains(value))
				throw new ParameterException(" Entered value of action is incorrect: "+value+", expected values are (simple|record)");
			return;
		}
		//throw new ParameterException(" Entered type of Action is incorrect: "+value+", expected values are (delete|print)");
	}

}