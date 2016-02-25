package resultoutput;

import java.nio.file.Path;

import logprocessing.StatDataProcessor;

public class FileFabric {
	
	public static ResultOutput getOutputFileCreator(String processor_name,StatDataProcessor sdp, Path filename){
		switch (processor_name){
		case	"record":
			return new FileFromRecords(sdp,filename);

		case	"simple":
			return new FileFromArrays(sdp,filename);
			
		}
		return null;
	}
}
