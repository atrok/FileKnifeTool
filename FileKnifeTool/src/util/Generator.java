package util;

public class Generator {
	private static int i=0;
	private static String md5="";
	
	public static Integer getID(){
		i++;
		//
		return i;
	}
	
	private static Integer getLastGeneratedID(){
		return i;
	}
	
	public static String getMD5(){
		md5=MyStringUtils.MD5(Integer.toString(getID()));
		
		return md5;
	}
	
	public static String getLastGeneratedMD5(){
				
		return (md5==null) ? getMD5(): md5;
	}
}
