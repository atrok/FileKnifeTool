package cmdline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import garbagecleaner.ENUMERATIONS;
import garbagecleaner.FKTProperties;
import logprocessing.AggregatingStatistic;
import logprocessing.IncrementalStatistic;
import logprocessing.LineProcessing;
import logprocessing.LineProcessingFactory;
import logprocessing.LineProcessingLogs;
import logprocessing.StatDataProcessor;
import logprocessing.StatDataProcessorFactory;
import logprocessing.StatDataProcessorLogs;
import logprocessing.StatDataProcessorSeparatorsCSV;
import logprocessing.StatisticParamNaming;
import paramvalidators.LineProcValidator;
import paramvalidators.OutputFormatValidator;
import paramvalidators.SamplingValidator;
import resultoutput.FileFabric;
import resultoutput.FileFromRecords;
import resultoutput.ResultOutput;
import resultoutput.ResultOutputFabric;
import statmanager.StatfileNotFoundException;
import statmanager.StatisticManager;
import statmanager.UnsupportedStatFormatException;
import statmanager.UnsupportedStatParamException;
import util.FilesUtil;
import logprocessing.StatisticFactory;

@Parameters (separators=",", commandDescription=" command to parse genesys config server files")
public class CommandParse extends CommandImpl{

	//@Parameter(names = "-process", description = "data format for file handler (simple|record)", variableArity=false, required = false, validateWith=ProcessParamValidator.class)
	protected String process="record";
	
	@Parameter(names = "-out", description = "output filename", variableArity=false, required = false)
	private String output;
	
	@Parameter(names = "-sample", description = "statdata sampling (0|1|10|60|24(h)) min", variableArity=false, required = false, validateWith=SamplingValidator.class)
	private int sampling=0;

	@Parameter(names = "-statfile", description = "name of file with statistics", variableArity=false, required = false)
	protected String statfile="statistic.properties.ini";
	
	@Parameter(names = "-format", description = "format of output file ("
			+ENUMERATIONS.FORMAT_CSV+"|"
			+ENUMERATIONS.FORMAT_SQL+"|"
			+ENUMERATIONS.FORMAT_STAT+"|"
			+ENUMERATIONS.FORMAT_BLOCK+"|"
			+ENUMERATIONS.FORMAT_TABLE+")", variableArity=false, required = false, validateWith=OutputFormatValidator.class)
	protected String format="stat";
	
	@Parameter(names = "-processor", description = "type of line processor (time|simple). Time line processor splits the string by spaces and seeks for timestamp in first column\n"
			+ "Simple Line processor splits the string by spaces and passes to statistic processor directly. It needs for cases to reveal commonities for custom ID others than timestamp (like connID)", variableArity=false, required = false, validateWith=LineProcValidator.class)
	protected String processor=ENUMERATIONS.ProcessorTIME;

	
	protected StatDataProcessor sdp;
	protected ResultOutput result;
	private StatisticManager sm;
	protected LineProcessing ln;
	
	private Logger logger=LoggerFactory.getLogger(CommandParse.class);
	
	protected Map<String, String> params=new HashMap<String,String>();
	
	public CommandParse() {
		super();
		// TODO Auto-generated constructor stub
		//ln=new LineProcessing(getSampling(),sm);
	}
	

