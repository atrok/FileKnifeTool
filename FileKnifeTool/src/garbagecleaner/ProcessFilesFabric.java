package garbagecleaner;

import cmdline.CommandImpl;

public class ProcessFilesFabric {
	
	public static ProcessFiles create(CommandImpl cmd, String ext){
		
		switch(cmd.processStyle){
		case "old":
			return new ProcessFilesOldStyle(cmd,ext);
		case "new":
			return new ProcessFilesNewStyle(cmd,ext);
		}
		return null;
	}

}
