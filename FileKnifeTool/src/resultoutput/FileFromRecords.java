package resultoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import logprocessing.StatDataProcessor;
import record.Record;

public class FileFromRecords extends AbstractFileOutput{

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
			e.printStackTrace();
		}

		
	}

}
