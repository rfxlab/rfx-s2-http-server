package rfx.server.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateConfig {
	String location() ;
	int engine() default HANDLEBARS;
	
	public static int HANDLEBARS = 0;
	public static int MUSTACHE = 1;
}