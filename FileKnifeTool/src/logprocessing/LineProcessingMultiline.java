package logprocessing;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import statmanager.StatisticManager;

public class LineProcessingMultiline extends LineProcessingSimple{

	private Logger logger=LoggerFactory.getLogger(LineProcessingMultiline.class);

	private StringBuilder sb=new StringBuilder();
		public LineProcessingMultiline(int sampling, StatisticManager sm) {
		super(sampling, sm);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void processLine(String ln) {
		// TODO Auto-generated method stub
		String[] split_line;
		
		String extended_ln=ln+"\t"+filename; //adding filename to pass into statistic for further processing/use if needed
		
		split_line=StringUtils.split(extended_ln, null);
		try{
		Iterator<?> m = sm.getStatisticsList().iterator();
		
		while (m.hasNext()) {
			StatisticDefinition p=(StatisticDefinition) m.next();
			if (p.isMatched(ln)) 
				p.calculate(ln, split_line, null);
			else{
				int t; 
				t=0;
			}
		}
		}catch(Exception e){
			//System.out.println("Statistic Manager is likely empty: please add Statistic Definitions\n");
			logger.error(ln);
			logger.error("Statistic Manager is likely empty or one of statistics is defined incorrectly {}",e);
			System.exit(0);
		}
	}
}
