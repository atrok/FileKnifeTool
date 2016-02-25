package logprocessing;

public class StatDataProcessorFactory {
	
	public static StatDataProcessor getStatDataProcessor(String s){
		switch(s){
		case "csv":
			return new StatDataProcessorSeparatorsCSV();
		case "sql":
			return new StatDataProcessorSeparatorsSQL();
		case "stat":
			return new StatDataProcessorLogs();
				
		}
		return null;
	}

}
