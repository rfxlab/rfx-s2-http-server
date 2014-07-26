package rfx.server.util.cache;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import rfx.server.util.HashUtil;
import rfx.server.util.StringPool;
import rfx.server.util.cache.GuavaCacheUtil;

import com.google.common.cache.LoadingCache;

/**
 * How to use Spring AOP http://www.journaldev.com/2583/spring-aop-example-tutorial-aspect-advice-pointcut-joinpoint-annotations-xml-configuration
 * 
 * @author Trieu.nguyen
 *
 */
@Aspect
public class CacheManagerForAllDAO {

	//TODO use Memcache here
	static boolean cacheAllMethodsInDAO = true;
	
	public CacheManagerForAllDAO() {
		System.out.println("---CacheManagerForAllDAO---");
	}
		
	@Around("within(sample.pollapp.business.dao.*)")
    public Object process(ProceedingJoinPoint pJoinPoint){
		try {
			long maximumSize = 10000;
			long expireAfter = 8;
			LoadingCache<String, Object> cache = GuavaCacheUtil.getLoadingCache(pJoinPoint.getTarget(), maximumSize, expireAfter);
			if(cacheAllMethodsInDAO){
				Object value = null;
//		        System.out.println(" ---------Before invoking ---------- ");
		        
		        String key = pJoinPoint.getSignature().getName() + HashUtil.hashUrlCrc64(Arrays.toString(pJoinPoint.getArgs()));
	        	value = cache.get(key);	        	
	        	//System.out.println("Target: "+pJoinPoint.getTarget());
	        	
	        	if(StringPool.BLANK.equals(value)){
	        		//System.out.println("+Before call method=" + pJoinPoint.getSignature().getName());
	        		//System.out.println("++Agruments Passed=" + Arrays.toString(pJoinPoint.getArgs()));
	                value = pJoinPoint.proceed();	                
	                cache.put(key, value);
	        	} else {
	        		System.out.println("Hit cache by key:" + key );
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

	
}
