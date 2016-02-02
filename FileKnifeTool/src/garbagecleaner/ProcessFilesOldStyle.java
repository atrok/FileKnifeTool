package garbagecleaner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Lists;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;

public class ProcessFilesOldStyle implements ProcessFiles {

	private CommandImpl strategy;
	private String ext;

	public ProcessFilesOldStyle(Strategy strategy, String ext) {
		this.strategy = (CommandImpl)strategy;
		this.ext = ext;
	}

	public void start(List<String> a) {
		Object[] args = a.toArray();
		try {
			if (args.length == 0)
				processDirectoryTree(new File("."));
			else
				for (Object obj : args){
					String arg=(String)obj;
					
					File fileArg = new File((String)arg);
					if (fileArg.isDirectory())
						processDirectoryTree(fileArg);
					else {
						// Allow user to leave off extension:
						if (!arg.endsWith("." + ext))
							arg += "." + ext;
						strategy.process(new File(arg).getCanonicalFile());
					}
				}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void processDirectoryTree(File root) throws IOException {
		for (File file : Directory.walk(root.getAbsolutePath(), ".*\\." + ext))
			strategy.process(file.getCanonicalFile());
	}



}
