package logprocessing;

import java.util.HashMap;
import java.util.Set;

import record.Header;
import record.Record;

public class StatDataProcessorBlocks extends StatDataProcessor {

	@Override
	public void process() {

		Set<String> keys=statdata.keySet();
		
		Header topHeader=new Header("ID");
		
		int k=1;
		for(String o: keys){
			topHeader.addValue(o);
				HashMap h=(HashMap)statdata.get(o);
				Set<String> recordsID = h.keySet();
				if (resultR==null)
					resultR=new Record[recordsID.size()+1];

				for(String recordKEY: recordsID){
					
					Header record=(Header)resultR[k];
					
					if(record==null)
						record=new Header(recordKEY);
					
					record.addValue(h.get(recordKEY));
					resultR[k]=record;
					k++;
				}
				k=1;
				resultR[0]=topHeader;
		}
		

	}

}
