package resultoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import logprocessing.StatDataProcessor;
import logprocessing.StatisticDefinition;
import record.Record;

public class FileFromRecords extends AbstractFileOutput{

	private Logger logger=LoggerFactory.getLogger(FileFromRecords.class);

	public FileFromRecords(StatDataProcessor sdp, Path filename) {
		super(sdp, filename);
		// TODO Auto-generated constructor stub
	}
	
	public void outputResult() {
		Record[] result=sdp.getRecords();
		try {
			//Path createdFile=Files.createFile(filename);
			
			BufferedWriter bf=Files.newBufferedWriter(filename, StandardCharsets.UTF_8);
			for(Record s: result){
				///String line="";
				if(null!=s){
					bf.write(s.toString());
				}
				
				
			}
			bf.close();
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Can't write the result output",e);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error("Result output is likely empty, check your regular expression",e);
		}

		
	}

}
