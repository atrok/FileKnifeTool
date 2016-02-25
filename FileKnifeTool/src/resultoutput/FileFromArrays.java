package resultoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import logprocessing.StatDataProcessor;

public class FileFromArrays extends AbstractFileOutput{

	
	public FileFromArrays(StatDataProcessor sdp, Path filename) {
		super(sdp, filename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void outputResult() {
		String[][] result=sdp.getResult();
		
		try {
			Path createdFile=Files.createFile(filename);
			BufferedWriter bf=Files.newBufferedWriter(createdFile, StandardOpenOption.WRITE);
			for(String[] s: result){
				String line="";
				if(null!=s){
					int i=0;
					for(String str: s){
						if(i==0)
							line=str;
						else
							line=line+", "+str;
						i++;
					}
					line=line+"\n";
					bf.write(line);
				}
				
				
			}
			bf.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
