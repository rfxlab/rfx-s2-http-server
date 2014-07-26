package rfx.server.util.cache;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.reflections.Reflections;

import rfx.server.util.StringPool;
import rfx.server.util.StringUtil;

import com.google.common.cache.LoadingCache;

/**
 * How to use Spring AOP http://www.journaldev.com/2583/spring-aop-example-tutorial-aspect-advice-pointcut-joinpoint-annotations-xml-configuration
 * 
 * @author Trieu.nguyen
 *
 */
@Aspect
public class CacheManagerForAllDAO {
	
	final static String daoClasspath = "sample.pollapp.business.dao";
	final static String withinClasspath = "within("+daoClasspath+".*)";
	
	final static Map<String, CachePool> signatureConfigCache = new HashMap<>();

	//TODO use Memcache here
	static boolean cacheAllMethodsInDAO = true;
	
	public CacheManagerForAllDAO() {
		System.out.println("---CacheManagerForAllDAO---");
	}
		
	@Around(withinClasspath)
    public Object process(ProceedingJoinPoint pJoinPoint){
		try {
			long maximumSize = 10000;
			long expireAfter = 8;
			
			if(cacheAllMethodsInDAO){
				Object value = null;
//		        System.out.println(" ---------Before invoking ---------- ");
		        
		        //String key = pJoinPoint.getSignature().getName() + HashUtil.hashUrlCrc64(Arrays.toString(pJoinPoint.getArgs()));
				String key = StringUtil.toString(pJoinPoint.getSignature().getName(), StringPool.UNDERLINE, StringUtil.join(pJoinPoint.getArgs(),StringPool.UNDERLINE));
		        
		        LoadingCache<String, Object> cache = GuavaCacheUtil.getLoadingCache(pJoinPoint.getTarget(), maximumSize, expireAfter);
	        	value = cache.get(key);	        	
	        	System.out.println("++ Target: "+pJoinPoint.getTarget().getClass().getName());
	        	System.out.println("++ Signature: "+pJoinPoint.getSignature().getName());
	        	
	        	System.out.println("++ call method=" + pJoinPoint.getSignature().getName());
        		System.out.println("++ Agruments Passed=" + Arrays.toString(pJoinPoint.getArgs()));
	        	
	        	if(StringUtil.isEmpty(value)){	        		
	                value = pJoinPoint.proceed();	                
	                cache.put(key, value);
	        	} else {
	        		System.out.println("Hit cache by key: " + key );
	        	}
	        	//System.out.println(" value: "+value);
	        	
		        
//		        System.out.println("After invoking process method. Return value="+value);
//		        System.out.println(" -------------------------------- ");
		        return value;	
			}
			return pJoinPoint.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		return null;
    }

//	@Before("within(ambient.delivery.business.dao.*)")
//	public void logStringArguments(JoinPoint joinPoint) {
//		System.out.println("Before running loggingAdvice on method=" + joinPoint.toString());
//		System.out.println("Agruments Passed=" + Arrays.toString(joinPoint.getArgs()));
//
//	}
	
	static class CachePool {
		LoadingCache<String, Object> cache;
		String keyPrefix;
		 
		
		 
		public CachePool(LoadingCache<String, Object> cache, String keyPrefix) {
			super();
			this.cache = cache;
			this.keyPrefix = keyPrefix;
		}
		
		public LoadingCache<String, Object> getCache() {
			return cache;
		}
		public void setCache(LoadingCache<String, Object> cache) {
			this.cache = cache;
		}
		public String getKeyPrefix() {
			return keyPrefix;
		}
		public void setKeyPrefix(String keyPrefix) {
			this.keyPrefix = keyPrefix;
		}
		 
		public String buildKey(String signatureName, Object[] args){
			if(StringUtil.isEmpty(keyPrefix)){
				return StringUtil.toString(signatureName, StringPool.UNDERLINE, StringUtil.join(args,StringPool.UNDERLINE));
			}
			return StringUtil.toString(keyPrefix, signatureName, StringPool.UNDERLINE, StringUtil.join(args,StringPool.UNDERLINE));
		}
	}

	public static void init() throws Exception{
		Reflections reflections = new Reflections(daoClasspath);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CacheConfig.class);			
		for (Class<?> clazz : classes) {
			String className = clazz.getName();
			if (clazz.isAnnotationPresent(CacheConfig.class) ) {
				Annotation annotation = clazz.getAnnotation(CacheConfig.class);
				CacheConfig cacheConfig = (CacheConfig) annotation;
				
				long maximumSize = cacheConfig.maximumSize() > 0 ? cacheConfig.maximumSize() : 1000000;
				long expireAfter = cacheConfig.expireAfter() > 0 ? cacheConfig.expireAfter() : 10;
				String keyPrefix = cacheConfig.keyPrefix();
				int type = cacheConfig.type();
							
				
				if(type == CacheConfig.LOCAL_CACHE_ENGINE){	
					LoadingCache<String, Object> cache = GuavaCacheUtil.getLoadingCache(className, maximumSize, expireAfter );
					signatureConfigCache.put(className, new CachePool(cache, keyPrefix));
				}
				
				System.out.println("...registered signatureConfigCache:" + className);
			}			
		}
	}
	
	public static void main(String[] args) throws Exception {
		init();
	}
	
}
