package logprocessing;

import java.util.Map;
import java.util.Set;

import record.Header;
import record.Record;
import record.SimpleRecord;

public class StatDataProcessorSeparatorsCSV extends StatDataProcessor {


	@Override
	public void process() {
		Set<String[]> keys=statdata.keySet();
		result=keys.toArray(new String[0][]);
		
		resultR=new Record[result.length];
		
		for (int k=0;k<result.length;k++){
			SimpleRecord head=new SimpleRecord(result[k][0]);
			for (int i=1;i<result[k].length;i++)
				head.addValue(result[k][i]);
			resultR[k]=head;
		}
			
		
	}

}
