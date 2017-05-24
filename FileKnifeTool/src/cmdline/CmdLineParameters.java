package cmdline;


import com.beust.jcommander.Parameter;

import paramvalidators.ActionValidator;


public class CmdLineParameters  {
	


	  @Parameter(description = "delete or printout the list of the files (delete/print)", validateWith = ActionValidator.class, required = true)
	  private String action;

	  @Parameter(names = "-ext", description = "file extension to be looked", required = true)
	  private String[] extensions;
	 
	  @Parameter(names = "-d", description = "list of directories to be checked", required = true)
	  private String[] paths;

	public String getAction() {
		return action;
	}

	public String[] getExtensions() {
		return extensions;
	}

	public String[] getPaths() {
		return paths;
	}

}