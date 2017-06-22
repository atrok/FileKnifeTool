package tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import cmdline.CmdLineParser;
import cmdline.Command;
import cmdline.CommandImpl;
import cmdline.CommandParse;
import garbagecleaner.ProcessFilesFabric;
import util.FilesUtil;

public class TestCommandParse {


	Path start = Paths.get("Tests/resources/");
	Path resources = Paths.get("target/_temp/resources");
	
	String garbagecleaner_logs="D:\\Share\\distrib\\3dparty\\GarbageCleaner\\logs";
	String default_logs=start.toAbsolutePath().toString();
	
	@Test
	public void testCmdParseSample10(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "config_proxy_person_cto_p_all.+.log",
						"-sample","10",
						null,null
						},
				1, // found files
				"2015-09-18 09:50:00.000,32.0,16.0,16.0,16.0,173.0,3631.0,1528.0,16226.0,156.0,0,0,0,0,1.0,0,0,0,0,1.0,0,0,1.0,0,0,0,1.0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0,1.0,0,0,0,0,1.0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,354563.0,7.0,5363.0,62640.0,24448.0,259616.0,2496.0,246.0,7.0,7575.0,7.0,7887.0,13671.0,21558.0",
				0);
	}

	@Test(expected = AssertionError.class) // it's not supported for 'genesys' mode 
	public void testCmdParse_Format_CSV(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "config_proxy_person_cto_p_all.+.log",
						"-sample","10",
						"-format","csv", /// it's not supported for 'genesys' mode
						null,null
						},
				1, // found files
				"2015-09-18 09:50,31.0,18.0,21.0,0,16.0,3.0,0,16.0,16.0,16.0,16.0,29.0,173.0,1.0,1.0,3631.0,33.0,1.0,1.0,1528.0,0.0,16226.0,1.0,65.0,156.0,1.0,1.0,1.0,0,0,0,0,1.0,0,0,0,0,1.0,0,0,1.0,0,0,0,1.0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0,1.0,0,0,0,0,1.0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,364910.0,7.0,435.0,5456.0,15.0,280.0,62695.0,1895.0,80.0,122.0,30296.0,0.0,259616.0,16.0,1290.0,2496.0,173.0,16.0,29.0,246.0,7.0,7575.0,7.0,7887.0,13671.0,21558.0",
				0);
		

	}


	/*
	 * 
	 * 
	 No timestamp header, so "-format logs" is not supported and will not produce any results
	 
	    _I_I_0075029c3714e8ad [14:33] strategy: *0x65*RP_90015_CMP_GrupoT_05_PruebaA (4257837752) is attached to the call
	    
	urs.properties.ini
	[$3]
	stattype=IncrementalStatistic
	regexp=.+strategy:.+is attached to the call
	 */
	
	@Test
	public void testCmdParse_Format_Block_Processor_Simple(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.properties.ini",
						"-format","block", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"simple,3.0,2.0,14.0,176.0,1.0,1.0,21.0",
				0);
		

	}


	
	@Test
	public void testCmdParse_Processor_Simple_Format_Table(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"*0x65*RP_90016_CMP_GrupoT_06,3.0",
				0);
		

	}

	@Test
	public void testCmdParse_Processor_Simple_Format_Table_MSGID(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","msgid.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"Int 20002 interaction is routed to,211.0",
				0);
		

	}
	
	@Test
	public void testCmdParse_Processor_Simple_Format_Table_FILENAME(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","filename.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				//"URS_tab_delimeted.log,29.0");
				"URS.20170420_080950_004.log,637389.0",
				0);
		

	}
	@Test
	public void testCmdParse_Processor_Simple_Format_Table_NumberAsName(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.errors.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"TLIB Error Bad parameter passed to function,1.0",
				0);
		

	}
	
	@Test
	public void testCmdParse_Format_Block_AggregatingFieldGroupName_MaxStatistic(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "AMR_US_Aus_ORS01_B.20170505_064008_819.log",
						"-statfile","ors.doc_duration.properties.ini",
						"-format","block", 
						null,null
						},
				1, // found files
				"01E6030IE0CGP8GFLJMMG4DAES0000P1,2017-05-05 06:41:20.808,2017-05-05 06:41:23.974,722.0,3166.0",
				0);
		
	}
	
	@Test
	public void testCmdParse_Process_Simple_Format_Block(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.properties.ini",
						"-format","block", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"simple,3.0,2.0,14.0,176.0,1.0,1.0,21.0");
		

	}
	
	@Test
	public void testCmdParse_Process_Simple_Format_Block_IncrementingStat_with_rowname(){
	
		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "config_proxy_person_cto_p_all.20150918_095313_510.log ",
						"-sample","0",
						"-statfile","ixn.clients.properties.ini",
						"-format","block", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"config_proxy_person_cto_p_all.20150918_095313_510.log,2.0,3.0,1.0,1.0,1.0,2.0,1.0,1.0,2.0,1.0,5.0,1.0,3.0,2.0,3.0,2.0,1.0,1.0,1.0,1.0,5.0,1.0,1.0,2.0,1.0,1.0,3.0,1.0,5.0,2.0,1.0,2.0,2.0,1.0,4.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,3.0,2.0,1.0,1.0,1.0,1.0,3.0,1.0,1.0,1.0,2.0,2.0,1.0,1.0,4.0,1.0,2.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,1.0,1.0,2.0,4.0,1.0,1.0,2.0,2.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,3.0,1.0,2.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,1.0,4.0,1.0,2.0,2.0,1.0,1.0,5.0,1.0,4.0,2.0,2.0,1.0,2.0,1.0,3.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,4.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,2.0,1.0,2.0,3.0,1.0,1.0,1.0,1.0,4.0,2.0,3.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,4.0,1.0,1.0,1.0,1.0,1.0,1.0,3.0,2.0,4.0,1.0,2.0,1.0,1.0,1.0,2.0,2.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,2.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,2.0,1.0,1.0,3.0,1.0,1.0,1.0,3.0,1.0,4.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,3.0,2.0,1.0,1.0,1.0",
				2 // result_length
				);
		

	}
	
	@Test
	public void testCmdParse_Format_Block(){

		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "AMR_US_Aus_ORS01_B.20170505_064008_819.log",
						"-sample","0",
						"-statfile","ors.duration.properties.ini",
						"-format","block", 
						null,null
						},
				1, // found files
				"01E6030IE0CGP8GFLJMMG4DAES0000P5,2204.0,2017-05-05 06:42:29.034,2017-05-05 06:42:31.238");
		

	}

	
	@Test
	public void testCmdParse_Format_Block_Duration(){

		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "duration.log",
						"-sample","0",
						"-statfile","ors.duration.properties.ini",
						"-format","block", 
						null,null
						},
				1, // found files
				"01E6030IE0CGP8GFLJMMG4DAES0000P5,2204.0,2017-05-05 06:42:29.034,2017-05-05 06:42:31.238");
		

	}

	
	
	/*
	 * test to find filiename duration
	 *  
	 */
		@Test
	public void testCmdParse_Format_Block_Duration_Filename_Field(){

		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","filename.duration.properties.ini",
						"-format","block",
						"-processor","time",
						null,null
						},
				1, // found files
				"URS.20170420_080950_004.log,2017-04-20 08:09:50.004,5510845.0,2017-04-20 09:41:40.849");
		

	}

	
	@Test
	public void testCmdParse_Format_Block_URS_Log(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs,  
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.duration.properties.ini",
						"-format","block", 
						null,null
						},
				1, // found files
				"0075029c37105c68,87.0,2017-04-20 08:09:55.890,2017-04-20 08:09:55.977");
		

	}
	

	/*
	 * 
	 * 
	 ++++ Test with custom column name ++++
	 
	 No timestamp header, so "-format logs" is not supported and will not produce any results
	 
	    _I_I_0075029c3714e8ad [14:33] strategy: *0x65*RP_90015_CMP_GrupoT_05_PruebaA (4257837752) is attached to the call
	    
	urs.properties.ini
	[$3]
	stattype=IncrementalStatistic
	regexp=.+strategy:.+is attached to the call
	column=value
	 */
	
	@Test
	public void testCmdParse_Format_Table_Processor_Simple_Column(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.column.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"ID,value");
		

	}
	
	/*
	 * 
	 * 
	 ++++ Test with custom column name ++++
	 
	 No timestamp header, so "-format logs" is not supported and will not produce any results
	 
	    _I_I_0075029c3714e8ad [14:33] strategy: *0x65*RP_90015_CMP_GrupoT_05_PruebaA (4257837752) is attached to the call
	    
	urs.properties.ini
	[$3]
	stattype=IncrementalStatistic
	regexp=.+strategy:.+is attached to the call
	column=value
	 */
	
	@Test
	public void testCmdParse_Format_Block_Processor_Simple_Column(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-sample","0",
						"-statfile","urs.column.properties.ini",
						"-format","block", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"value,3.0,2.0,14.0,176.0,1.0,1.0,21.0");
		

	}
	
	/*
	 * 
	 * 
	 ++++ Test with group name + column name ++++
	 
[duration]
stattype=SumStatistic
regexp=^\\s+AttributeUserData\\s+\\[([0-9]+)\\]\\s+.+
field=1
rowname=filename

	 */
	
	@Test
	public void testCmdParse_Format_Table_Processor_Simple_AggregatingFieldGroupName_SumStatistic(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-statfile","urs.aggr_group.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"userdata bytes,3709342.0");
		

	}

	/*
	 * 
	 * 
	 ++++ Test with group name + column name ++++
	 


	 */
	
	@Test
	public void testCmdParse_Format_Table_Processor_Simple_AggregatingFieldGroupName_combined(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.+.log",
						"-statfile","filename.combined_duration.properties.ini",
						"-format","block", 
						"-processor","time",
						"-sample","0",
						null,null
						},
				2, // found files
				"URS.20170420_080950_004.log,3709342.0,415930.0,2017-04-20 08:09:50.004,5510845.0,2017-04-20 09:41:40.849,637389.0");
		

	}
	/*
	 * 
	 * 
	 ++++ Test with group name + column name ++++
	 
	 No timestamp header, so "-format logs" is not supported and will not produce any results
	 
	    _I_I_0075029c3714e8ad [14:33] strategy: *0x65*RP_90015_CMP_GrupoT_05_PruebaA (4257837752) is attached to the call
	    
	urs.groups.properties.ini
	[$1]
	stattype=IncrementalStatistic
	regexp=.+strategy:\\s+*[\\dx]*(\\w)\\s+.+is attached to the call
	column=value
	 */
	
	@Test
	public void testCmdParse_Format_Table_Processor_Simple_Column_GroupName(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS.20170420_080950_004.log",
						"-statfile","urs.groups.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"Strategy RP_80007_Atencion_Bancoppel,2.0");
		

	}

	/*
	 * urs.errors.properties.ini
	 * [$1]
	 * stattype=IncrementalStatistic
	 * regexp=.+AttributeErrorMessage.+'([\\d\\w\\s]+)'
	 * 
	 */
	@Test
	public void testCmdParse_Format_Table_Processor_Simple_Column_GroupName_EventError(){
	


		parse(
				new String[]{
						"genesys", 
						"-d", default_logs, 
						"-ext", "URS_tab_delimeted.log",
						"-statfile","urs.errors.properties.ini",
						"-format","table", 
						"-processor","simple",
						null,null
						},
				1, // found files
				"Strat Error 0013 Remote error (TreatmentPlayAnnouncement),1.0");
		

	}
	
