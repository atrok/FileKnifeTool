package util;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class MyStringUtils {
	
	public static String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
	
	public MyStringUtils(){};
	private HashMap<String,String[]> cache;
	
	public String[] getArray(String s,String regex){
		
		//String hashcode=MD5(s);
		
		if (cache==null)
			cache=new HashMap<String,String[]>();
		
		String[] arr=cache.remove(s);
		
		if(null==arr){
			
			if(regex==null)
				arr=StringUtils.split(s);
			else
				arr=s.split(regex);
			cache.put(s, arr);
			
		}
		
		return arr;
		
		
	}
}
