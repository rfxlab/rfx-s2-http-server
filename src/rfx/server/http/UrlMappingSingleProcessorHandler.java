package rfx.server.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;

import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.StringPool;



public class UrlMappingSingleProcessorHandler extends SimpleChannelInboundHandler<Object> {
	
	private static final Map<String, HttpProcessorManager> handlers = new HashMap<>();
	final static String BASE_CONTROLLER_PACKAGE = "rfx.server.http.processor";
	
	/**
	 * run at server start-up
	 */
	public static void initHandlers() {		
		Reflections reflections = new Reflections(BASE_CONTROLLER_PACKAGE);
		Set<Class<?>> clazzes =  reflections.getTypesAnnotatedWith(HttpProcessorMapper.class);
	    for (Class<?> clazz : clazzes) {        		
			if (clazz.isAnnotationPresent(HttpProcessorMapper.class)) {        		     
				Annotation annotation = clazz.getAnnotation(HttpProcessorMapper.class);
				HttpProcessorMapper mapper = (HttpProcessorMapper) annotation;
				
				HttpProcessorManager manager = handlers.get(mapper.uriPath());
				if( manager == null ){
					manager = new HttpProcessorManager(mapper.contentType(), mapper.templatePath(), clazz);						
					if( ! StringPool.BLANK.equals(mapper.uriPath()) ){
							handlers.put(mapper.uriPath(), manager);
							System.out.println("...registered controller class: "+ clazz.getName() + " ;uriPath:"+mapper.uriPath()+" ;tpl:"+mapper.templatePath()+ " ;content-type"+mapper.contentType());
					} 
					else if( ! StringPool.BLANK.equals(mapper.uriPattern()) ){
						handlers.put(mapper.uriPattern(), manager);
						System.out.println("...registered controller class: "+ clazz.getName() + " ;uriPattern:"+mapper.uriPattern()+" ;tpl:"+mapper.templatePath()+ " ;content-type"+mapper.contentType());
					} 
					else {
						throw new IllegalArgumentException("the class "+clazz.getName() + " is missing uriPath or uriPattern config");
					}    				
				}
			}        	        	
	    }
	}
	
	static HttpProcessorManager routingForUriPath(QueryStringDecoder qDecoder){		
		return handlers.get(qDecoder.path());
	}
	
	static int PATTERN_INDEX = 2;
	static HttpProcessorManager routingForUriPattern(QueryStringDecoder qDecoder){
		String[] toks = qDecoder.path().split("/");
		if(toks.length>PATTERN_INDEX){
			String pathPattern = toks[PATTERN_INDEX];	
			if(handlers.containsKey(pathPattern)){				
				return handlers.get(pathPattern);
			}			
		}
		return null;
	}
	
	static FullHttpResponse callProcessor(HttpProcessorManager processorManager, String ip, String uri, Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request  ){
		FullHttpResponse response;
		try {
			response = processorManager.doProcessing(ip, uri, params, ctx, request);
		} catch (Exception e) {
			StringBuilder s = new StringBuilder("Error###");
			s.append(e.getMessage());
			s.append(" ### <br>\n StackTrace: ").append(ExceptionUtils.getStackTrace(e));							
			response = NettyHttpUtil.theHttpContent( s.toString() );	
		} finally {
			//httpProcessor.clear();
		}
		return response;
	}
	
	public UrlMappingSingleProcessorHandler(){}	

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {    	
        if (msg instanceof HttpRequest) {        	
        	HttpRequest request = (HttpRequest) msg;
        	//TODO filter DDOS/bad/attacking requests 
        	        	
        	String uri = request.getUri();
        	String ip = NettyHttpUtil.getRequestIP(ctx, request);
        	
        	//System.out.println("===> uri: " + uri);
    		if (uri.equalsIgnoreCase(NettyHttpUtil.FAVICON_URI)) {
    			NettyHttpUtil.returnImage1pxGifResponse(ctx);
    		} else {
    			FullHttpResponse response = null;
    			//TODO access log
//				try {
//					AccessLogUtil.logAccess(request, ipAddress, uri);
//					response = UriMapper.buildHttpResponse(ipAddress,ctx,request , uri);
//				} catch (Exception e) {
//					e.printStackTrace();
//					LogUtil.error("HttpLogChannelHandler", e.getMessage());
//				}
				
				QueryStringDecoder qDecoder = new QueryStringDecoder(uri);
				Map<String, List<String>> params = qDecoder.parameters();
//					System.out.println(queryDecoder.path());
//					System.out.println(queryDecoder.parameters());
				
				HttpProcessorManager processorManager = routingForUriPath(qDecoder);
				if(processorManager != null){
					response = callProcessor(processorManager, ip, uri, params, ctx, request);
				} else {
					processorManager = routingForUriPattern(qDecoder);
					if(processorManager != null){
						response = callProcessor(processorManager, ip, uri, params, ctx, request);	
					} else {
						String s = "Not found HttpProcessor for URI: "+uri;
						response = NettyHttpUtil.theHttpContent(s, HttpResponseStatus.NOT_FOUND);
					}
				}
				
				// Write the response.				
		        ChannelFuture future = ctx.write(response);
		        ctx.flush().close();
				 
				//Close the non-keep-alive connection after the write operation is done.
				future.addListener(ChannelFutureListener.CLOSE);
    		}
        }

        if (msg instanceof HttpContent) {
            if (msg instanceof LastHttpContent) {                
            	NettyHttpUtil.returnImage1pxGifResponse(ctx);
            }
        }
    }    
   
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	ctx.flush();
    }       
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	ctx.flush().close(); 
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.flush().close();   
	}
}