private void parse(String[] s, int foundfiles, String search, int result_length) {
		CmdLineParser cmdParser = new CmdLineParser();
		JCommander commander = cmdParser.getCommander();

		try {
			
			String testresult="testresult_"+System.nanoTime();
			String path = Paths.get("").toAbsolutePath().toString()+"\\results";
			
			s[s.length-2]="-out";
			s[s.length-1]=testresult;
			
			commander.parse(s);
			Command cmd = cmdParser.getCommandObj(commander.getParsedCommand());

			assertTrue(cmd instanceof CommandParse);

			
			Map<String,String> results= run((CommandImpl) cmd).getStatData();
			
			int i = Integer.valueOf(results.get("Found"));
			assertTrue("ќжидаемое количество найденных файлов - "+foundfiles+", найдено " + i, i == foundfiles);
			assertTrue(Files.exists(Paths.get(path+"\\"+testresult)));

			String[] result=FilesUtil.read(new FileInputStream((path+"\\"+testresult)));
			
			assertTrue(true==contains(result, search));
			if (result_length!=0){
				assertTrue("ќжидаема€ длина массива результата - "+result_length+", найдено " + i,result.length==result.length);
			}

		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			commander.usage();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have thrown any exception");
			
		}finally{
			cmdParser.flush();
		}
	}
	
	private CommandImpl run(CommandImpl cmd) {
		cmd.getExtensions().forEach(s -> {
			ProcessFilesFabric.create((CommandImpl) cmd, s).start(cmd.getPaths());

		});
		return cmd;
	}
	
	private boolean contains(String[] arr, String search){
		
		System.out.println("Expected: "+search);
		System.out.println("Found   : "+Arrays.toString(arr));
		
		for(String s: arr)
			if (search.equals(s))
				return true;
		return false;
	}
	
	private void parse(String[] s, int foundfiles, String search){
		
		parse(s,foundfiles,search,0);
	}

}
