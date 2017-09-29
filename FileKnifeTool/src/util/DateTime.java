package util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateTime {

	public static LocalDateTime SimpleStringToDate(String input) {

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			String pad = padDateString(input);
			LocalDateTime date = LocalDateTime.parse(pad, formatter); // check
																		// the
																		// date
																		// to
																		// satisfy
																		// our
																		// pattern,
																		// append
																		// milliseconds
																		// if
																		// needed
			// System.out.printf("%s%n", date);

			return date;

		} catch (DateTimeParseException exc) {
			System.out.printf("%s is not parsable!%n", input);
			throw exc; // Rethrow the exception.
		}
	}

	public static long getTimeDifference(String start, String end) {

		LocalDateTime date = DateTime.SimpleStringToDate(start);
		LocalDateTime endDate = DateTime.SimpleStringToDate(end);

		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long epoch = date.atZone(zoneId).toInstant().toEpochMilli();
		long endepoch = endDate.atZone(zoneId).toInstant().toEpochMilli();

		long diff = endepoch - epoch;

		return (diff >= 0) ? diff : -1; // if start time is larger than end time
										// return -1

	}

	public static String padDateString(String str) {

		if (str.length() < 23) {// milliseconds aren't provided

			String[] arr = str.split("[\\s\\p{Punct}]");

			String[] timestamp_pattern=new String[]{"00","00","0000","00","00","00","000"};

			for(int i=0; i<arr.length;i++)
				timestamp_pattern[i]=arr[i];
			
				String s = timestamp_pattern[6];
				int diff = 0;
				if (s.length() < 3) {
					diff = 3 - s.length();
					for (int i = 0; i < diff; i++)
						s = s + "0";
				}
				
				str = timestamp_pattern[0] + "-" + timestamp_pattern[1] + "-" + timestamp_pattern[2] + " " + timestamp_pattern[3] + ":" + timestamp_pattern[4] + ":" + timestamp_pattern[5] + "." + s;
				
		}
		
		return str;
				
	}

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}
