package rfx.server.http;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.RoundRobin;
import rfx.server.util.StringPool;
import rfx.server.util.StringUtil;
import rfx.server.util.template.MustacheTemplateUtil;

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
	public static int DEFAULT_MAX_POOL_SIZE = 20000;	
	
	public HttpProcessorManager(String contentType, String templatePath, Class<?> httpProcessorClass) throws Exception {
		super();	
		init(contentType, templatePath, httpProcessorClass, DEFAULT_MAX_POOL_SIZE);
	}
	
	public HttpProcessorManager(String contentType, String templatePath, Class<?> httpProcessorClass, int maxPoolSize) throws Exception {
		super();	
		init(contentType, templatePath, httpProcessorClass, maxPoolSize);
	}
	
	void init(String contentType, String templatePath, Class<?> httpProcessorClass, int maxPoolSize) throws InstantiationException, IllegalAccessException {
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
				outStr = MustacheTemplateUtil.execute(templatePath, model);
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

	/**
	 * run at server bootstrap
	 * @throws Exception 
	 */
	public static Map<String, HttpProcessorManager> loadHandlers(String processorPackage, int filteredAccessMode, int processorPoolSize) throws Exception {	
		Reflections reflections = new Reflections(processorPackage);
		Set<Class<?>> clazzes =  reflections.getTypesAnnotatedWith(HttpProcessorConfig.class);
		Map<String, HttpProcessorManager> tempMap = new HashMap<>();
		System.out.println("Http Processor Scanning "+processorPackage + " for access mode "+filteredAccessMode);
	    for (Class<?> clazz : clazzes) {
			if (clazz.isAnnotationPresent(HttpProcessorConfig.class)) {        		     
				Annotation annotation = clazz.getAnnotation(HttpProcessorConfig.class);
				HttpProcessorConfig config = (HttpProcessorConfig) annotation;
				
				if( config.privateAccess() == filteredAccessMode){
					HttpProcessorManager manager = tempMap.get(config.uriPath());
					if( manager == null ){
						manager = new HttpProcessorManager(config.contentType(), config.templatePath(), clazz, processorPoolSize);						
						if( StringUtil.isNotEmpty(config.uriPath()) ){
							tempMap.put(config.uriPath(), manager);
							String s = "...registered controller class: "+ clazz.getName() + " ;uriPath:"+config.uriPath()+" ;tpl:"+config.templatePath()+ " ;content-type"+config.contentType();
							System.out.println(s);
						} 
						else if( StringUtil.isNotEmpty(config.uriPattern()) ){
							tempMap.put(config.uriPattern(), manager);
							String s = "...registered controller class: "+ clazz.getName() + " ;uriPattern:"+config.uriPattern()+" ;tpl:"+config.templatePath()+ " ;content-type"+config.contentType();
							System.out.println(s);
						}
						else {
							throw new IllegalArgumentException("the class "+clazz.getName() + " is missing uriPath or uriPattern config");
						}    				
					} else {
						throw new IllegalArgumentException("duplicated "+ config.uriPath() + " , existed class " + manager.getClass().getName());
					}
				}
			}  	        	
	    }
	    return Collections.unmodifiableMap(tempMap);
	}
	
	public static final HttpProcessorManager routingForUriPath(Map<String, HttpProcessorManager> handlers, QueryStringDecoder qDecoder){		
		return handlers.get(qDecoder.path());
	}
	
	public static final HttpProcessorManager routingForUriPattern(Map<String, HttpProcessorManager> handlers, QueryStringDecoder qDecoder, int index){
		String[] toks = qDecoder.path().split("/");
		if(toks.length  >index){
			String pathPattern = toks[index];
			return handlers.get(pathPattern);						
		}
		return null;
	}
}
