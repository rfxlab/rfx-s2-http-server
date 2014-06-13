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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;

import rfx.server.http.common.NettyHttpUtil;



public class RountingHttpProcessorHandler extends SimpleChannelInboundHandler<Object> {
	
	static final Map<String, HttpProcessor> handlers = new HashMap<>();
	final static String BASE_CONTROLLER_PACKAGE = "rfx.server.http.processor";
	
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
    						handlers.put(mapper.uriPath(), httpProcessor);
    						System.out.println("...registered controller class: "+ clazz.getName() + " ;uri:"+mapper.uriPath()+" ;tpl:"+mapper.templatePath()+ " ;content-type"+mapper.contentType());
    					} catch (InstantiationException e) {
    						e.printStackTrace();
    					} catch (IllegalAccessException e) {
    						e.printStackTrace();
    					}
            		}
            	}        	        	
			}
	}
	
	public RountingHttpProcessorHandler(){}	

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {    	
        if (msg instanceof HttpRequest) {        	
        	HttpRequest request = (HttpRequest) msg;
        	//TODO filter DDOS/bad/attacking requests 
        	        	
        	String uri = request.getUri();
        	String ipAddress = NettyHttpUtil.getRequestIP(ctx, request);
        	
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
				if(response == null){
					QueryStringDecoder queryDecoder = new QueryStringDecoder(uri);
//					System.out.println(queryDecoder.path());
//					System.out.println(queryDecoder.parameters());
					
					HttpProcessor httpProcessor = handlers.get(queryDecoder.path());
					if(httpProcessor != null){
						try {
							response = httpProcessor.init(ipAddress, uri, queryDecoder.parameters(), ctx, request).doProcessing();
						} catch (Exception e) {
							StringBuilder s = new StringBuilder("Error###");
							s.append(e.getMessage());
							s.append(" ### <br>\n StackTrace: ").append(ExceptionUtils.getStackTrace(e));							
							response = NettyHttpUtil.theHttpContent( s.toString() );	
						} finally {
							httpProcessor.clear();
						}
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
