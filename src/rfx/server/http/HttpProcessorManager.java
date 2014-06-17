package rfx.server.http;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.RoundRobin;
import rfx.server.util.StringPool;
import rfx.server.util.template.MustacheUtil;

import com.google.gson.Gson;

/**
 * @author Trieu.nguyen
 * 
 * the manager, the factory for HTTP processor instances, input: HttpRequest output: FullHttpResponse
 *
 */
public class HttpProcessorManager {
	
	private String contentType;
	private String templatePath;
	Class<?> httpProcessorClass;
	RoundRobin<HttpProcessor> roundRobinRounter;
	private int maxPoolSize = 20000;	
	
	public HttpProcessorManager(String contentType, String templatePath, Class<?> httpProcessorClass) throws Exception {
		super();
		this.contentType = contentType;
		this.templatePath = templatePath;
		this.httpProcessorClass = httpProcessorClass;
		
		List<HttpProcessor> pool = new ArrayList<>(maxPoolSize);
		for (int i = 0; i < maxPoolSize; i++) {
			HttpProcessor httpProcessor = (HttpProcessor) httpProcessorClass.newInstance();
			pool.add(httpProcessor);
		}
		roundRobinRounter = new RoundRobin<>(pool);
	}

	/**
	 * always called by UrlMappingSingleProcessorHandler.callProcessor
	 * 
	 * @return FullHttpResponse
	 */
	public FullHttpResponse doProcessing(HttpRequestEvent requestEvent) {
		String outStr = null;
		BaseModel model = null;
		FullHttpResponse response;
		try {			
			model = roundRobinRounter.next().doProcessing(requestEvent);
			
			if(contentType.equals(ContentTypePool.JSON)){
				outStr = new Gson().toJson(model);
			} else {
				outStr = MustacheUtil.execute(templatePath, model);
			}
		} 
		catch (Throwable e) {
			e.printStackTrace();
			StringBuilder s = new StringBuilder("Error###");
			s.append(e.getMessage());
			s.append(" ### <br>\n StackTrace: ").append(ExceptionUtils.getStackTrace(e));							
			response = NettyHttpUtil.theHttpContent( s.toString() );
		} 
		finally {
			if(model != null){
				model.freeResource();
			}
		}
		
		//TODO log the result 		
		
		if (outStr != null) {
			response = NettyHttpUtil.theHttpContent(outStr, contentType);
		} else {
			response = NettyHttpUtil.theHttpContent(StringPool.BLANK);
		}
		return response;
	}
	
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getTemplatePath() {
		return templatePath;
	}
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}	


}
