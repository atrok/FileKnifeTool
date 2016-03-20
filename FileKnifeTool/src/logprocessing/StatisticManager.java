package logprocessing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



public class StatisticManager {

	private List<StatisticDefinition> stats=new LinkedList<StatisticDefinition>();
		
	private static StatisticManager sm=new StatisticManager();
	
	private StatisticManager(){
		StatisticFactory.instance().registerProduct("IncrementalStatistic",IncrementalStatistic.class);
		StatisticFactory.instance().registerProduct("AggregatingStatistic",AggregatingStatistic.class);
		StatisticFactory.instance().registerProduct("MaxStatistic",MaxStatistic.class);
		StatisticFactory.instance().registerProduct("MinStatistic",MinStatistic.class);
		StatisticFactory.instance().registerProduct("SumStatistic",SumStatistic.class);
		

	}
	
	public static StatisticManager getInstance(){return sm;}
	
	public List getStatisticsList(){return stats.size()!=0 ? stats : null;} /// to make sure statistic definitions were put into statistic manager before passing it to LineProcessing class
	
	public Map<String, HashMap> getStatDataMap(){
		Iterator i=stats.iterator();
		
		Map map=new HashMap();
		while(i.hasNext()){
			Map<String, Map> t=((StatisticDefinition)i.next()).getStatistics();
			for (Map.Entry entry : t.entrySet()){
			     
			     Map<String,Integer> c=(Map<String, Integer>) entry.getValue();
			     
			     map.put(entry.getKey(), c);
			     
			}
			
			}
		
		return map;

		
	}
	
	public void addStatistic(StatisticDefinition stat){
		stats.add(stat);
	}
	
	public void printStatData(){
		Iterator i=stats.iterator();

		while(i.hasNext()){
			Map<String, Map> t=((StatisticDefinition)i.next()).getStatistics();
			for (Map.Entry entry : t.entrySet()){
			     System.out.print(entry.getKey()+"{");
			     Map<String,Integer> c=(Map<String, Integer>) entry.getValue();
			     
			     
			     for (Map.Entry e : c.entrySet()){
			    	 System.out.println(e.getKey()+" : "+e.getValue());
			     }
			     System.out.println("}");
			}
		}
	}
	
	public boolean flush(){
		return stats.removeAll(stats);
	}
}
