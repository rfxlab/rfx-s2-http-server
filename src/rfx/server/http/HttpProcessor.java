package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

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
	protected String ipAddress;
	protected String path;
	protected Map<String, List<String>> params;
	protected String contentType;
	protected ChannelHandlerContext ctx;
	protected HttpRequest request;
	protected FullHttpResponse response;

	private String templatePath;
	

	public HttpProcessor init(String ipAddress, String path, Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request) {
		this.ipAddress = ipAddress;
		this.path = path;
		this.params = params;
		this.ctx = ctx;
		this.request = request;
		return this;
	}

	/**
	 * always called by RountingHttpProcessorHandler.channelRead0
	 * 
	 * @return FullHttpResponse
	 */
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
	protected String param(String name){
		return NettyHttpUtil.getParamValue(name, params);
	}
	protected String param(String name, String defaultVal){
		return NettyHttpUtil.getParamValue(name, params, defaultVal);
	}
	///////////// for the implementation class /////////////

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUri() {
		return path;
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
	
	public void clear(){
		this.request = null;
		this.params.clear();
		this.path = null;
	}

}
