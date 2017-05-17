package logprocessing;

import garbagecleaner.ENUMERATIONS;

public class StatDataProcessorFactory {
	
	public static StatDataProcessor getStatDataProcessor(String s){
		switch(s){
		case "csv":
			return new StatDataProcessorSeparatorsCSV();
		case "sql":
			return new StatDataProcessorSeparatorsSQL();
		case "stat":
			return new StatDataProcessorLogs();
		case "block":
			return new StatDataProcessorBlocks();
		case ENUMERATIONS.FORMAT_TABLE:
			return new StatDataProcessorTest();		
		}
		return null;
	}

}
