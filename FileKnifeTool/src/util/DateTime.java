package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTime {
	
	public static LocalDateTime SimpleStringToDate(String input){
		
		try {
		    DateTimeFormatter formatter =
		                      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		    LocalDateTime date = LocalDateTime.parse(input, formatter);
		    //System.out.printf("%s%n", date);
		    
		    return date;
		    
		}
		catch (DateTimeParseException exc) {
		    System.out.printf("%s is not parsable!%n", input);
		    throw exc;      // Rethrow the exception.
		}
	}

	public static long getTimeDifference(String start, String end){
		
		LocalDateTime date=DateTime.SimpleStringToDate(start);
		LocalDateTime endDate=DateTime.SimpleStringToDate(end);
		
		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long epoch = date.atZone(zoneId).toInstant().toEpochMilli();
		long endepoch = endDate.atZone(zoneId).toInstant().toEpochMilli();
		
		long diff=endepoch-epoch;
		
		return (diff>=0)? diff : -1; // if start time is larger than end time return -1
		
	}
}
