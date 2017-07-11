package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import garbagecleaner.ENUMERATIONS;
import logprocessing.AggregatingStatistic;
import logprocessing.DurationStatistic;
import logprocessing.IncrementalStatistic;
import logprocessing.LineProcessingLogs;
import logprocessing.LineProcessingSeparators;
import logprocessing.LineProcessingSimple;
import logprocessing.MaxStatistic;
import logprocessing.MinStatistic;
import logprocessing.StatDataProcessor;
import logprocessing.StatDataProcessorBlocks;
import logprocessing.StatDataProcessorFactory;
import logprocessing.StatDataProcessorLogs;
import logprocessing.StatDataProcessorSeparatorsCSV;
import logprocessing.StatisticDefinition;
import logprocessing.SumStatistic;
import resultoutput.FileFromArrays;
import resultoutput.FileFromRecords;
import statmanager.StatisticManager;
import statmanager.UnsupportedStatFormatException;

public class TestLineProcessing {

	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");// "Tests/resources/cs85.20151223_145909_388.log");
	Path scsfile = Paths.get("Tests/resources/AMR_US_NWK_SCS_P.20160521_045633_239.log");// "Tests/resources/cs85.20151223_145909_388.log");
	Path orsfile = Paths.get(
			"R:\\Apple\\ORS\\5thmayloadtest\\unpacked\\AMR_US_Aus_ORS01_B\\AMR_US_Aus_ORS01_B.20170505_100442_331.log");// "Tests/resources/cs85.20151223_145909_388.log");
	Path prnfile = Paths.get("Tests/resources/cs_performance_dec15-march16_memonly.prn");
	Path csvfile = Paths.get("Tests/resources/result_test.csv");
	Path lmsfile = Paths.get("Tests/resources/common.lms");
	Path sqlfile = Paths.get("Tests/resources/result_sql.sql");

	String timespot = "2015-09-18 10:07";

	static String[] linePatterns = new String[] { "Local time:       2014-09-16T13:14:58.465",
			"13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
			"13:16:54.215 Trc 04541 Message MSGCFG_GETOBJECTINFO received from 224 (CCView 'CCPulse_701')"

	};

	public void testLine() {
		LineProcessingLogs ln;
		try {
			ln = new LineProcessingLogs(1, StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT));
			for (String str : linePatterns) {
				ln.processLine(str);
			}
		} catch (UnsupportedStatFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testFile() {
		try {

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new IncrementalStatistic(".+Message.+(received from|sent to).+", "SentReceived"));
			sm.addStatistic(
					new IncrementalStatistic(".+Trc\\s24300.+\\sclient.+\\sconnected.+", "#New client connection"));
			sm.addStatistic(new IncrementalStatistic(".+Trc.+Client.+disconnected.+", "#Client disconnected"));
			sm.addStatistic(new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+",
					"##ObjectSent  $9 <1000"));
			// sm.addStatistic(new
			// IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
			sm.addStatistic(new AggregatingStatistic(".+Total number of clients.+", "#total clients", 5));

			LineProcessingLogs ln = new LineProcessingLogs(1, sm);

			Files.lines(file, StandardCharsets.ISO_8859_1).forEach(s -> ln.processLine(s));
			StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT).printStatData();

			// sm.printStatData();

			Map stats = sm.getStatDataMap();

			// int value=(int) ((HashMap)stats.get("#New client
			// connection")).get(timespot);

			assertTrue("Expected value of '#New client connection' statistic is 2",
					(double) ((HashMap) stats.get("#New client connection")).get(timespot) == 2);

			assertTrue("Expected value of 'SentReceived' statistic is 2852",
					(double) ((HashMap) stats.get("SentReceived")).get(timespot) == 2852);

