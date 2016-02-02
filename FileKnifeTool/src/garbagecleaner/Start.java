package garbagecleaner;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;

public class Start {

		private static Logger logger=LoggerFactory.getLogger(Start.class);

		// 
		public static void main(String[] args) {
			
			long start=System.nanoTime();
			logger.info("Starting...");
			CmdLineParser cmdParser = new CmdLineParser();
			JCommander commander = cmdParser.getCommander();
			try {
				commander.parse(args);
				Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());
				cmd.getExtensions().forEach(
						s->{
					logger.info("Processing extension: {}",s);
					ProcessFilesFabric.create((CommandImpl)cmd, s).start(cmd.getPaths());
					processStatData(cmd);
				});
			} catch (ParameterException ex) {
				logger.error("Parameter exception",ex);
				commander.usage();
				System.exit(1);
			} catch (Exception e) {
				logger.error("Start exception", e);
			}
			logger.info("Finished in {}", System.nanoTime()-start);

		}

		
		private static void processStatData(Command cmd){
			Map<String,String> data=cmd.getStatData();
			
			//data.forEach((id, val) -> System.out.println(id+" : "+val));		
			
			data.entrySet().stream()
	        .sorted(Map.Entry.<String, String>comparingByValue()) 
	        .forEach(System.out::println); // 
			
		}

}


