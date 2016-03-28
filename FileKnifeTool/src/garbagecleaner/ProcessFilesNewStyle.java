package garbagecleaner;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cmdline.Command;
import cmdline.CommandImpl;
import jregex.*;

public class ProcessFilesNewStyle implements ProcessFiles {

	private Logger logger=LoggerFactory.getLogger(ProcessFilesNewStyle.class);
	private CommandImpl cmd;
	private String ext;
	jregex.Pattern jpattern;
	jregex.Matcher matcher;
	public ProcessFilesNewStyle(Strategy strategy, String ext) {
		this.cmd = (CommandImpl)strategy;
		this.ext = ext;
		jpattern = new jregex.Pattern(ext);
	}

	@Override
	public void start(List<String> a) {
		// TODO Auto-generated method stub

		a.forEach(
				s-> walk(Paths.get(s))
				);
		  
	}

		  private void walk(Path start){
			  
			  try {
					Files.walk(start,Integer.MAX_VALUE)
								.map(s->s.toString())
					           .filter(s -> jpattern.matcher(s).find())
					           .forEach(s -> {
					        	   try {
					        		   logger.info("Processing: {}",s);
					        		   cmd.process(Paths.get(s).toFile());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("{}",e);
								}
					           });
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("{}",e);
				}

			  
		  }
		
	}




