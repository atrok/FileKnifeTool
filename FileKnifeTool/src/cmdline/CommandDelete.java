package cmdline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDelete extends CommandImpl {

	int counter_found=0;
	int counter_processed=0;
	Map<String,String> errors =new HashMap<String,String>();
	@Override
	public void process(File file) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		counter_found++;
		try {
			file.delete();
			counter_processed++;
		} catch (Exception e) {
			errors.put(e.toString(),file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> getStatData() {
		// TODO Auto-generated method stub
		errors.put("Found", String.valueOf(counter_found));
		errors.put("Processed", String.valueOf(counter_processed));
		return errors;
		
	}

	@Override
	public void resetStatData() {
		// since it's a Singleton, we need to reset any stat counters before running it next time
		counter_found=0;
		counter_processed=0;
		errors =new HashMap<String,String>();
		
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		if (!isInitialized())
			setInitialized(true);
	}


}