	protected ResultOutput getFormat(Map<String,String> params,StatDataProcessor sdp, Path filename) {
		//params.put("process", process);
		//params.put("format", format);
		
		return FileFabric.getOutputFileCreator(process, sdp, filename);
		//return new CSVFileFromRecords(null, null);
	}

/*	protected String getProcess() {
		return process;
	}
*/
	protected Path getOutputPath() {
		Path currentRelativePath = Paths.get(context.resultPath);
		if(null==output){// make default filename
			output="result_"+sampling+"_"+System.nanoTime()+"."+format;
		}
		String s = currentRelativePath.toAbsolutePath().toString();
		
			try {
				Files.createDirectory(currentRelativePath);
			}catch(FileAlreadyExistsException e){
				logger.info("No need to create "+currentRelativePath.toString()+", folder exists ");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		s=s+"\\"+output;
		logger.info("Result to be stored to : {}", s);
		return Paths.get(s);
	}

	protected int getSampling() {
		return sampling;
	}


	  
	protected int counter=0;
	protected int counter_line=0;
	protected int processed=0;
	protected int failed=0;
	protected Map<String,String> data=new HashMap<String,String>();

	public void process(File file) {
		// TODO Auto-generated method stub
			counter++;
			long start=System.nanoTime();
			
			try {
				//Stream<String> stream = Files.lines(file.toPath(),Charset.defaultCharset());
				Stream<String> stream = Files.lines(file.toPath(),StandardCharsets.ISO_8859_1);
						//.parallel();
				//long numOfLines = stream.count();
				stream.forEach(s->{counter_line++;
	            	ln.processLine(s,new String[]{file.getName(),String.valueOf(counter_line)});
	            });
	            stream.close();
	            
	            processed++;
	            logger.info("processed file: {}, {} sec", file, TimeUnit.SECONDS.convert(System.nanoTime()-start, TimeUnit.NANOSECONDS));
	            
	            ln.getReport();
	            
	 		}catch(IOException e){
	 			failed++;
	 			data.put(e.toString(), file.getAbsolutePath()+"; Last processed line "+counter_line);
	 			logger.error("Failed at line "+counter_line,e);
	 			
	 		}finally{
	 			counter_line=0;
	 		}

	}

	@Override
	public Map<String, String> getStatData() {// it calculates internal statistic related to files processing
		// TODO Auto-generated method stub

		
		data.put("Found",String.valueOf(counter));
		data.put("Processed",String.valueOf(processed));
		data.put("Failed",String.valueOf(failed));
		
		//StatDataProcessor sdp=new StatDataProcessorLogs(sm.getStatDataMap());
		startAdditionalProcessing();

		return data;
	}

	private void startAdditionalProcessing(){
		loadDataToSDP();
		result=getFormat(params,sdp, getOutputPath());
		result.outputResult();
		sm.flush();
	}
	
	public void loadDataToSDP() {
		// TODO Auto-generated method stub
		sdp.load(sm.getStatDataMap());
	}


	@Override
	protected void resetStatData() {
		// TODO move these methods to CommandImpl

		counter=0;
		failed=0;
		processed=0;
		data=new HashMap<String,String>();
	}


	@Override
	public void init() throws StatfileNotFoundException, UnsupportedStatFormatException, UnsupportedStatParamException {
		// TODO Auto-generated method stub
		try {
		sm=StatisticManager.getInstance(format);
		ln=LineProcessingFactory.getInstance(processor, sampling, sm);
		sdp=StatDataProcessorFactory.getStatDataProcessor(format);
		
		// Load statistics from configuration file
		
		Properties prop = new Properties();
    	InputStream input = null;
    	
    	
        
    		// checking classpath
    					/*ClassLoader cl = ClassLoader.getSystemClassLoader();

    					URL[] urls = ((URLClassLoader)cl).getURLs();

    						        for(URL url: urls){
    						        	logger.debug("Classpath: {}",url.getFile());
    						        }*/
    		

    						        

    	//	File jarPath=new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    		//File jarPath=context.jarPath;
    		
            //String propertiesPath=jarPath.getParentFile().getAbsolutePath()+"\\conf";
    		String propertiesPath=context.propertiesPath;
    		
            logger.debug("Context assigned:\n {} ",context);
            //prop.load(new FileInputStream(propertiesPath+"/importer.properties"));				            

    						        
    		String filename = propertiesPath+"\\"+statfile;
    		//input = CommandParse.class.getClassLoader().getResourceAsStream(filename);
    		try{
    		input=new FileInputStream(filename);
    		
    		}catch(Exception e){
    	            logger.error("Sorry, unable to find {}", filename);
    		    throw new StatfileNotFoundException("Can't find statfile "+filename);
    		}
    		
    		Ini ini=new Ini();
    		ini.load(input);
    		// get all section names
            Set<String> sectionNames = ini.keySet();
            
            for(String section: sectionNames){
            	Map<String,String>kv=ini.get(section);
            	Object[] args=new Object[2];
            	
            	args[0]=section;
            	args[1]=kv;
            	
            	sm.addStatistic(StatisticFactory.instance().getProduct(kv.get(StatisticParamNaming.STATTYPE.toString()),args));
            }
/*    		String[] configuredStatistics=FilesUtil.read(input);
    		
    		for (String s: configuredStatistics){
    			String[] stat=StringUtils.split(s);
    			String[] args=new String[stat.length-1];
    			args=Arrays.copyOfRange(stat, 1, stat.length);
    			sm.addStatistic(StatisticFactory.instance().getProduct(stat[0],args));
    		}*/
    		
    		
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("File reading error: {}",e);
			System.exit(1);
		}catch (UnsupportedStatParamException e){
			logger.error("Statistic parameter error: {}",e);
			throw e;
			//System.exit(1);
		} catch (UnsupportedStatFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Output format error",e);
			throw e;
		}finally{}
    	
		//sm.addStatistic(new IncrementalStatistic(".+Message.+(received from|sent to).+","SentReceived"));
		//sm.addStatistic(new IncrementalStatistic(".+Trc\\s24300.+\\sclient.+\\sconnected.+","#New client connected"));
		//sm.addStatistic(new IncrementalStatistic(".+Trc\\s24301.+\\sClient.+\\sdisconnected.+","#Client disconnected"));
		//sm.addStatistic(new IncrementalStatistic(".+There are \\[[0-9]{3,}\\] objects of type.+","##ObjectSent  $9 <1000"));
		//sm.addStatistic(new IncrementalStatistic(".+(Trc|Std|Int|Dbg).+","$msgID"));
		//sm.addStatistic(new AggregatingStatistic(".+Total number of clients.+","#total clients",5));
		
		if (!isInitialized())
			setInitialized(true);
	}


	

	
}
