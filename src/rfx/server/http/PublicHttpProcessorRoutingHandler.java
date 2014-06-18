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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rfx.server.http.common.NettyHttpUtil;



public class PublicHttpProcessorRoutingHandler extends SimpleChannelInboundHandler<Object> {
	
	private static final Map<String, HttpProcessorManager> handlers = new HashMap<>();
	final static String BASE_CONTROLLER_PACKAGE = "rfx.server.http.processor";
	public static final int PATTERN_INDEX = 2;
	public static int DEFAULT_MAX_POOL_SIZE = 20000;
		
	public PublicHttpProcessorRoutingHandler(){}
	
	public static void init() throws Exception{
		handlers.putAll(HttpProcessorManager.loadHandlers(BASE_CONTROLLER_PACKAGE, HttpProcessorConfig.PUBLIC_ACCESS, DEFAULT_MAX_POOL_SIZE));
	}

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
				
				HttpProcessorManager processorManager = HttpProcessorManager.routingForUriPath(handlers,qDecoder);
				if(processorManager != null){
					HttpRequestEvent requestEvent = new HttpRequestEvent(ip, uri, params, request);
					response = processorManager.doProcessing(requestEvent);
				} else {
					processorManager = HttpProcessorManager.routingForUriPattern(handlers,qDecoder, PATTERN_INDEX);
					if(processorManager != null){
						HttpRequestEvent requestEvent = new HttpRequestEvent(ip, uri, params, request);
						response = processorManager.doProcessing(requestEvent);
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