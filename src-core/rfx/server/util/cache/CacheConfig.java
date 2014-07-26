package rfx.server.util.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfig {
	String keyPrefix() default ""; //the prefix of key
	long maximumSize() default -1; //unlimited
	long expireAfter() default 600; //10 minutes
	int type() default LOCAL_CACHE_ENGINE; //default using Google Guava Cache and store in local JVM Memory 
	
	public static int LOCAL_CACHE_ENGINE = 1;
	public static int MEMCACHE_CACHE_ENGINE = 2;
	public static int REDIS_CACHE_ENGINE = 3;
}