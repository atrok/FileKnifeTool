package logprocessing;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineProcessingSimple implements LineProcessing{

	private Logger logger=LoggerFactory.getLogger(LineProcessingSimple.class);

	private int sampling=0;
	private StatisticManager sm;

	private Map result;
	public LineProcessingSimple(int sampling, StatisticManager sm){
		this.sampling = sampling;
		this.sm = sm;
		
	}
	@Override
	public void processLine(String ln, String[] params) {
		
		processLine(ln);
		
	}

	@Override
	public void processLine(String ln) {
		// TODO Auto-generated method stub
		String[] split_line;

		split_line=StringUtils.split(ln, ' ');

		try{
		Iterator<?> m = sm.getStatisticsList().iterator();
		
		while (m.hasNext()) {
			StatisticDefinition p=(StatisticDefinition) m.next();
			if (p.isMatched(ln)) 
				p.calculate(ln, split_line, null);
			
		}
		}catch(Exception e){
			System.out.println("Statistic Manager is likely empty: please add Statistic Definitions\n");
			logger.error(ln);
			logger.error("Statistic Manager is likely empty or one of statistics is defined incorrectly {}",e);
			//System.exit(0);
		}

	}

	@Override
	public void getReport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
