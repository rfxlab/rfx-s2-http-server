package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.StringPool;

public abstract class HttpProcessor {
	String ipAddress;
	String uri;
	ChannelHandlerContext ctx;
	HttpRequest request;
	FullHttpResponse response;
			
	public HttpProcessor init(String ipAddress, String uri, ChannelHandlerContext ctx, HttpRequest request){
		this.ipAddress = ipAddress;
		this.uri = uri;
		this.ctx = ctx;
		this.request = request;
		return this;
	}
	public FullHttpResponse process(){
		if(ctx == null ){
			throw new IllegalArgumentException("init must be called before process");
		}
		//System.out.println("IP:"+ipAddress);
		String rs = handler();
		if(rs != null){
			return NettyHttpUtil.theHttpContent(rs);
		} else {
			return NettyHttpUtil.theHttpContent(StringPool.BLANK);
		}
	}
	protected abstract String handler();		
}
