package sample.http.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import rfx.server.configs.ContentTypePool;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.data.HttpRequestEvent;
import rfx.server.http.data.service.DataService;
import rfx.server.util.StringUtil;

import com.google.gson.Gson;

@HttpProcessorConfig(uriPath= "/u", contentType = ContentTypePool.TRACKING_GIF)
public class UserTrackingProcessor extends HttpProcessor {

		
	
	@Override
	protected DataService process(HttpRequestEvent e) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.connect();			
		
		String url = e.param("url");
		String heats = e.param("heats");		
		String[] tokens = heats.split("d");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>(tokens.length);
		for (String token : tokens) {
			String[] toks = token.split("j");
			if(toks.length >= 3){
				Map<String, Object> p = new HashMap<String, Object>();
				p.put("x", StringUtil.safeParseInt(toks[0]));
				p.put("y", StringUtil.safeParseInt(toks[1]));
				p.put("value", StringUtil.safeParseInt(toks[2]));
				list.add(p);
			}								
		}
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("heats", list);
		m.put("url", url);
		jedis.publish("realtime", new Gson().toJson(m));
		jedis.close();
		return EMPTY;
	}

}
