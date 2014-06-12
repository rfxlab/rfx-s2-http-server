package rfx.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import rfx.server.configs.HttpServerConfigs;
import rfx.server.http.common.NettyHttpUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class ServerCommandUtil {

	public static final String LOG_ARTICLE_CMD = "log_article_id";
	public static final String SECURITY_STRING = "agst2324hhksd24834j8jd262";
	
	public static FullHttpResponse commandResponse(String ipAddress, HttpRequest request){
		
		FullHttpResponse response = null; 
		
		String uri = request.getUri()+"&";
		String command   = getValueByParam(uri,"cmd");

		if( LOG_ARTICLE_CMD.equals( command ) ){
			
			String securitySt   = getValueByParam(uri,"code");
			String option       = getValueByParam(uri,"option");
			String siteId       = getValueByParam(uri,"site-id");
			String articleId    = getValueByParam(uri,"article-id");
			String hour         = getValueByParam(uri,"hour");
			
			String htmlResponse = "";

			if( !SECURITY_STRING.equals(securitySt) ){
				htmlResponse = "WRONG SECURITY STRING!";
				return NettyHttpUtil.theHttpContent(htmlResponse);
			}
			
			// get server ip, server port
			String serverip   = HttpServerConfigs.load().getIp();
			int serverport    = HttpServerConfigs.load().getPort();
			
			if( "SET".equals(option) ){

				htmlResponse += "COMMAND = "+command+"\n";
				htmlResponse += "OPTION = "+option+"\n";
				htmlResponse += "SITE-ID = "+siteId+"\n";
				htmlResponse += "ARTICLE-ID = "+articleId+"\n";
				htmlResponse += "----------------------------> \n"+"\n";
				
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:sss");
				htmlResponse += "SET TIME  = "+df.format(Calendar.getInstance().getTime())+"\n";
				htmlResponse += "SERVER IP = "+serverip+"\n";
				htmlResponse += "SERVER PORT = "+serverport+"\n";
				
				CountArticleUtil.reset();
				String[] arIdArr = articleId.split(",");
				for( String arId :  arIdArr ){
					 CountArticleUtil.getCountMap().put(siteId+":"+arId, new Integer(0) );
				}
				CountArticleUtil.setServerIp( serverip );
				CountArticleUtil.setServerPort( serverport );
				CountArticleUtil.setHour( Integer.parseInt(hour) );
				CountArticleUtil.setSetUpTime( df.format(Calendar.getInstance().getTime()) );
				CountArticleUtil.setTurnOn(true);
				
				htmlResponse += "HOUR MONITOR = "+CountArticleUtil.getHour()+" \n";
				htmlResponse += "TURN ON   = "+CountArticleUtil.isTurnOn()+" \n";
				
			}
			else if( "UNSET".equals(option) ){
				CountArticleUtil.reset();
				htmlResponse += "COMMAND = "+command+"\n";
				htmlResponse += "OPTION = "+option+"\n";
				htmlResponse += "----------------------------> \n"+"\n";
				htmlResponse += "UNSET COMPLETE!"+"\n";
			}
			else if( "GET".equals(option) ){
				
				htmlResponse += "COMMAND = "+command+"\n";
				htmlResponse += "OPTION = "+option+"\n";
				htmlResponse += "---------------------------- \n"+"\n";
				htmlResponse += "SET TIME  = "+CountArticleUtil.getSetUpTime()+" \n";
				htmlResponse += "SERVER IP = "+CountArticleUtil.getServerIp()+"\n";
				htmlResponse += "SERVER PORT = "+CountArticleUtil.getServerPort()+"\n";
				htmlResponse += "HOUR MONITOR = "+CountArticleUtil.getHour()+" \n";
				htmlResponse += "TURN ON   = "+CountArticleUtil.isTurnOn()+" \n";
				htmlResponse += "----------------------------> "+"\n";
				for( String key : CountArticleUtil.getCountMap().keySet() ){
					 htmlResponse += key + " --> " + CountArticleUtil.getCountMap().get(key)+"\n";
				}
				
			}
			
			response = NettyHttpUtil.theHttpContent(htmlResponse);
		}
		
		
		return response;
	}
	
	
	private static String getValueByParam(String uri , String param){
		int idx = uri.indexOf(param+"=");
		if( idx > -1 ){
			return  uri.substring(idx+param.length()+1 , uri.indexOf("&", idx+param.length()+2) );
		}
		
		return "";
	}
	
	
}
