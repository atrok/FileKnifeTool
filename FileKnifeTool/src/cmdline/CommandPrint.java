package cmdline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import garbagecleaner.Strategy;

public class CommandPrint extends CommandImpl {
	
	int counter=0;
	Map<String,String> data=new HashMap<String,String>();

	public void process(File file) {
		// TODO Auto-generated method stub
		try {
			
			counter++;
			data.put(String.valueOf(counter), file.getAbsolutePath());
		} catch (Exception e) {
			data.put(e.toString(), file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> getStatData() {
		// TODO Auto-generated method stub
		data.put("Found",String.valueOf(counter));
		return data;
	}

	@Override
	public void resetStatData() {
		// TODO Auto-generated method stub

		counter=0;
		data=new HashMap<String,String>();
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		if (!isInitialized())
			setInitialized(true);
	}
}
