package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Benchmark {
	
	static Logger logger=LoggerFactory.getLogger(Benchmark.class);
    // time that tick() was called
    static long tickTime;

    // called at start of operation, for timing
    public static void tick () {
        tickTime = System.nanoTime();
    }

    // called at end of operation, prints message and time since tick().
    public static void tock (String action) {
        long mstime = (System.nanoTime() - tickTime) / 1000000;
        logger.debug("{} : {} ms",action,mstime);
    }
    
    public static long tack () {
        return (System.nanoTime() - tickTime);
        //logger.debug("{} : {} ms",action,mstime);
    }


}
