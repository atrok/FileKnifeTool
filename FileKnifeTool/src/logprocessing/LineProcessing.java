package logprocessing;

import java.util.Map;

public interface LineProcessing {

	public void processLine(String ln);
	public void getReport();
	public Map getData();
}
