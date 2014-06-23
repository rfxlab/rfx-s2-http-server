package rfx.server.util.template;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import rfx.server.http.BaseModel;
import rfx.server.http.TemplateConfig;

public class TemplateConfigUtil {
	
	final static Map<String, String> templateConfigCache = new HashMap<>();

	public static String getTemplateLocation(BaseModel model){
		
		String tplLocation = templateConfigCache.get(model.classpath());
		if(tplLocation == null){
			Class<?> modelClass = model.getClass();
			Reflections reflections = new Reflections(modelClass);
			Set<Class<?>> clazzes = reflections.getTypesAnnotatedWith(TemplateConfig.class);			
			for (Class<?> clazz : clazzes) {
				if (clazz.isAnnotationPresent(TemplateConfig.class) && clazz.equals(modelClass)) {
					Annotation annotation = clazz.getAnnotation(TemplateConfig.class);
					TemplateConfig templateConfig = (TemplateConfig) annotation;
					tplLocation = templateConfig.location();
					templateConfigCache.put(model.classpath(), tplLocation);
					System.out.println("...registered BaseModel:" + modelClass + " at location: "+tplLocation);
				}			
			}
		}
//		System.out.println("templateConfigCache: "+templateConfigCache);
//		System.out.println("getTemplateLocation: "+tplLocation);
		return tplLocation;
	}
	
	public static void initTemplateConfigCache(String mainPackage) throws IOException{
		Reflections reflections = new Reflections(mainPackage);
		Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(TemplateConfig.class);			
		for (Class<?> modelClass : modelClasses) {
			if (modelClass.isAnnotationPresent(TemplateConfig.class) ) {
				Annotation annotation = modelClass.getAnnotation(TemplateConfig.class);
				TemplateConfig templateConfig = (TemplateConfig) annotation;
				String tplLocation = templateConfig.location();
				templateConfigCache.put(modelClass.getName(), tplLocation);
				HandlebarsTemplateUtil.compileAndCache(tplLocation);
				System.out.println("...registered model:" + modelClass.getName() + " at location: "+tplLocation);
			}			
		}
	}
}
