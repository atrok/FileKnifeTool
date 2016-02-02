package cmdline;

import java.util.List;
import java.util.Map;

public interface Command {
	
	public List<String> getExtensions();

	public List<String> getPaths();
	
	public Map<String,String> getStatData();
}