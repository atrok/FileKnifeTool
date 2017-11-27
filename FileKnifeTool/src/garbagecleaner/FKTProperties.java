package garbagecleaner;

import java.io.File;

public class FKTProperties {
	public File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	public String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "\\conf\\";
	public String resultPath = jarPath.getParentFile().getAbsolutePath() + "\\results\\";

	private static FKTProperties obj=new FKTProperties();
	public static FKTProperties getProperties(){
		
		return obj;
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("jarPath:\t"+jarPath.toString()+"\n");
		sb.append("propertiesPath:\t"+propertiesPath+"\n");
		sb.append("resultPath:\t"+resultPath+"\n");
		
		return sb.toString();
	}
}
