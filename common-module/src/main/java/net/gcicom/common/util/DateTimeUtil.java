package net.gcicom.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.springframework.util.ObjectUtils;

/**
 * Common utility class to handle GCI specific date time conversion 
 *
 */
public abstract class DateTimeUtil {

	
	/** Returns The values are numbered following the ISO-8601 standard, 
	 * from 1 (Monday) to 7 (Sunday). See java.time.temporal.WeekFields.dayOfWeek() for localized week-numbering.
	 * @param date
	 * @return
	 */
	public static int getWeekDayFlag(LocalDateTime date) {
		
		if (ObjectUtils.isEmpty(date)) {
			
			throw new IllegalArgumentException("Input date time can not be null");
		}
		
		return date.getDayOfWeek().getValue();
		
	}
	
	/** converts hh:mm:ss duration to seconds
	 * @param duration
	 * @return
	 */
	public static int getDurationInSeconds(String duration) {
		
		if (ObjectUtils.isEmpty(duration)) {
			
			throw new IllegalArgumentException("Input date time can not be null");
			
		} 
		
		try {
			
			return LocalTime.parse(duration).toSecondOfDay();
			
		} catch (DateTimeParseException e) {
			
			throw new IllegalArgumentException("Input duration must be in java.time.format.DateTimeFormatter.ISO_LOCAL_TIME format", e);
		}
	}
	
	
	/**
	 * @return Today's date for UTC tz
	 */
	public static Date getTodaysDate() {
		
		return Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
	}
	
	/** convert {@link LocalDateTime} to {@link Date}
	 * @param dt
	 * @return
	 */
	public static Date convertLocalDateTimeToDate(LocalDateTime dt) {
		
		return Date.from(dt.toInstant(ZoneOffset.UTC));
	}
	
	public static String formatYYYYMM(LocalDateTime dt) {
		
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMM");
		return dt.format(df);
		
	}
	
	public static LocalDateTime convertStringToLocalDateTime(String dateInString,SimpleDateFormat formatter){
	   //  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	     //String dateInString = "07/06/2013";
	     LocalDateTime localDateTime = null;
	     ZoneId defaultZoneId = ZoneId.systemDefault();
	       
	        try {
	            Date date = formatter.parse(dateInString);	
	            
   	         //1. Convert Date -> Instant
	           Instant instant = date.toInstant();	       
	           
	         //2. Instant + system default time zone + toLocalDateTime() = LocalDateTime
	            localDateTime = instant.atZone(defaultZoneId).toLocalDateTime();
	          	           
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
			return localDateTime;
	}
	
}