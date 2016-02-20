package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class testRegexp {
	Logger logger=LoggerFactory.getLogger(testRegexp.class);
	
	
	@Test
	public void test1() {
		
		
		String m="2015-09-18T11:26:57.583 Trc 04541 Message MSGCFG_GETOBJECTINFO received from 816 (InteractionWorkspace  'Inbox_Uniclass_Prod')";
		logger.info("before: {}",m);
		m=m.replaceAll("\\p{Punct}", "A");
		logger.info("after: {}",m);
		

		matches(".+Trc\\s24301.+\\sClient.+\\sdisconnected.+","2015-09-18T09:56:03.216 Trc 24301 Extended info : Client [2068] disconnected, application [Inbox_Uniclass_Prod], type [InteractionWorkspace ], protocol [CFGLIB]");
		matches(".+Trc\\s24300.+\\sclient.+\\sconnected.+","2015-09-18T09:53:39.953 Trc 24300 Extended info : New client [1888] connected, protocol [cfglib]");
		
		matches(".+\\]$","[CfgAgent Group]");
		
		matches(".+Std\\s+[0-9]{5,5}\\s.+","14:23:44.340 Std 04541 Message MSGCFG_CHANGEOBJECT received from 624 (SCE 'default')");
		
		matches("^MSGCFG.+[0-9]?","MSGCFG_REGISTEROBJECTTYPEEX2");
		
		matches("^[0-9T:.-]{12,}\\s(Trc|Std|Int|Dbg)\\s+[0-9]{5,5}\\s.+", "22:43:47.812 Std 22122 Client 197 failed to get authorization. Name [default], type [SCE], user [readonly], address [17.170.208.58:59643]. Reason : Authentication failed, user name or password is incorrect");
		
	}
	
	private void matches(String regexp, String line){
		if (Pattern.matches(regexp, line))
			logger.info("match\nString {} \n to pattern {}", line, regexp);
		else
			logger.info("doesn't match\nString {}\n to pattern {}", line, regexp);
	}



	    // time that tick() was called
	    static long tickTime;

	    // called at start of operation, for timing
	    static void tick () {
	        tickTime = System.nanoTime();
	    }

	    // called at end of operation, prints message and time since tick().
	    static void tock (String action) {
	        long mstime = (System.nanoTime() - tickTime) / 1000000;
	        System.out.println(action + ": " + mstime + "ms");
	    }

	    // generate random strings of form AAAABBBCCCCC; a random 
	    // number of characters each randomly repeated.
	    static List<String> generateData (int itemCount) {

	        Random random = new Random();
	        List<String> items = new ArrayList<String>();
	        long mean = 0;

	        for (int n = 0; n < itemCount; ++ n) {
	            StringBuilder s = new StringBuilder();
	            int characters = random.nextInt(7) + 1;
	            for (int k = 0; k < characters; ++ k) {
	                char c = (char)(random.nextInt('Z' - 'A') + 'A');
	                int rep = random.nextInt(95) + 5;
	                for (int j = 0; j < rep; ++ j)
	                    s.append(c);
	                mean += rep;
	            }
	            items.add(s.toString());
	        }

	        mean /= itemCount;
	        System.out.println("generated data, average length: " + mean);

	        return items;

	    }

	    // match all strings in items to regexStr, do not precompile.
	    static void regexTestUncompiled (List<String> items, String regexStr) {

	        tick();

	        int matched = 0, unmatched = 0;

	        for (String item:items) {
	            if (item.matches(regexStr))
	                ++ matched;
	            else
	                ++ unmatched;
	        }

	        tock("uncompiled: regex=" + regexStr + " matched=" + matched + 
	             " unmatched=" + unmatched);

	    }

	    // match all strings in items to regexStr, precompile.
	    static void regexTestCompiled (List<String> items, String regexStr) {

	        tick();

	        Matcher matcher = Pattern.compile(regexStr).matcher("");
	        int matched = 0, unmatched = 0;

	        for (String item:items) {
	            if (matcher.reset(item).matches())
	                ++ matched;
	            else
	                ++ unmatched;
	        }

	        tock("compiled: regex=" + regexStr + " matched=" + matched + 
	             " unmatched=" + unmatched);

	    }

	    // test all strings in items against regexStr.
	    static void regexTest (List<String> items, String regexStr) {

	        regexTestUncompiled(items, regexStr);
	        regexTestCompiled(items, regexStr);

	    }

	    // generate data and run some basic tests
	    @Test
	    public void testStart () {

	        List<String> items = new ArrayList();
	        //generateData(1000000);
	        items.add("2015-09-18T09:56:03.216 Trc 24301 Extended info : Client [2068] disconnected, application [Inbox_Uniclass_Prod], type [InteractionWorkspace ], protocol [CFGLIB]");
	        items.add("2015-09-18T09:53:39.953 Trc 24300 Extended info : New client [1888] connected, protocol [cfglib]");
	        
	        regexTest(items, ".+Trc\\s24300.+\\sclient.+\\sconnected.+");
	        //regexTest(items, ".+Trc\\s24300.+");
	        //regexTest(items, "E*C*W*F*");

	    }


	
	
}
