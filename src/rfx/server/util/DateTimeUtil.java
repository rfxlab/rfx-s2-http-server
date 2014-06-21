package rfx.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	public final static long COOKIE_AGE_10_YEARS = 630720000;
	public final static long COOKIE_AGE_2_YEARS = 63072000;
	public final static long COOKIE_AGE_1_HOUR = 3600; // One hour
	public final static long COOKIE_AGE_2_HOURS = 7200; // 2 hours
	public final static long COOKIE_AGE_3_HOURS = 10800; // 3 hours	
	public final static long COOKIE_AGE_1_DAY = 86400; // One day
	public final static long COOKIE_AGE_3_DAYS = 259200; // 3 days
	public final static long COOKIE_AGE_1_WEEK = 604800; // One week
	
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	static final DateFormat DATE_NAME_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	static final DateFormat HOUR_NAME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:00:00");
	static final DateFormat DATE_FORMAT_FOR_DB = new SimpleDateFormat("yyyy-MM-dd");
	
	static final DateFormat DATEHOUR_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH");	
	static final DateFormat DATEHOURMINUTE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	

	public static String formatDate(Date d ){
		return DATE_FORMAT.format(d);
	}
	
	public static String formatDateName(Date d ){
		return DATE_FORMAT.format(d);
	}
	
	public static String formatHourName(Date d ){
		return HOUR_NAME_FORMAT.format(d);
	}
	
	public static long getTimestampFromDateHour(String d){
		try {
			return HOUR_NAME_FORMAT.parse(d).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static long getTimestampFromDate(String d){
		try {
			return DATE_NAME_FORMAT.parse(d).getTime();
		} catch (ParseException e) {
			System.err.println(d + " getTimestampFromDate " + e.getMessage());
		}
		return 0;
	}
	
	public static int getTimestampFromDate(java.sql.Date d){
		if(d != null){
			return (int) (d.getTime()/1000);
		}
		return 0;
	}
	
	public static String formatDate(Date d, String format ){
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(d);
	}
	
	public static String getDateStringForDb(Date d ){
		return DATE_FORMAT_FOR_DB.format(d);
	}
	
	public static Date parseDateStr(String str){
		try {
			return DATE_FORMAT.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Date();
	}
	
	public static synchronized String getDateHourString(Date d ){
		return DATEHOUR_FORMAT.format(d);
	}
	
	public static String formatDateHourMinute(Date date) {
		return DATEHOURMINUTE_FORMAT.format(date);		
	}
	
	public static Date parseDateHourMinuteStr(String str) throws ParseException{
		return DATEHOURMINUTE_FORMAT.parse(str);		
	}
}
