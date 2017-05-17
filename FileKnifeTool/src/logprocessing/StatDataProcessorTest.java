package logprocessing;

import java.util.HashMap;
import java.util.Set;

import garbagecleaner.ENUMERATIONS;
import record.Header;
import record.Record;
import record.SimpleRecord;

public class StatDataProcessorTest extends StatDataProcessor {

	@Override
	public void process() {
		// 
			Set<String> stats=statdata.keySet();
			
			Header topHeader=new Header(ENUMERATIONS.COLUMN_ID);
			
			int k=1;
			for(String statName: stats){
				
					HashMap keyvalue_pairs=(HashMap)statdata.get(statName);
					if (resultR==null)
						resultR=new Record[stats.size()+1];
					
					SimpleRecord record=(SimpleRecord)resultR[k];
					
					if(record==null){
						record=new SimpleRecord(statName);
						
					}
					
					
					Set<String> keys = keyvalue_pairs.keySet();

					for(String recordKEY: keys){
						
						topHeader.addValue(recordKEY);
						record.addValue(keyvalue_pairs.get(recordKEY));
					}
					
					resultR[0]=topHeader;
					resultR[k]=record;
					k++;
			}
			

		}

	

}
