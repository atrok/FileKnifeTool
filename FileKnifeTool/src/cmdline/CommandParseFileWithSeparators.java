package cmdline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import logprocessing.LineProcessing;
import logprocessing.LineProcessingLogs;
import logprocessing.LineProcessingSeparators;
import logprocessing.StatDataProcessor;
import logprocessing.StatDataProcessorFactory;
import logprocessing.StatDataProcessorSeparatorsCSV;
import logprocessing.StatisticFactory;
import logprocessing.StatisticManager;
import logprocessing.StatisticParamNaming;
import parameterizer.Parameterizer;
import resultoutput.FileFromArrays;
import resultoutput.FileFromRecords;
import resultoutput.ResultOutput;
import resultoutput.ResultOutputFabric;

@Parameters (separators=",", commandDescription=" command to parse alarms common files to retrieve log events and its categories")
public class CommandParseFileWithSeparators extends CommandParse{

	@Parameter(names = "-process", description = "data format for file handler (simple|record)", variableArity=false, required = false)
	protected String process="record";
	
	@Parameter(names = "-sep", description = "String separator, '|' is separator by default", variableArity=false, required = false)
	private char separator='|';

	//@Parameter(names = "-format", description = "format of output file (csv|sql|blocks)", variableArity=false, required = false)
	//private String format="csv";
	
	@Parameter(names = "-addname", description = "add <value:{begin|end|{int num}> to the parsed line at position)", variableArity=false, required = false)
	private Parameterizer param;
	
	private ResultOutput result;
	
	//private StatDataProcessor sdp;
	private Logger logger=LoggerFactory.getLogger(CommandParseFileWithSeparators.class);
	//private LineProcessing  ln= new LineProcessingSeparators(separator);
	
	public CommandParseFileWithSeparators() {
		super();
		// TODO Auto-generated constructor stub
		//ln=new LineProcessing(getSampling(),sm);

		
		
	}

/*


	public String getProcess() {
		return super.getProcess();
	}

	public Path getOutput() {
		return super.getOutput();
	}

*/
	
	int counter=0;
	int processed=0;
	int failed=0;
	Map<String,String> data=new HashMap<String,String>();

	/*
	public void process(File file) {
		super.process(file);

	}

*/
	/*
	@Override
	public Map<String, String> getStatData() {// it calculates internal statistic related to files processing
		// TODO think how to make this code less dependent on 
		data.put("Found",String.valueOf(counter));
		data.put("Processed",String.valueOf(processed));
		data.put("Failed",String.valueOf(failed));
		
		sdp=new StatDataProcessorSeparators(ln.getData());
		result=getFormat(params,sdp, getOutputPath());
		
		result.outputResult();
		
		return data;
	}

*/
	/*
	@Override
	public void resetStatData() {
		super.resetStatData();
	}

*/
	@Override
	public void init() {
		// TODO Auto-generated method stub

		ln= new LineProcessingSeparators(separator);
		sdp=StatDataProcessorFactory.getStatDataProcessor(format);
		

		
		if (!isInitialized())
			setInitialized(true);
	}

	public void loadDataToSDP() {
		// TODO Auto-generated method stub
		sdp.load(ln.getData());
	}

	
}
