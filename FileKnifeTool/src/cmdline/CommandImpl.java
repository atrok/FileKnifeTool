package cmdline;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import garbagecleaner.Strategy;

@Parameters (separators=",", commandDescription=" command to add firewall rule")
public abstract class CommandImpl implements Command,Strategy{


	@Parameter(names = "-ext", description = "file extension to be looked", variableArity=true, required = true)
	  private List<String> extensions;
	 
	  @Parameter(names = "-d", description = "list of directories to be checked", variableArity=true, required = true)
	  private List<String> paths;
	  
	  @Parameter(names = "-style", description = "style of processing (old|new)", required = false)
	  public String processStyle="new";

	  private boolean initialized=false;
	  
	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public List<String> getExtensions() {
		return extensions;
	}

	public List<String> getPaths() {
		return paths;
	}

	public abstract void resetStatData();
	public abstract void process(File file);

	protected abstract void init();
	


}
