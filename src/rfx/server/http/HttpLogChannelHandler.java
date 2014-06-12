package rfx.server.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rfx.server.http.common.AccessLogUtil;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.LogUtil;
import rfx.server.util.StringPool;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;



public class HttpLogChannelHandler extends SimpleChannelInboundHandler<Object> {
	
	static class Model {
		List<Item> items() {
			return Arrays.asList(
					new Item("Item 1", "$19.99", Arrays.asList(new Feature(
							"New!"), new Feature("Awesome!"))),
					new Item("Item 2", "$29.99", Arrays.asList(new Feature(
							"Old."), new Feature("Ugly."))));
		}

		String data = "this is a text";
	}

	static class Item {
		Item(String name, String price, List<Feature> features) {
			this.name = name;
			this.price = price;
			this.features = features;
		}

		String name, price;
		List<Feature> features;
	}

	static class Feature {
		Feature(String description) {
			this.description = description;
		}

		String description;
	}
		
	static class TestHttpEventProcessor extends HttpProcessor {			
		@Override
		public String handler() {	
			StringWriter stringWriter = new StringWriter();
			try {
				mustache.execute(stringWriter, new Model()).flush();
				stringWriter.flush();
				stringWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			};	
			return stringWriter.toString();
		}
	}
	
	static final MustacheFactory mf = new DefaultMustacheFactory();
	static final Mustache mustache = mf.compile("resources/tpl/template.mustache");
	static final Map<String, HttpProcessor> handlers = new HashMap<>();
	static {
		handlers.put("/ad-delivery", new TestHttpEventProcessor());
	}
	
	public HttpLogChannelHandler(){}	

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
				try {
					AccessLogUtil.logAccess(request, ipAddress, uri);
					response = UriMapper.buildHttpResponse(ipAddress,ctx,request , uri);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.error("HttpLogChannelHandler", e.getMessage());
				}
				if(response == null){
					if(uri.equalsIgnoreCase("/ad-delivery")){
						response = handlers.get(uri).init(ipAddress, uri, ctx, request).process();
					} else {
						response = NettyHttpUtil.theHttpContent(StringPool.NOT_SUPPORT);	
					}					
				}
				
				// Write the response.				
		        ChannelFuture future = ctx.write(response);
		        ctx.flush();
		        ctx.close();
				 
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
    	ctx.flush();
        ctx.close(); 
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.flush();
        ctx.close();   
	}
}
