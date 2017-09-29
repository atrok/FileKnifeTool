package tests;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import logprocessing.DurationStatistic;
import logprocessing.LineProcessingLogs;
import statmanager.StatisticManager;
import util.DateTime;

public class TestUtils {

	Path file = Paths.get("Tests/resources/config_proxy_person_cto_p_all.20150918_095313_510.log");//"Tests/resources/cs85.20151223_145909_388.log");
	
	@Test
	public void testDateFormatter() {
		String 	 t="2017-05-05 06:42:29.034";
		String end="2017-05-05 06:42:31.23";
		
		LocalDateTime a=LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
		LocalDateTime date=DateTime.SimpleStringToDate(t);
		LocalDateTime endDate=DateTime.SimpleStringToDate(end);
		
		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long epoch = date.atZone(zoneId).toInstant().toEpochMilli();
		long endepoch = endDate.atZone(zoneId).toInstant().toEpochMilli();
		
		long diff=endepoch-epoch;
		assertTrue("We got "+diff,diff==64115);
	}
	
	@Test
	public void testPadding() {
		String 	 t="2017-05-05 06:42";
		String end="2017-05-05 06:42:31.23";
		
		LocalDateTime a=LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
		LocalDateTime date=DateTime.SimpleStringToDate(t);
		LocalDateTime endDate=DateTime.SimpleStringToDate(end);
		
		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long epoch = date.atZone(zoneId).toInstant().toEpochMilli();
		long endepoch = endDate.atZone(zoneId).toInstant().toEpochMilli();
		
		long diff=endepoch-epoch;
		assertTrue("We got "+diff,diff==31230);
	}

}
