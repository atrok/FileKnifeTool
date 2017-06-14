package statmanager;

import java.util.Map;

import logprocessing.StatisticDefinition;
import logprocessing.StatisticParamNaming;

public class StatisticManagerSTAT extends StatisticManager {

	@Override
	public boolean verifyStatistic(StatisticDefinition stat) throws UnsupportedStatParamException {
		// TODO Auto-generated method stub
		Map<String,String> parameters=stat.getStatParameters();
		
		if(parameters.containsKey(StatisticParamNaming.ROWNAME.toString()))
			throw new UnsupportedStatParamException(StatisticParamNaming.ROWNAME+" is not supported with format STAT");
		return true;
	}

}
