package logprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import record.Header;
import record.Record;
import record.SimpleRecord;

public class StatDataProcessorBlocks extends StatDataProcessor {

	@Override
	public void process() {

		Set<String> keys=statdata.keySet();
		
		Header topHeader=new Header("ID");
		Set<Record> tempRecords=new TreeSet<Record>();
		tempRecords.add(topHeader);

		for(String statname: keys){
			topHeader.addValue(statname);
				@SuppressWarnings("unchecked")
				HashMap<String, ?> h=(HashMap<String, ?>)statdata.get(statname);
				Set<String> rowIDs = h.keySet();
				
			
				for(String recordKEY: rowIDs){
					
					SimpleRecord record=new SimpleRecord(recordKEY);
					tempRecords.add(record);
					
				}
				
				
		}
		
		for(String statname: keys){
			HashMap<String, ?> h=(HashMap<String, ?>)statdata.get(statname);
			Set<String> rowIDs = h.keySet();
			
			Iterator<Record> it=tempRecords.iterator();
			
			while(it.hasNext()){
				Object t1;
				SimpleRecord r;
				if((t1=it.next()) instanceof SimpleRecord)
					r=(SimpleRecord)t1;
				else r=null;
				
				if(r!=null){
				Object v=h.get(r.getZeroColumnName());
				//if (v!=null)
					r.addValue(v);
				//else r.addValue(0);
				
				//tempRecords.set(i, r);
			}
			}
		}
		
		if (resultR==null)
			resultR=new Record[tempRecords.size()];
		
		tempRecords.toArray(resultR);


	}

}
