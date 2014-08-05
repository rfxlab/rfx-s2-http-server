package rfx.server.util.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.reflections.Reflections;

import rfx.server.util.StringUtil;

import com.google.common.cache.LoadingCache;

public abstract class CacheManager {
	protected final static Map<String, CachePool> signatureConfigCache = new HashMap<>();
	protected final static Map<String, Boolean> globalCachableMethods = new HashMap<>();
	
	//TODO use Memcache here
	protected static boolean cacheAllMethodsInDAO = true;
	
	
	public static void init(String daoClasspath) throws Exception{
		Reflections reflections = new Reflections(daoClasspath);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CacheConfig.class);			
		for (Class<?> clazz : classes) {
			String className = clazz.getName();
			if (clazz.isAnnotationPresent(CacheConfig.class) ) {
				Method[] methods = clazz.getMethods();
				Map<String, Long> cachableMethods = new HashMap<>(methods.length);
				for (Method method : methods) {
					if(method.isAnnotationPresent(Cachable.class)){
						Annotation am = method.getAnnotation(Cachable.class);
						Cachable cachable = (Cachable) am;
						
						String mkey;
						if( cachable.keyFormat().isEmpty() ){
							mkey = method.getName(); 
						} else {
							mkey = cachable.keyFormat();//TODO
						}
						if(cachableMethods.containsKey(mkey)){
							throw new IllegalArgumentException("duplicated cachable method key at class:"+className + " method:" + mkey);
						}
						cachableMethods.put(mkey, cachable.expireAfter());
					}
				}
				Annotation annotation = clazz.getAnnotation(CacheConfig.class);
				CacheConfig cacheConfig = (CacheConfig) annotation;
				
				long maximumSize = cacheConfig.maximumSize() > 0 ? cacheConfig.maximumSize() : 1000000;
				long expireAfter = cacheConfig.expireAfter() > 0 ? cacheConfig.expireAfter() : 10;
				String keyPrefix = cacheConfig.keyPrefix();
				int type = cacheConfig.type();							
				
				if(type == CacheConfig.LOCAL_CACHE_ENGINE){	
					LoadingCache<String, Object> cacheImpl = GuavaCacheUtil.getLoadingCache(className, maximumSize, expireAfter );
					signatureConfigCache.put(className, new CachePool(cacheImpl, keyPrefix, cachableMethods, expireAfter));
				} else if(type == CacheConfig.MEMCACHE_CACHE_ENGINE){
					LoadingCache<String, Object> cacheImpl = new MemcacheLoadingImpl();
					signatureConfigCache.put(className, new CachePool(cacheImpl, keyPrefix, cachableMethods, expireAfter));
				}
				
				System.out.println("...registered signatureConfigCache:" + className);
			}			
		}
	}
	
	protected Object processMethod(ProceedingJoinPoint pJoinPoint) throws Throwable{
		String className = pJoinPoint.getTarget().getClass().getName();
		CachePool cachePool = signatureConfigCache.get(className);
		if(cachePool != null){
			Object value = null;
//	        System.out.println(" ---------Before invoking ---------- ");
	        
	        //String key = pJoinPoint.getSignature().getName() + HashUtil.hashUrlCrc64(Arrays.toString(pJoinPoint.getArgs()));
		
			Signature method = pJoinPoint.getSignature();
			String methodName = method.getName();
			Object[] args = pJoinPoint.getArgs();
			long expireAfter = cachePool.getExpireAfter(methodName);
			if(expireAfter > 0){
				System.out.println(className +" " +methodName + " " + cachePool);
				String key = cachePool.buildKey(methodName, args);
		        LoadingCache<String, Object> cache = cachePool.getCache();
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
			} else {
				value = pJoinPoint.proceed();
			}				
        	//System.out.println(" value: "+value);
        	
	        return value;	
		}
		return pJoinPoint.proceed();
	}
}
