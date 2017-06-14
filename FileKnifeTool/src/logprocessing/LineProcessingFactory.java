package logprocessing;

import garbagecleaner.ENUMERATIONS;
import statmanager.StatisticManager;

public class LineProcessingFactory {
	
	public static LineProcessing getInstance(String code, int sampling,StatisticManager sm){
		
		switch(code){
		
		case ENUMERATIONS.ProcessorTIME: return new LineProcessingLogs(sampling,sm);
		case ENUMERATIONS.ProcessorSIMPLE: return new LineProcessingSimple(sampling,sm);
		default: return null;
		}
	}

}
