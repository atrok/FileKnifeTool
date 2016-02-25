package resultoutput;

import java.nio.file.Path;

import logprocessing.StatDataProcessor;

public abstract class AbstractFileOutput implements ResultOutput {

	protected StatDataProcessor sdp;
	protected Path filename;
	
	public AbstractFileOutput(StatDataProcessor sdp, Path filename){
		this.sdp=sdp;
		this.filename=filename;
	}
	
	public abstract void outputResult();
	
}
