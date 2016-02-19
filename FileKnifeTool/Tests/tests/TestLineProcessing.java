package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import logprocessing.AggregatingStatistic;
import logprocessing.IncrementalStatistic;
import logprocessing.LineProcessing;
import logprocessing.StatDataProcessor;
import logprocessing.StatisticManager;
import resultoutput.CSVFile;
import resultoutput.CSVFileFromRecords;

public class TestLineProcessing {

	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");//"Tests/resources/cs85.20151223_145909_388.log");
	
	Path csvfile= Paths.get("Tests/resources/result_test.csv");
	
	
	static String[] linePatterns=new String[]{
			"Local time:       2014-09-16T13:14:58.465",
		"13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
		"13:16:54.215 Trc 04541 Message MSGCFG_GETOBJECTINFO received from 224 (CCView 'CCPulse_701')"

	};
	
	public void testLine() {
		LineProcessing ln=new LineProcessing(1, StatisticManager.getInstance());
		
		for(String str: linePatterns){
			ln.processLine(str);
		}
		
		
		
		
	}
	
	
	public void testFile(){
		StatisticManager sm=StatisticManager.getInstance();

		sm.addStatistic(new IncrementalStatistic(".+Message.+(received from|sent to).+","SentReceived"));
		sm.addStatistic(new IncrementalStatistic(".+Trc.+client.+connected.+","#New client connection"));
		sm.addStatistic(new IncrementalStatistic(".+Trc.+client.+disconnected.+","#Client disconnected"));
		sm.addStatistic(new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+","##ObjectSent  $9 <1000"));
		sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
		sm.addStatistic(new AggregatingStatistic(".+Total number of clients.+","#total clients",5));

		LineProcessing ln=new LineProcessing(1, sm);
		
		try {
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->ln.processLine(s));
			StatisticManager.getInstance().printStatData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get("#New client connection")).get("2015:12:23:14:59");
		
		assertTrue("Expected value of '#New client connection' statistic is 19",value==19);
		
		value=(int) ((HashMap)stats.get("#New client connection")).get("2015:12:23:15:16");
		
		assertTrue("Expected value of '#New client connection' statistic is 20",value==20);
		
		value=(int) ((HashMap)stats.get("#total clients")).get("2015:12:23:14:59");
		
		assertTrue("Expected value of '#total clients' statistic is 4",value==4);
		
		StatDataProcessor sdp=new StatDataProcessor(sm.getStatDataMap());

		CSVFileFromRecords csv=new CSVFileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		sm.flush();

		
	}
	
	
	public void testStatisticDefinitionwithVariableMsgName(){
		System.out.println("------------ Variable Name testing -------------");
		String[] lines=new String[]{
			"13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
			"15:00:10.448 Trc 24215 There are [187] objects of type [CfgApplication] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
			"15:00:11.107 Trc 24215 There are [108] objects of type [CfgDN] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
			"15:00:11.375 Trc 24215 There are [445] objects of type [CfgPerson] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
			"15:00:12.291 Trc 24215 There are [335] objects of type [CfgEnumeratorValue] sent to the client [608] (application [UR_Server_750], type [RouterServer])"
		};
		
		StatisticManager sm=StatisticManager.getInstance();
		IncrementalStatistic s=new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+","##ObjectSent  $9 <1000");
		sm.addStatistic(s);
		sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
		sm.addStatistic(new IncrementalStatistic(".+(received from|sent to).+","SentReceived"));
		
		int sampling =1;
		LineProcessing ln=new LineProcessing(sampling, sm);
		
		for(String l: lines){
			ln.processLine(l);
		}
		
		sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get("SentReceived")).get("2014:09:16:15:00");
		
		assertTrue("Expected value of SentReceived statistic is 4",value==4);
		
		StatDataProcessor sdp=new StatDataProcessor(sm.getStatDataMap());
		
		CSVFileFromRecords csv=new CSVFileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		//System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();
		
	}

	@Test
	public void testStatisticTrc24206Notification(){
		System.out.println("------------ Trc24206Notification Name testing -------------");
		String statname="#Notification";
		
		String[] lines=new String[]{
				"Local time:       2016-02-04T21:55:32.104",
				"21:57:44.415 Trc 24206 Notification : Object [CfgAgentGroup], name [AC_Apple_Austin_VAG], DBID: [1114] is changed at server",
				"21:57:44.421 Trc 24206 Notification : Object [CfgAgentGroup], name [External agents with Internal skill], DBID: [3184] is changed at server",
				"21:57:44.427 Trc 24206 Notification : Object [CfgAgentGroup], name [AC_Apple_Austin_US_EN_iPhone_TS_VAG], DBID: [2109] is changed at server",
				"21:57:44.433 Trc 24206 Notification : Object [CfgAgentGroup], name [AC_Apple_Austin_US_EN_iPod_Touch_VAG], DBID: [2658] is changed at server",
				"21:57:44.440 Trc 24206 Notification : Object [CfgAgentGroup], name [IST_CCP_AC_Apple_Austin_VAG], DBID: [2955] is changed at server",
				"21:57:44.446 Trc 24206 Notification : Object [CfgAgentGroup], name [Agent Group], DBID: [3059] is changed at server",
				"21:57:44.452 Trc 24206 Notification : Object [CfgAgentGroup], name [CAN iPhone at Austin], DBID: [3983] is changed at server",
				"21:57:44.452 Trc 24308 Message MSGCFG_OBJECTCHANGED2 (0x2aabe1a7f370) generated",
				"21:57:44.452 Trc 04542 Message MSGCFG_OBJECTCHANGED2 (0x2aaab89e5d50) sent to 30 (SCE 'default')",
				"21:57:44.452 Trc 04542 Message MSGCFG_OBJECTCHANGED2 sent to 31 (ConfigurationServer 'APAC_JP_NRT_CSProxy01_B')"
				};
		
		StatisticManager sm=StatisticManager.getInstance();
		IncrementalStatistic s=new IncrementalStatistic(".+Trc.+24206.+Notification.+",statname);
		sm.addStatistic(s);
		sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));

		
		int sampling =1;
		LineProcessing ln=new LineProcessing(sampling, sm);
		
		for(String l: lines){
			ln.processLine(l);
		}
		
		sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get(statname)).get("2016:02:04:21:57");
		
		assertTrue("Expected value of statistic is 7",value==7);
		
		StatDataProcessor sdp=new StatDataProcessor(sm.getStatDataMap());
		
		CSVFileFromRecords csv=new CSVFileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		//System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();
		
	}
	
}
