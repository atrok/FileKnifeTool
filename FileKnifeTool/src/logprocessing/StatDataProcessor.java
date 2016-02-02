package logprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import record.Header;
import record.Record;
import record.TimeStamp;

public class StatDataProcessor {
	
	private Logger logger=LoggerFactory.getLogger(StatDataProcessor.class);
	private Map statdata;
	//String[] sampled_timeframe;
	String[][] result;
	Record[] resultR;
	
	public StatDataProcessor(Map statdata){
		this.statdata=statdata;
		process();
	}

	private void process(){
		
		logger.debug("Collected statdata processing has started");
		
		Set<String> messages=statdata.keySet();
		//String[] header=new String[messages.size()+1];
		//header[0]="Time";
		Header head=new Header("Time");//#
		
		Iterator it=messages.iterator();
		
		for(int i=1;i<messages.size()+1;i++){
			//header[i]=(String)it.next();
			head.addValue((String)it.next());//#
		}

		int headerSize=head.getValues().size()+1;
		
		Map[] data=(Map[]) statdata.values().toArray(new Map[0]);
		
		Set timestamps=new HashSet();
		// we need to determine the list of unique timestamps which to be added to recordR
		for(Map m: data)
			timestamps.addAll(m.keySet());
					
		//it=timestamps.iterator();
		String[] t=(String[])timestamps.toArray(new String[0]);
		
		Arrays.sort(t);
		//sampled_timeframe=new String[data.length];
		//result=new String[data.length+1][];
		resultR=new Record[timestamps.size()+1];// because of header
		
		//result[0]=header;
		
		resultR[0]=head;//#
		
		int a=1;		
		for(int i=1;i<resultR.length;i++){/// we start from 1 because at i=0 there is Time column. However all other arrays starts from 0 so we introduce t_ind
			int t_ind=i-1;
			//String[] row=new String[head.getValues().size()];
			//result[i]=row;
			
			logger.debug("timestamps.length={}, timestamp: {}",timestamps.size(),t[t_ind]);
			
			//sampled_timeframe[i-1]=t[0];
			//row[0]=t[0];
			
			//logger.trace("sampled timeframe: {}",t[t_ind]);
			
			TimeStamp tstamp=new TimeStamp(t[t_ind]); //#
			if (null==resultR[a])
				resultR[a]=tstamp;//#
			Arrays.sort(resultR,1,a);//#
			int ind=Arrays.binarySearch(resultR, 1, a, tstamp);//#
			if (ind>=0){//#
				tstamp=(TimeStamp)resultR[ind];//#
				resultR[a]=null;
			}
			else
				a++;//resultR[i]=tstamp;
				
			
			List t_values=tstamp.getValues();//#
			
			for(int k=1;k<headerSize;k++){
				Map m=(HashMap)statdata.get(head.getValues().get(k-1));
				if (!m.containsKey(t[t_ind])){
					//row[k]="0";
					
					if(t_values.size()>=k){
						if(t_values.get(k-1)==null)//# to retain previous value
							tstamp.putValue(k-1,0);//# we can also use t_values.add(ind,v)
					}else tstamp.putValue(k-1,0);//#
					
				}else{
					//row[k]=Integer.toString((int) m.get(t[0]));
					if(t_values.size()<k)//#
						tstamp.putValue(k-1,m.get(t[t_ind]));//#
				}
			}
		}
		// this part is to be removed once Record type is implemented properly
/*		for(int i=1;i<result.length;i++){
			
			if (result[i]==null)
				break;
			
			for(int k=i+1;k<result.length;k++){
				
				if(result[k]!=null){
					if(result[k][0].equals(result[i][0])){
						
						for(int p=1;p<result[i].length;p++){
							if (null!=result[k][p]&& null==result[i][p])
								result[i][p]=result[k][p];
								
						}
						
						result[k]=null;
							
					}
				}
					
					
			}
		}
*/		
		@SuppressWarnings("unused")
		int m=0;
		
	}
	
	public String[][] getResult(){
		return (result!=null)? result: null;
	}
	
	public Record[] getRecords(){
		return resultR;
	}
}
