package logprocessing;

import garbagecleaner.ENUMERATIONS;

public class StatDataProcessorFactory {
	
	public static StatDataProcessor getStatDataProcessor(String s){
		switch(s){
		case ENUMERATIONS.FORMAT_CSV:
			return new StatDataProcessorSeparatorsCSV();
		case ENUMERATIONS.FORMAT_SQL:
			return new StatDataProcessorSeparatorsSQL();
		case ENUMERATIONS.FORMAT_STAT:
			return new StatDataProcessorLogs();
		case ENUMERATIONS.FORMAT_BLOCK:
			return new StatDataProcessorBlocks();
		case ENUMERATIONS.FORMAT_TABLE:
			return new StatDataProcessorTest();		
		}
		return null;
	}

}