			assertTrue("Expected value of '#total clients' statistic is 267",
					(double) ((HashMap) stats.get("#total clients")).get(timespot) == 267);

			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			sm.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testSCSFile() {
		try {
			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			// sm.addStatistic(new
			// IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
			sm.addStatistic(new IncrementalStatistic(".+GCTI-00-04502   Cannot connect.+", "$8"));

			LineProcessingLogs ln = new LineProcessingLogs(24, sm);

			Files.lines(scsfile, StandardCharsets.ISO_8859_1).forEach(s -> ln.processLine(s));
			StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT).printStatData();

			/*
			 * //sm.printStatData();
			 * 
			 * Map stats = sm.getStatDataMap();
			 * 
			 * 
			 * 
			 * //int value=(int) ((HashMap)stats.get("#New client connection"
			 * )).get(timespot);
			 * 
			 * assertTrue(
			 * "Expected value of '#New client connection' statistic is 2"
			 * ,(double) ((HashMap)stats.get("#New client connection"
			 * )).get(timespot)==2);
			 * 
			 * 
			 * assertTrue("Expected value of 'SentReceived' statistic is 2852"
			 * ,(double)
			 * ((HashMap)stats.get("SentReceived")).get(timespot)==2852);
			 * 
			 * 
			 * assertTrue("Expected value of '#total clients' statistic is 267"
			 * ,(double) ((HashMap)stats.get("#total clients"
			 * )).get(timespot)==267);
			 */
			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			sm.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testORSFile() {
		try {
			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			// sm.addStatistic(new
			// IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
			sm.addStatistic(new IncrementalStatistic(".+appl_begin.+", "appl_begin"));

			LineProcessingLogs ln = new LineProcessingLogs(24, sm);

			Files.lines(orsfile, StandardCharsets.ISO_8859_1).forEach(s -> ln.processLine(s));
			sm.printStatData();

			/*
			 * //sm.printStatData();
			 * 
			 * Map stats = sm.getStatDataMap();
			 * 
			 * 
			 * 
			 * //int value=(int) ((HashMap)stats.get("#New client connection"
			 * )).get(timespot);
			 * 
			 * assertTrue(
			 * "Expected value of '#New client connection' statistic is 2"
			 * ,(double) ((HashMap)stats.get("#New client connection"
			 * )).get(timespot)==2);
			 * 
			 * 
			 * assertTrue("Expected value of 'SentReceived' statistic is 2852"
			 * ,(double)
			 * ((HashMap)stats.get("SentReceived")).get(timespot)==2852);
			 * 
			 * 
			 * assertTrue("Expected value of '#total clients' statistic is 267"
			 * ,(double) ((HashMap)stats.get("#total clients"
			 * )).get(timespot)==267);
			 */
			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			sm.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testStatisticDefinitionwithVariableMsgName() {
		try {

			System.out.println("------------ Variable Name testing -------------");
			String[] lines = new String[] { "13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
					"15:00:10.448 Trc 24215 There are [187] objects of type [CfgApplication] sent to the client [608] (application [UR Server 750], type [RouterServer])",
					"15:00:11.107 Trc 24215 There are [108] objects of type [CfgDN] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"15:00:11.375 Trc 24215 There are [445] objects of type [CfgPerson] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"15:00:12.291 Trc 24215 There are [335] objects of type [CfgEnumeratorValue] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"09:56:19.456 Trc 04522 Client 2068 authorized, name 'Inbox_Uniclass_Prod', type 'InteractionWorkspace '"

			};

			String msgID = "Trc 24215 There are objects of type sent to the client";

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);
			IncrementalStatistic s = new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+",
					"##ObjectSent  $9 <1000");
			sm.addStatistic(s);
			sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+", "$msgID"));
			sm.addStatistic(new IncrementalStatistic(".+(received from|sent to).+", "SentReceived"));

			int sampling = 1;
			LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get("SentReceived")).get("2014-09-16 15:00");

			assertTrue("Expected value of SentReceived statistic is 4 but we got " + value, value == 4);

			String[] statnames = (String[]) ((HashMap) stats).keySet().toArray(new String[] {});

			assertTrue("Expected statname '" + msgID + "' is not found\n" + Arrays.toString(statnames),
					inArray(statnames, msgID));

			assertTrue("Expected statname 'Trc 04522 Client authorized name type' is not found\n"
					+ Arrays.toString(statnames), inArray(statnames, "Trc 04522 Client authorized name type"));

			assertTrue("Expected value of SentReceived statistic is 4 but we got " + value, value == 4);
			outputResult(sm);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testStatisticDefinition_MSGID_simple() {
		try {

			System.out.println("------------ Variable Name testing -------------");
			String[] lines = new String[] { "13:16:54.058 Trc 04120 Check point 2014-09-16T13:16:54",
					"15:00:10.448 Trc 24215 There are [187] objects of type [CfgApplication] sent to the client [608] (application [UR Server 750], type [RouterServer])",
					"15:00:11.107 Trc 24215 There are [108] objects of type [CfgDN] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"15:00:11.375 Trc 24215 There are [445] objects of type [CfgPerson] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"15:00:12.291 Trc 24215 There are [335] objects of type [CfgEnumeratorValue] sent to the client [608] (application [UR_Server_750], type [RouterServer])",
					"09:56:19.456 Trc 04522 Client 2068 authorized, name 'Inbox_Uniclass_Prod', type 'InteractionWorkspace '"

			};

			String msgID = "Trc 24215 There are objects of type sent to the client";

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);
			sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+", "$msgid"));

			int sampling = 1;
			LineProcessingSimple ln = new LineProcessingSimple(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get("Trc 04120 Check point")).get("simple");

			assertTrue("Expected value of SentReceived statistic is 1 but we got " + value, value == 1);

			outputResult(sm);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean inArray(String[] statnames, Object key) {

		Arrays.sort(statnames);
		return (Arrays.binarySearch(statnames, key) >= 0) ? true : false;
	}

	private void outputResult(StatisticManager sm) {

		StatDataProcessor sdp = new StatDataProcessorLogs();

		sdp.load(sm.getStatDataMap());
		FileFromRecords csv = new FileFromRecords(sdp, csvfile);

		csv.outputResult();

		// System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();

	}

	@Test
	public void testStatisticTrc24206Notification() {

		try {
			System.out.println("------------ Trc24206Notification Name testing -------------");
			String statname = "#Notification";

			String[] lines = new String[] { "Local time:       2016-02-04T21:55:32.104",
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
					"21:57:44.452 Trc 04542 Message MSGCFG_OBJECTCHANGED2 sent to 31 (ConfigurationServer 'APAC_JP_NRT_CSProxy01_B')" };

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new IncrementalStatistic(".+Trc.+24206.+Notification.+", statname));
			sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+", "$msgID"));
			sm.addStatistic(
					new IncrementalStatistic(".+Trc 24206 Notification : Object.+, name.+, DBID:.+is changed at server",
							"#Changed at server: $6"));
			sm.addStatistic(new IncrementalStatistic(".+Object.+name.+DBID:.+is changed by client.+",
					"#Changed by client: $18"));
			sm.addStatistic(new IncrementalStatistic(".+sent to the client.+", "#SentType: $18"));

			int sampling = 1;
			LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get(statname)).get("2015-09-18 21:57");

			assertTrue("Expected value of statistic is 7", value == 7);

			value = (double) ((HashMap) stats.get("#Changed by client: [agentmaster.api]")).get("2015-09-18 18:20");

			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testMaxMinSumStatistic() {

		try {
			System.out.println("------------ MaxMin Stat testing -------------");
			String statname = "#Notification";

			String[] lines = new String[] { "Local time:       2016-02-04T21:55:32.104",
					"21:57:06.351 Total number of clients: 90", "21:57:06.382 Total number of clients: 100",
					"21:57:16.397 Total number of clients: 200", "21:57:16.444 Total number of clients: 300" };
			String timestamp = "2016-02-04 21:57";
			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);
			String totalMax = "#totalMax";
			String totalMin = "#totalMin";
			String totalSum = "#totalSum";

			sm.addStatistic(new MaxStatistic(".+Total number of clients.+", "#totalMax", 5));
			sm.addStatistic(new MinStatistic(".+Total number of clients.+", "#totalMin", 5));
			sm.addStatistic(new SumStatistic(".+Total number of clients.+", "#totalSum", 5));

			int sampling = 1;
			LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get(totalMax)).get(timestamp);

			assertTrue("Expected value of " + totalMax + "statistic is 300", value == 300);

			value = (double) ((HashMap) stats.get(totalMin)).get(timestamp);

			assertTrue("Expected value of " + totalMin + "statistic is 90", value == 90);

			value = (double) ((HashMap) stats.get(totalSum)).get(timestamp);

			assertTrue("Expected value of " + totalSum + "statistic is 690", value == 690);

			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPrnFile() {

		try {

			double d1 = Double.valueOf("9598284.0");
			double d2 = Double.valueOf("9.577844E7");

			if (d1 != d2)
				System.out.println(d1 + " not equal to " + d2);

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new MaxStatistic(".+configservermemory.+", "#memory%", 2));

			LineProcessingLogs ln = new LineProcessingLogs(1, sm);

			Files.lines(prnfile, StandardCharsets.ISO_8859_1).forEach(s -> ln.processLine(s));
			sm.printStatData();

			// sm.printStatData();

			Map stats = sm.getStatDataMap();

			int value = (int) ((HashMap) stats.get("#New client connection")).get(timespot);

			assertTrue("Expected value of '#New client connection' statistic is 2", value == 2);

			value = (int) ((HashMap) stats.get("SentReceived")).get(timespot);

			assertTrue("Expected value of 'SentReceived' statistic is 2852", value == 2852);

			value = (int) ((HashMap) stats.get("#total clients")).get(timespot);

			assertTrue("Expected value of '#total clients' statistic is 267", value == 267);

			StatDataProcessor sdp = new StatDataProcessorLogs();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			sm.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testLMSfile() {

		LineProcessingSeparators ln = new LineProcessingSeparators('|');
		StatDataProcessor sdp = StatDataProcessorFactory.getStatDataProcessor("sql");

		try {

			Files.lines(lmsfile, Charset.defaultCharset()).forEach(s -> {
				ln.processLine(s, new String[] { lmsfile.getFileName().toString() });
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		sdp.load(ln.getData());
		FileFromRecords sql = new FileFromRecords(sdp, sqlfile);

		sql.outputResult();

	}

	@Test
	public void testDurationStatistic() {

		try {

			System.out.println("------------ Duration Statistic testing -------------");
			String statname = "#duration";

			String[] lines = new String[] { "Local time:       2017-05-05T06:40:08.819",
					"06:42:29.034 [T:140286721910528] {ScxmlMetric:1} METRIC <appl_begin sid='01E6030IE0CGP8GFLJMMG4DAES0000P5' />",
					"06:41:20.808 [T:140286679947008] {ScxmlMetric:1} METRIC <appl_begin sid='01E6030IE0CGP8GFLJMMG4DAES0000P1' />",
					"06:42:31.238 [T:140286730303232] {ScxmlMetric:1} METRIC <appl_end sid='01E6030IE0CGP8GFLJMMG4DAES0000P5' name='AC_SIP_Refer.AC_SIP_Refer' url='https://cctech-ors-amr.corp.apple.com/AC_SIP_Refer_1_2_2/src-gen/IPD_A C_SIP_Refer_AC_SIP_Refer.scxml' reason='normal' />",
					"06:41:23.974 [T:140286696732416] {ScxmlMetric:1} METRIC <appl_end sid='01E6030IE0CGP8GFLJMMG4DAES0000P1' name='RONA_RedirToQueue.RONA_RedirToQueue'" };

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new DurationStatistic(".+(appl_begin|appl_end).+", statname, "5"));

			int sampling = 0;
			LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get(statname)).get("sid='01E6030IE0CGP8GFLJMMG4DAES0000P5");

			assertTrue("Expected value of statistic is 2204, but we got " + value, value == 2204);

			StatDataProcessor sdp = new StatDataProcessorBlocks();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDurationStatistic_URS() {

		try {
			System.out.println("------------ Duration Statistic testing -------------");
			String statname = "#duration";

			String[] lines = new String[] { "Local time:       2017-05-05T06:40:08.819",
					"09:41:40.621 Int 20001 interaction 0075029c37150fe4 is started",
					"_I_I_0075029c37150fe4 [01:14] current call classification: media=voice(100), service=default(200), segment=default(300)",
					"09:41:40.621_I_I_0075029c37150fe4 [09:06] >>>>>>>>>>>>start interp()		};",
					"09:41:41.093 Int 20002 interaction 0075029c37150fe4 is routed to GrupoT_05@SS_Routing.GA" };
			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new DurationStatistic(".+(Int 20001|Int 20002).+", statname, "4"));

			int sampling = 0;
			LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get(statname)).get("0075029c37150fe4");

			assertTrue("Expected value of statistic is 472, but we got " + value, value == 472);

			StatDataProcessor sdp = new StatDataProcessorBlocks();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testLineProcessingSimple() {

		try {
			System.out.println("------------ LineProcessing Simple testing -------------");
			String statname = "$3"; // _I_I_0075029c371116ec [14:33] strategy:
									// *0x65*RP_90011_CMP_GrupoT_01 (2414814088)
									// is attached to the call
									// 0 1 2 3

			String[] lines = new String[] {
					"_I_I_0075029c371116ec [14:33] strategy: *0x65*RP_90011_CMP_GrupoT_01 (2414814088) is attached to the call",
					"_I_I_0075029c371115ee [14:33] strategy: *0x65*RP_90015_CMP_GrupoT_05_PruebaA (4257837752) is attached to the call"

			};

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new IncrementalStatistic(".+strategy:.+is attached to the call", statname));

			int sampling = 0;
			LineProcessingSimple ln = new LineProcessingSimple(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map<String, HashMap> stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get("*0x65*RP_90011_CMP_GrupoT_01")).get("simple");

			assertTrue("Expected value of statistic is 1, but we got " + value, value == 1);

			StatDataProcessor sdp = new StatDataProcessorBlocks();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testLineProcessingSimple_Numbers() {

		try {

			System.out.println("------------ LineProcessing Simple testing -------------");
			String statname = "$1"; // AttributeErrorCode 223
									// 0 1

			String[] lines = new String[] { " AttributeErrorCode      56",

			};

			StatisticManager sm = StatisticManager.getInstance(ENUMERATIONS.FORMAT_STAT);

			sm.addStatistic(new IncrementalStatistic(".+AttributeErrorCode.+", statname));

			int sampling = 0;
			LineProcessingSimple ln = new LineProcessingSimple(sampling, sm);

			for (String l : lines) {
				ln.processLine(l);
			}

			sm.printStatData();

			Map<String, HashMap> stats = sm.getStatDataMap();

			double value = (double) ((HashMap) stats.get("56")).get("simple");

			assertTrue("Expected value of statistic is 1, but we got " + value, value == 1);

			StatDataProcessor sdp = new StatDataProcessorBlocks();
			sdp.load(sm.getStatDataMap());
			FileFromRecords csv = new FileFromRecords(sdp, csvfile);

			csv.outputResult();

			// System.out.println(Arrays.toString(sdp.getResult()));
			sm.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDurationStatistic_ISCC() {
		
		System.out.println("------------ ISCC Duration Statistic testing -------------");
		String statname = "#duration";

		String[] lines = new String[] { "Local time:       2017-05-05T06:40:08.819",
				"@08:08:55.2410 [ISCC] Transaction [D56561818]: created [origin:AMR_US_Aus_SIP02_P@AMR_US_Aus_SIP02_Switch controller:AMR_US_Aus_SIP02_P@AMR_US_Aus_SIP02_Switch device:1016241 resource:]",
				"@08:09:45.9298 [ISCC] Transaction [D56561818]: deleted [origin:AMR_US_Aus_SIP02_P@AMR_US_Aus_SIP02_Switch controller:AMR_US_Aus_SIP02_P@AMR_US_Aus_SIP02_Switch device:1016241 resource:1082285]"
				};
		StatisticDefinition[] sd=new StatisticDefinition[]{
				new DurationStatistic(".+\\[ISCC\\] Transaction \\[D(\\d+)\\]: (created|deleted).+", statname, "1")
				};
		
		run(lines, ENUMERATIONS.FORMAT_STAT, 0, sd, statname, "56561818", 50688);
	}

	@Test
	public void testDurationStatistic_ISCC_acquired() {
		
		System.out.println("------------ ISCC Duration Statistic testing -------------");
		String statname = "#duration";

		String[] lines = new String[] { "Local time:       2017-05-05T06:40:08.819",
				"@08:05:11.5400 [ISCC] Resource 1082201: acquired by Transaction [D33333319]",
				"@08:05:11.5400 [ISCC] Resource 1082201: acquired by Transaction [D56689519]",
				"@08:05:11.5446 [ISCC] Resource 1082264: released by Transaction [D56689519]"
				
				};
		StatisticDefinition[] sd=new StatisticDefinition[]{
				new DurationStatistic(".+\\[ISCC\\]\\s+Resource.+(acquired|released).+\\[D(\\d+)\\]", statname, "2")
				};
		
		run(lines, ENUMERATIONS.FORMAT_STAT, 0, sd, statname, "56561818", 50688);
	}
	
	@Test
	public void testDurationStatistic_ISCC_acquired_improved() {
		
		System.out.println("------------ ISCC Duration Statistic testing with improved settings-------------");
		String statname = "#duration";

		String[] lines = new String[] { "Local time:       2017-05-05T06:40:08.819",
				"@08:05:11.5400 [ISCC] Resource 1082201: acquired by Transaction [D33333319]",
				"@08:05:11.5400 [ISCC] Resource 1082201: acquired by Transaction [D56689519]",
				"@08:05:11.5446 [ISCC] Resource 1082264: released by Transaction [D56689519]"
				
				};
		Map param=new HashMap<String,String>();
		param.put("startwith", ".+\\[ISCC\\]\\s+Resource.+acquired.+\\[D(\\d+)\\]" );
		param.put("endwith", ".+\\[ISCC\\]\\s+Resource.+released.+\\[D(\\d+)\\]" );
		param.put("field", "1" );

		DurationStatistic dur=new DurationStatistic(statname,param);
		StatisticDefinition[] sd=new StatisticDefinition[]{dur};
		
		run(lines, ENUMERATIONS.FORMAT_STAT, 0, sd, statname, "56561818", 50688);
	}
	
	private void run(String[] lines, String smtype, int sampling, StatisticDefinition[] statistics, String statname, String rowname, int expected_value){
		
		try{
			StatisticManager sm = StatisticManager.getInstance(smtype);
			
		for(int i=0;i<statistics.length;i++){
			sm.addStatistic(statistics[i]);
		}
		
		LineProcessingLogs ln = new LineProcessingLogs(sampling, sm);

		for (String l : lines) {
			ln.processLine(l);
		}

		sm.printStatData();

		Map stats = sm.getStatDataMap();

		double value = (double) ((HashMap) stats.get(statname)).get(rowname);

		assertTrue("Expected value of statistic is "+expected_value, value == expected_value);


		StatDataProcessor sdp = new StatDataProcessorLogs();
		sdp.load(sm.getStatDataMap());
		FileFromRecords csv = new FileFromRecords(sdp, csvfile);

		csv.outputResult();

		// System.out.println(Arrays.toString(sdp.getResult()));
		sm.flush();

	} catch (Exception e) {
		e.printStackTrace();
	}
		
		
	}

}
