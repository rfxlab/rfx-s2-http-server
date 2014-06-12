package rfx.server.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CountArticleUtil {

	private static HashMap<String, Integer> countMap = new HashMap<>();
	private static int hour ;
	private static String serverIp ;
	private static int serverPort ;
	private static String setUpTime ;
	private static boolean turnOn ;
	
	public static synchronized void countArticle( String uri ){

		try{
		
			if( !turnOn ) return;
			
			Date currD = Calendar.getInstance().getTime() ;
			Calendar.getInstance().setTime(currD);
			
			if( hour != Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ){
				return ;
			}
			
			if( uri.indexOf("get?action_name=")>-1 ){
				
				int siteIdIdx   = uri.indexOf("&idsite=");
				String siteIdSt = "";
				if( siteIdIdx>-1 ){
					siteIdSt = uri.substring( siteIdIdx+8 , uri.indexOf("&",siteIdIdx+8) ) ;
				}
				
				int arIdIdx = uri.indexOf("tt_article_id%22%2C%22");
				String arIdSt = "";
				if( arIdIdx>-1 ){
					arIdSt = uri.substring( arIdIdx+22 , uri.indexOf("%22",arIdIdx+22) ) ;
				}
				//System.out.println( siteIdSt+":"+arIdSt );
				Integer count = countMap.get(siteIdSt+":"+arIdSt) ;
				if( count != null ){
					//System.out.println( "PUT "+siteIdSt+":"+arIdSt + " , count : " + (count+1) );
					countMap.put(siteIdSt+":"+arIdSt,count+1) ;
				}
				
			}
		}
		catch(Exception e){
			LogUtil.error("CountArticleUtil.countArticle : ", e.getMessage() );
		}
		
	}
	
	public static void reset(){
		countMap.clear();
		hour=0;
		serverIp="";
		serverPort=0;
		setUpTime="";
		turnOn=false;
	}

	//---------------------------------------------------------

	public static int getHour() {
		return hour;
	}

	public static HashMap<String, Integer> getCountMap() {
		return countMap;
	}

	public static void setCountMap(HashMap<String, Integer> countMap) {
		CountArticleUtil.countMap = countMap;
	}

	public static void setHour(int hour) {
		CountArticleUtil.hour = hour;
	}

	public static String getServerIp() {
		return serverIp;
	}

	public static void setServerIp(String serverIp) {
		CountArticleUtil.serverIp = serverIp;
	}

	public static String getSetUpTime() {
		return setUpTime;
	}

	public static void setSetUpTime(String setUpTime) {
		CountArticleUtil.setUpTime = setUpTime;
	}

	public static boolean isTurnOn() {
		return turnOn;
	}

	public static void setTurnOn(boolean turnOn) {
		CountArticleUtil.turnOn = turnOn;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public static void setServerPort(int serverPort) {
		CountArticleUtil.serverPort = serverPort;
	}
	
}
