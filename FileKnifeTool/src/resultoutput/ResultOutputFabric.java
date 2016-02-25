package resultoutput;

import java.nio.file.Path;
import java.util.Map;

import logprocessing.StatDataProcessor;

public class ResultOutputFabric {
	
	public static ResultOutput getOutputProcessor(Map<String,String> params,StatDataProcessor sdp, Path filename){
		switch (params.get("format")){
		case	"csv":
			return FileFabric.getOutputFileCreator(params.get("process"),sdp,filename);
		}
		return null;
	}
}
