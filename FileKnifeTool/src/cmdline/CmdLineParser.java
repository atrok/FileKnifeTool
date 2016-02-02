package cmdline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

public class CmdLineParser {
	
	JCommander jc;
	private Logger logger=LoggerFactory.getLogger(CmdLineParser.class);
	
	static Map<String,Command> commands;
	static{
		commands=new HashMap<String,Command>();
		commands.put("delete", new CommandDelete());
		commands.put("print", new CommandPrint());
		commands.put("genesys", new CommandParse());
	}

	public CmdLineParser(){
		jc=new JCommander();
		
		addCommands();
	}
	
	public JCommander getCommander(){
		return jc;
	}
	
	public void addCommands(){
		
		for (Entry<String, Command> entry : commands.entrySet())
		{
			  jc.addCommand(entry.getKey(),entry.getValue());
			}

	}
	
	public Command getCommandObj(String cmd){ // Here we return Command object determined on base of obtained from jc.getParsedCommand. 
		Command cmdobj=commands.get(cmd);
		
		if(null!=cmdobj){
			((CommandImpl)cmdobj).init();
			return cmdobj;
		}else{
			logger.error("command with name '{}' is not found, check your command line arguments", cmd);
			return null;
		}
	}
}
