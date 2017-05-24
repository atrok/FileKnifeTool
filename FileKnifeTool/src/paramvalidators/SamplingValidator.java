package paramvalidators;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class SamplingValidator implements IParameterValidator {
	
	
	public void validate(String name, String value) throws ParameterException {
		List<String> options=Arrays.asList("0","1","10","60","24","24h");
		// TODO Auto-generated method stub
		if (value instanceof String){
			if (!options.contains(value))
				throw new ParameterException(" Entered value of -sample is incorrect: "+value+", expected values are (0|1|10|60|24(h))");
			return;
		}
		//throw new ParameterException(" Entered type of Action is incorrect: "+value+", expected values are (delete|print)");
	}

}