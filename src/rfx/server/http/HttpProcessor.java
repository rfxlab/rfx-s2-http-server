package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import rfx.server.configs.ErrorMessagePool;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.MustacheUtil;
import rfx.server.util.StringPool;

/**
 * @author Trieu.nguyen
 * 
 * the base class for HTTP processor, input: HttpRequest output:FullHttpResponse
 *
 */
public abstract class HttpProcessor {
	String ipAddress;
	String uri;
	String contentType;
	ChannelHandlerContext ctx;
	HttpRequest request;
	FullHttpResponse response;

	private String templatePath;

	public HttpProcessor init(String ipAddress, String uri,
			ChannelHandlerContext ctx, HttpRequest request) {
		this.ipAddress = ipAddress;
		this.uri = uri;
		this.ctx = ctx;
		this.request = request;
		return this;
	}

	public FullHttpResponse doProcessing() {
		if (ctx == null) {
			throw new IllegalArgumentException(ErrorMessagePool.INIT_BEFORE_PROCESS);
		}
		// System.out.println("IP:"+ipAddress);
		String rs = handler();
//		System.out.println(rs);
		if (rs != null) {
			return NettyHttpUtil.theHttpContent(rs, contentType);
		} else {
			return NettyHttpUtil.theHttpContent(StringPool.BLANK);
		}
	}

	///////////// for the implementation class /////////////
	protected abstract String handler();	
	protected String render(Object model) {
		return MustacheUtil.execute(templatePath, model);
	}	
	///////////// for the implementation class /////////////

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUri() {
		return uri;
	}

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public FullHttpResponse getResponse() {
		return response;
	}

	public String getTemplatePath() {
		return templatePath;
	}
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

}
