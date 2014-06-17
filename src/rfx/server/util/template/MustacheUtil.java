package rfx.server.util.template;

import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;

public class MustacheUtil {
	static MustacheFactory mustacheFactory = null;
	static Map<String, Mustache> mustacheMap = new ConcurrentHashMap<>();//for hot deployment & update template 
	final static String BASE_TEMPLATE_PATH = "resources/tpl/";
	
	//flag
	static boolean isUsedCache = true;
	
	public static MustacheFactory getMustacheFactory() {
		if(mustacheFactory == null){
			mustacheFactory = new DefaultMustacheFactory();
		}
		return mustacheFactory;
	}
	
	public static void refreshTemplateCache(){
		mustacheFactory = null;
		mustacheMap.clear();
	}
	
	public static Mustache getCompiledTemplate(String tplPath){
		String path = BASE_TEMPLATE_PATH + tplPath;
		if(isUsedCache){
			Mustache mustache = mustacheMap.get(path);
			if(mustache == null){
				mustache = getMustacheFactory().compile(path);
				mustacheMap.put(path, mustache);
			}		
			return mustache;
		} else {			
			return new DefaultMustacheFactory().compile(path);
		}
	}
	
	public static String execute(String tplPath, Object model){	
		if(tplPath == null){
			throw new IllegalArgumentException("tplPath is NULL");
		}
		try {
			StringWriter stringWriter = new StringWriter();
			getCompiledTemplate(tplPath).execute(stringWriter, model).flush();
			stringWriter.flush();
			stringWriter.close();				
			return stringWriter.toString();
		} catch (MustacheException e) {
			StringBuilder s = new StringBuilder("Error:");
			s.append(e.getMessage());
			return s.toString();
		} catch (Throwable e) {
			StringBuilder s = new StringBuilder("Error###");
			s.append(e.getMessage());
			s.append(" ### <br>\n StackTrace: ").append(ExceptionUtils.getStackTrace(e));
			return s.toString();
		}
	}
	
}
