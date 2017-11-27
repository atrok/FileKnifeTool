package cmdline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import enums.Commands;
import garbagecleaner.FKTProperties;
import statmanager.StatfileNotFoundException;
import statmanager.UnsupportedStatFormatException;
import statmanager.UnsupportedStatParamException;

public class CmdLineParser {
	
	JCommander jc;
	private Logger logger=LoggerFactory.getLogger(CmdLineParser.class);
	
	static Map<Commands,Command> commands=new HashMap<Commands,Command>();

	/*
	static{
		commands=new HashMap<String,Command>();
		commands.put("delete", new CommandDelete());
		commands.put("print", new CommandPrint());
		commands.put("genesys", new CommandParse());
		commands.put("lms", new CommandParseFileWithSeparators());
	}
*/
	public CmdLineParser(){
		jc=new JCommander();
		
		addCommands();
	}
	
	public JCommander getCommander(){
		return jc;
	}
	
	public void addCommands(){
		
		//commands=new HashMap<String,Command>();
		commands.put(Commands.delete, Commands.delete.getCmd());
		commands.put(Commands.print, new CommandPrint());
		commands.put(Commands.genesys, new CommandParse());
		commands.put(Commands.lms, new CommandParseFileWithSeparators());

		for (Entry<Commands, Command> entry : commands.entrySet())
		{
			  jc.addCommand(entry.getKey().toString(),entry.getValue());
			}

	}
	
	public Command getCommandObj(String cmd) throws UnsupportedStatFormatException, StatfileNotFoundException, UnsupportedStatParamException{ // Here we return Command object determined on base of obtained from jc.getParsedCommand. 
		Command cmdobj=commands.get(Commands.valueOf(cmd));
		
		if(null!=cmdobj){
			((CommandImpl)cmdobj).init();
			return cmdobj;
		}else{
			logger.error("command with name '{}' is not found, check your command line arguments", cmd);
			return null;
		}
	}

	public Command getCommandObj(String cmd,FKTProperties context) throws UnsupportedStatFormatException, StatfileNotFoundException, UnsupportedStatParamException{ // 
		Command cmdobj=commands.get(Commands.valueOf(cmd));
		
		if(null!=cmdobj){
			((CommandImpl)cmdobj).init(context);
			return cmdobj;
		}else{
			logger.error("command with name '{}' is not found, check your command line arguments", cmd);
			return null;
		}
	}

	public void flush(){
		
		commands.clear();
		
	}
}
