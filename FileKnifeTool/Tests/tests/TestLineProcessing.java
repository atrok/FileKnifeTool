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
import logprocessing.LineProcessingLogs;
import logprocessing.MaxStatistic;
import logprocessing.MinStatistic;
import logprocessing.StatDataProcessor;
import logprocessing.StatDataProcessorLogs;
import logprocessing.StatisticManager;
import resultoutput.FileFromArrays;
import resultoutput.FileFromRecords;

public class TestLineProcessing {

	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");//"Tests/resources/cs85.20151223_145909_388.log");
	
	Path csvfile= Paths.get("Tests/resources/result_test.csv");
	
	String timespot="2015:09:18:10:07";
	
	static String[] linePatterns=new String[]{
			"Local time:       2014-09-16T13:14:58.465",
		"13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
		"13:16:54.215 Trc 04541 Message MSGCFG_GETOBJECTINFO received from 224 (CCView 'CCPulse_701')"

	};
	
	public void testLine() {
		LineProcessingLogs ln=new LineProcessingLogs(1, StatisticManager.getInstance());
		
		for(String str: linePatterns){
			ln.processLine(str);
		}
		
		
		
		
	}
	
	@Test
	public void testFile(){
		StatisticManager sm=StatisticManager.getInstance();

		sm.addStatistic(new IncrementalStatistic(".+Message.+(received from|sent to).+","SentReceived"));
		sm.addStatistic(new IncrementalStatistic(".+Trc.+client.+connected.+","#New client connection"));
		sm.addStatistic(new IncrementalStatistic(".+Trc.+client.+disconnected.+","#Client disconnected"));
		sm.addStatistic(new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+","##ObjectSent  $9 <1000"));
		sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
		sm.addStatistic(new AggregatingStatistic(".+Total number of clients.+","#total clients",5));

		LineProcessingLogs ln=new LineProcessingLogs(1, sm);
		
		try {
			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s->ln.processLine(s));
			StatisticManager.getInstance().printStatData();

		
		
		//sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get("#New client connection")).get(timespot);
		
		assertTrue("Expected value of '#New client connection' statistic is 2",value==2);
		
		value=(int) ((HashMap)stats.get("SentReceived")).get(timespot);
		
		assertTrue("Expected value of 'SentReceived' statistic is 2852",value==2852);
		
		value=(int) ((HashMap)stats.get("#total clients")).get(timespot);
		
		assertTrue("Expected value of '#total clients' statistic is 267",value==267);
				} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StatDataProcessor sdp=new StatDataProcessorLogs();
		sdp.load(sm.getStatDataMap());
		FileFromRecords csv=new FileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		sm.flush();

		
	}
	
	@Test
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
		LineProcessingLogs ln=new LineProcessingLogs(sampling, sm);
		
		for(String l: lines){
			ln.processLine(l);
		}
		
		sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get("SentReceived")).get("2014-09-16 15:00");
		
		assertTrue("Expected value of SentReceived statistic is 4",value==4);
		
		StatDataProcessor sdp=new StatDataProcessorLogs();
		
		sdp.load(sm.getStatDataMap());
		FileFromRecords csv=new FileFromRecords(sdp, csvfile);
		
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
				"14:55:29.782 [HISTORYDB]: Data record 40350086 has been written to history database",
				"2015-09-18T11:24:34.492 Std 24201 Object: [CfgAgent], name [004225868], DBID: [157503] is changed by client, type [InteractionWorkspace ], name: [Inbox_Uniclass_Prod], user: [004225868]",
				"2015-09-18T09:54:28.222 Trc 24215 There are [1] objects of type [CfgPerson] sent to the client [1492] (application [Inbox_Uniclass_Prod], type [InteractionWorkspace ])",
				"21:57:44.415 Trc 24206 Notification : Object [CfgAgent.Group], name [AC _Apple_Austin_VAG], DBID: [1114] is changed at server",
				"21:57:44.421 Trc 24206 Notification : Object [CfgAgentGroup], name [External agents with Internal skill], DBID: [3184] is changed at server",
				"21:57:44.427 Trc 24206 Notification : Object [CfgAgentGroup], name [AC_Apple_Austin_US_EN_iPhone_TS_VAG], DBID: [2109] is changed at server",
				"21:57:44.433 Trc 24206 Notification : Object [CfgAgentGroup], name [AC_Apple_Austin_US_EN_iPod_Touch_VAG], DBID: [2658] is changed at server",
				"21:57:44.440 Trc 24206 Notification : Object [CfgAgentGroup], name [IST_CCP_AC_Apple_Austin_VAG], DBID: [2955] is changed at server",
				"21:57:44.446 Trc 24206 Notification : Object [CfgAgentGroup], name [Agent Group], DBID: [3059] is changed at server",
				"21:57:44.452 Trc 24206 Notification : Object [CfgAgent Group], name [CAN iPhone at Austin], DBID: [3983] is changed at server",
				"18:20:07.571 Std 24201 Object: [CfgFolder], name [Exited Agents], DBID: [2422] is changed by client, type [SCE], name: [default], user: [agentmaster.api]",
				"21:57:44.452 Trc 24308 Message MSGCFG_OBJECTCHANGED2 (0x2aabe1a7f370) generated",
				"21:57:44.452 Trc 04542 Message MSGCFG_OBJECTCHANGED2 (0x2aaab89e5d50) sent to 30 (SCE 'default')",
				"21:57:44.452 Trc 04542 Message MSGCFG_OBJECTCHANGED2 sent to 31 (ConfigurationServer 'APAC_JP_NRT_CSProxy01_B')"
				};
		
		StatisticManager sm=StatisticManager.getInstance();
		
		sm.addStatistic(new IncrementalStatistic(".+Trc.+24206.+Notification.+",statname));
		sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
		sm.addStatistic(new IncrementalStatistic(".+Trc 24206 Notification : Object.+, name.+, DBID:.+is changed at server","#Changed at server: $6"));
		sm.addStatistic(new IncrementalStatistic(".+Object.+name.+DBID:.+is changed by client.+","#Changed by client: $18"));
		sm.addStatistic(new IncrementalStatistic(".+sent to the client.+","#SentType: $18"));
		
		
		int sampling =1;
		LineProcessingLogs ln=new LineProcessingLogs(sampling, sm);
		
		for(String l: lines){
			ln.processLine(l);
		}
		
		sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get(statname)).get("2015-09-18 21:57");
		
		assertTrue("Expected value of statistic is 7",value==7);
		
		value=(int) ((HashMap)stats.get("#Changed by client: [agentmaster.api]")).get("2015-09-18 18:20");
		
		StatDataProcessor sdp=new StatDataProcessorLogs();
		sdp.load(sm.getStatDataMap());
		FileFromRecords csv=new FileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		//System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();
		
	}
	
	@Test
	public void testMaxMinStatistic(){
		System.out.println("------------ MaxMin Stat testing -------------");
		String statname="#Notification";
		
		String[] lines=new String[]{
				"Local time:       2016-02-04T21:55:32.104",
				"21:57:06.351 Total number of clients: 90",
				"21:57:06.382 Total number of clients: 100",
				"21:57:16.397 Total number of clients: 200",
				"21:57:16.444 Total number of clients: 300"
				};
		String timestamp="2016-02-04 21:57";
		StatisticManager sm=StatisticManager.getInstance();
		String totalMax="#totalMax";
		String totalMin="#totalMin";
		
		sm.addStatistic(new MaxStatistic(".+Total number of clients.+","#totalMax",5));
		sm.addStatistic(new MinStatistic(".+Total number of clients.+","#totalMin",5));
		
		
		int sampling =1;
		LineProcessingLogs ln=new LineProcessingLogs(sampling, sm);
		
		for(String l: lines){
			ln.processLine(l);
		}
		
		sm.printStatData();
		
		Map stats = sm.getStatDataMap();
		
		
		
		int value=(int) ((HashMap)stats.get(totalMax)).get(timestamp);
		
		assertTrue("Expected value of "+totalMax+"statistic is 300",value==300);
		
		value=(int) ((HashMap)stats.get(totalMin)).get(timestamp);
		
		assertTrue("Expected value of "+totalMin+"statistic is 90",value==90);
		
		StatDataProcessor sdp=new StatDataProcessorLogs();
		sdp.load(sm.getStatDataMap());
		FileFromRecords csv=new FileFromRecords(sdp, csvfile);
		
		csv.outputResult();
		
		//System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();
		
	}

}
