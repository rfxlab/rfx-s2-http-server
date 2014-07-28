package rfx.server.util.template;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.reflections.Reflections;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.DataService;
import rfx.server.http.HttpOutputResource;
import rfx.server.http.HttpRequestEvent;
import rfx.server.http.OutputConfig;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.FileUtils;
import rfx.server.util.StringUtil;

public class OutputConfigUtil {
	
	final static Map<String, OutputConfig> outputConfigCache = new HashMap<>();
	

	public static OutputConfig getOutputConfig(DataService model) throws IOException{
		OutputConfig outputConfig = outputConfigCache.get(model.getClasspath());
		if(outputConfig != null){			
			return outputConfig;
		}
		throw new IOException(" Not found outputConfig for "+model.getClasspath()); 
	}
	
	public static FullHttpResponse processOutput(HttpRequestEvent requestEvent, DataService model, String contentType) throws IOException{
		if( model == null){
			return NettyHttpUtil.theHttpContent(StringUtil.toString("Null model"), contentType);
		}
		if( ! model.isOutputable() ){
			return NettyHttpUtil.theHttpContent(StringUtil.toString("The model ",model.getClass().getName(), " is not outputable"), contentType);
		}
		OutputConfig config = getOutputConfig(model);
		int type = config.type();
		String location = config.template();
		
		if(type == OutputConfig.HANDLEBARS_TEMPLATE){			
			String text = HandlebarsTemplateUtil.execute(location, model);
			if(ContentTypePool.HTML_UTF8.equals(contentType)){
				text = StringEscapeUtils.unescapeHtml4(text);
			}
			
//			if(requestEvent.param("jscompress", "").equals("1"))
//			{
//				switch (contentType) {
//					case ContentTypePool.JAVA_SCRIPT:
//						text = JsOptimizerUtil.compile(text);
//						break;
//					default:
//						break;
//				}			
//			}
//			System.out.println(text);
			FullHttpResponse response = NettyHttpUtil.theHttpContent(text , contentType);
			List<HttpHeaders> list = model.getHttpHeaders();
			if(list != null){
				list.stream().forEach(new Consumer<HttpHeaders>() {
					@Override
					public void accept(HttpHeaders h) {
						response.headers().add(h);
					}
				});	
			}			
			return response;
		} else if(type == OutputConfig.STATIC_FILE){			
			HttpOutputResource re = FileUtils.readHttpOutputResource(location);
			return NettyHttpUtil.theHttpContent(re , contentType);
		}
		return NettyHttpUtil.theHttpContent("", contentType);		
	}
	
	public static void initTemplateConfigCache(String mainPackage) throws IOException{
		Reflections reflections = new Reflections(mainPackage);
		Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(OutputConfig.class);			
		for (Class<?> modelClass : modelClasses) {
			if (modelClass.isAnnotationPresent(OutputConfig.class) ) {
				Annotation annotation = modelClass.getAnnotation(OutputConfig.class);
				OutputConfig templateConfig = (OutputConfig) annotation;
				String tplLocation = templateConfig.template();
				int type = templateConfig.type();
				outputConfigCache.put(modelClass.getName(), templateConfig);
				
				if(type == OutputConfig.HANDLEBARS_TEMPLATE){	
					HandlebarsTemplateUtil.compileAndCache(tplLocation);
				}
				
				System.out.println("...registered model:" + modelClass.getName() + " at location: "+tplLocation);
			}			
		}
	}
}
