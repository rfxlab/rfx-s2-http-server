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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;

import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.StringPool;



public class RountingHttpProcessorHandler extends SimpleChannelInboundHandler<Object> {
	
	private static final Map<String, HttpProcessor> handlers = new ConcurrentHashMap<>();
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
            		
            		HttpProcessor httpProcessor = handlers.get(mapper.uriPath());
            		if( httpProcessor == null ){            			
    					try {
    						httpProcessor = (HttpProcessor) clazz.newInstance();
    						httpProcessor.setContentType(mapper.contentType());
    						httpProcessor.setTemplatePath(mapper.templatePath());
    						if( ! StringPool.BLANK.equals(mapper.uriPath()) ){
    							handlers.put(mapper.uriPath(), httpProcessor);
    							System.out.println("...registered controller class: "+ clazz.getName() + " ;uriPath:"+mapper.uriPath()+" ;tpl:"+mapper.templatePath()+ " ;content-type"+mapper.contentType());
    						} else if( ! StringPool.BLANK.equals(mapper.uriPattern()) ){
    							handlers.put(mapper.uriPattern(), httpProcessor);
    							System.out.println("...registered controller class: "+ clazz.getName() + " ;uriPattern:"+mapper.uriPattern()+" ;tpl:"+mapper.templatePath()+ " ;content-type"+mapper.contentType());
    						} else {
    							throw new IllegalArgumentException("the class "+clazz.getName() + " is missing uriPath or uriPattern config");
    						}
    						
    						
    					} catch (InstantiationException e) {
    						e.printStackTrace();
    					} catch (IllegalAccessException e) {
    						e.printStackTrace();
    					}
            		}
            	}        	        	
	        }
	}
	
	static HttpProcessor routingForUriPath(QueryStringDecoder qDecoder){
		HttpProcessor httpProcessor = handlers.get(qDecoder.path());
		return httpProcessor;
	}
	
	static HttpProcessor routingForUriPattern(QueryStringDecoder qDecoder){
		String[] toks = qDecoder.path().split("/");
		if(toks.length>1){
			HttpProcessor httpProcessor = handlers.get(toks[2]);
			return httpProcessor;
		}
		return null;
	}
	
	static FullHttpResponse callProcessor(HttpProcessor httpProcessor, String ip, String uri, Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request  ){
		FullHttpResponse response;
		try {
			response = httpProcessor.injectContext(ip, uri, params, ctx, request).doProcessing();
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
	
	public RountingHttpProcessorHandler(){}	

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
				
				HttpProcessor httpProcessor = routingForUriPath(qDecoder);
				if(httpProcessor != null){
					response = callProcessor(httpProcessor, ip, uri, params, ctx, request);
				} else {
					httpProcessor = routingForUriPattern(qDecoder);
					if(httpProcessor != null){
						response = callProcessor(httpProcessor, ip, uri, params, ctx, request);	
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
