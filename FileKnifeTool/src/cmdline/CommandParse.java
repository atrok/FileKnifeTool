package cmdline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import logprocessing.AggregatingStatistic;
import logprocessing.IncrementalStatistic;
import logprocessing.LineProcessing;
import logprocessing.StatDataProcessor;
import logprocessing.StatisticManager;
import logprocessing.StatisticParamNaming;
import resultoutput.CSVFileFromRecords;
import resultoutput.ResultOutput;
import resultoutput.ResultOutputFabric;
import util.FilesUtil;
import logprocessing.StatisticFactory;

@Parameters (separators=",", commandDescription=" command to parse genesys config server files")
public class CommandParse extends CommandImpl{

	@Parameter(names = "-process", description = "csv format handler (simple|record)", variableArity=false, required = false)
	private String process;
	
	@Parameter(names = "-out", description = "output filename", variableArity=false, required = false)
	private String output;
	
	@Parameter(names = "-sample", description = "statdata sampling (1|10) min", variableArity=false, required = true)
	private int sampling;

	@Parameter(names = "-format", description = "format of output file (csv)", variableArity=false, required = false)
	private String format="csv";

	
	private StatDataProcessor sdp;
	private ResultOutput result;
	private StatisticManager sm=StatisticManager.getInstance();
	private LineProcessing ln;
	
	private Logger logger=LoggerFactory.getLogger(CommandParse.class);
	
	public CommandParse() {
		super();
		// TODO Auto-generated constructor stub
		//ln=new LineProcessing(getSampling(),sm);

		
		
	}


	public ResultOutput getFormat() {
		return new CSVFileFromRecords(null, null);
	}

	public String getProcess() {
		return process;
	}

	public Path getOutput() {
		Path currentRelativePath = Paths.get("");
		if(null==output){// make default filename
			output="result_"+sampling+"_"+System.nanoTime()+"."+format;
		}
		String s = currentRelativePath.toAbsolutePath().toString()+"/"+output;
		logger.info("Result to be stored to : {}", s);
		return Paths.get(s);
	}

	public int getSampling() {
		return sampling;
	}


	  
	int counter=0;
	int processed=0;
	int failed=0;
	Map<String,String> data=new HashMap<String,String>();

	public void process(File file) {
		// TODO Auto-generated method stub
			counter++;
			long start=System.nanoTime();
			
			try {
				Stream<String> stream = Files.lines(file.toPath(),Charset.defaultCharset());
	            stream.forEach(s->{
	            	ln.processLine(s);
	            });
	            stream.close();
	            
	            processed++;
	            logger.info("procesed file: {}, {}", file, TimeUnit.SECONDS.convert(System.nanoTime()-start, TimeUnit.NANOSECONDS));
	            logger.debug("timeprocessing: {}",TimeUnit.SECONDS.convert(ln.getTimeprocessing(), TimeUnit.NANOSECONDS));
	            logger.debug("normalizing: {}",TimeUnit.SECONDS.convert(ln.getTimenormalizing(), TimeUnit.NANOSECONDS));
	            logger.debug("getstatvalue: {}",TimeUnit.SECONDS.convert(ln.getTimegetstatvalue(),TimeUnit.NANOSECONDS));
	            logger.debug("getsplitvalue: {}",TimeUnit.SECONDS.convert(ln.getTimesplitline(),TimeUnit.NANOSECONDS));
	 		}catch(IOException e){
	 			failed++;
	 			data.put(e.toString(), file.getAbsolutePath());
	 			e.printStackTrace();
	 		}

	}

	@Override
	public Map<String, String> getStatData() {// it calculates internal statistic related to files processing
		// TODO Auto-generated method stub
		data.put("Found",String.valueOf(counter));
		data.put("Processed",String.valueOf(processed));
		data.put("Failed",String.valueOf(failed));
		
		StatDataProcessor sdp=new StatDataProcessor(sm.getStatDataMap());

		result=ResultOutputFabric.getOutputProcessor(format,sdp, getOutput());
		
		result.outputResult();
		
		sm.flush();
		
		return data;
	}

	
	@Override
	public void resetStatData() {
		// TODO move these methods to CommandImpl

		counter=0;
		failed=0;
		processed=0;
		data=new HashMap<String,String>();
	}


	@Override
	protected void init() {
		// TODO Auto-generated method stub

		ln=new LineProcessing(sampling,sm);

		
		// Load statistics from configuration file
		
		Properties prop = new Properties();
    	InputStream input = null;
    	
    	try {
        
    		// checking classpath
    					ClassLoader cl = ClassLoader.getSystemClassLoader();

    					URL[] urls = ((URLClassLoader)cl).getURLs();

    						        for(URL url: urls){
    						        	System.out.println(url.getFile());
    						        }
    		


    						            File jarPath=new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    						            String propertiesPath=jarPath.getParentFile().getAbsolutePath();
    						            logger.debug(" propertiesPath: {} ",propertiesPath);
    						            //prop.load(new FileInputStream(propertiesPath+"/importer.properties"));

    						        
    		String filename = propertiesPath+"/statistic.properties.ini";
    		//input = CommandParse.class.getClassLoader().getResourceAsStream(filename);
    		input=new FileInputStream(filename);
    		
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    System.exit(1);
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
            	
            	sm.addStatistic(StatisticFactory.instance().getProduct(kv.get(StatisticParamNaming.STATTYPE),args));
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
			e.printStackTrace();
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
