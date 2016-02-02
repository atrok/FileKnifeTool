package resultoutput;

import java.nio.file.Path;

import logprocessing.StatDataProcessor;

public class ResultOutputFabric {
	
	public static ResultOutput getOutputProcessor(String processor_name,StatDataProcessor sdp, Path filename){
		switch (processor_name){
		case	"csv":
			return new CSVFileFromRecords(sdp,filename);
		}
		return null;
	}
}
