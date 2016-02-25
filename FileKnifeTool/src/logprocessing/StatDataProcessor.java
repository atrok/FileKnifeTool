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

public abstract class StatDataProcessor {
	
	private Logger logger=LoggerFactory.getLogger(StatDataProcessor.class);
	protected Map statdata;
	//String[] sampled_timeframe;
	protected String[][] result;
	protected Record[] resultR;
	
	
	
	public abstract void process();
	
	public String[][] getResult(){
		return (result!=null)? result: null;
	}
	
	public Record[] getRecords(){
		return resultR;
	}


	public void load(Map statdata) {
		// TODO Auto-generated method stub
		this.statdata=statdata;
		process();
	}
}
