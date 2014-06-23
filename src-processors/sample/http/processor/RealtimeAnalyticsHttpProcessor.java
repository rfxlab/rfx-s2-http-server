package sample.http.processor;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;
import rfx.server.configs.ContentTypePool;
import rfx.server.http.BaseModel;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.HttpRequestEvent;



/**
 * @author trieu
 * 
 * simple sample processor 
 *
 */
@HttpProcessorConfig(uriPath= "/analytics", contentType = ContentTypePool.JSON)
public class RealtimeAnalyticsHttpProcessor extends HttpProcessor {
	
	@Override
	protected BaseModel process(HttpRequestEvent requestEvent) {		
		String cmd = requestEvent.param("cmd", "show-all");
		System.out.println("cmd: " + cmd);
		return new AnalyticData(cmd).processCommand();
	}

	static class AnalyticData implements BaseModel{		
		String cmd = "Hello ";
		Set<String> hotKeywords = new HashSet<>();

		public AnalyticData(String cmd) {
			super();
			this.cmd = cmd;
			
		}
		
		public AnalyticData processCommand(){
			//String minuteStr = DateTimeUtil.formatDateHourMinute(new Date());
			switch (cmd) {
			case "hotKeywords":
				Jedis jedis = new Jedis("127.0.0.1");
				
				Set<String> keys = jedis.keys("trending-keywords:*");
				for (String key : keys) {
					Set<String> keywords = jedis.zrange(key , 0, -1);					
					hotKeywords.addAll(keywords);
				}
				
				//String key = "trending-keywords:"+minuteStr;
			
				
				break;
			default:
				break;
			}
			return this;
		}
		
		@Override
		public void freeResource() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isOutputableText() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
