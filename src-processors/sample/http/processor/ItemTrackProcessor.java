package sample.http.processor;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import rfx.server.configs.ContentTypePool;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.data.HttpRequestEvent;
import rfx.server.http.data.service.DataService;

import com.google.gson.Gson;

/**
 * @author trieu
 * 
 *         simple sample processor
 *
 */
@HttpProcessorConfig(uriPath = "/tk", contentType = ContentTypePool.TRACKING_GIF)
public class ItemTrackProcessor extends HttpProcessor {

	@Override
	protected DataService process(HttpRequestEvent e) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.connect();
		String url = e.param("url");
		long count = jedis.hincrBy(url, "pageview", 1L);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("pageview", count);
		m.put("url", url);
		jedis.publish("realtime", new Gson().toJson(m));
		jedis.close();
		return EMPTY;
	}

}
