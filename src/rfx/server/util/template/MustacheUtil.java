package rfx.server.util.template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;

public class MustacheUtil {
	static MustacheFactory mf = new DefaultMustacheFactory();
	static Map<String, Mustache> mustacheMap = new HashMap<>();
	final static String BASE_TEMPLATE_PATH = "resources/tpl/";
	
	public static Mustache getCompiledTemplate(String tplPath){
		String path = BASE_TEMPLATE_PATH + tplPath;
		Mustache mustache = mustacheMap.get(path);
		if(mustache == null){
			mustache = mf.compile(path);
		}
		return mustache;
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
