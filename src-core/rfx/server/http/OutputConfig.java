package rfx.server.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputConfig {
	String location() default "system/default";
	int type() default HANDLEBARS_TEMPLATE;
	
	public static int STATIC_FILE = 11;
	public static int HANDLEBARS_TEMPLATE = 0;
	public static int MUSTACHE_TEMPLATE = 1;
}