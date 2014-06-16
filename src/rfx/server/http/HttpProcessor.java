package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

import rfx.server.configs.ErrorMessagePool;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.StringPool;
import rfx.server.util.template.MustacheUtil;

/**
 * @author Trieu.nguyen
 * 
 * the base class for HTTP processor, input: HttpRequest output:FullHttpResponse
 *
 */
public abstract class HttpProcessor {
	protected String ipAddress;
	protected String path;	
	protected String contentType;
	protected ChannelHandlerContext ctx;
	protected HttpRequest request;
	protected Map<String, List<String>> params;
	protected FullHttpResponse response;

	private String templatePath;
	
	

	public HttpProcessor injectContext(String ipAddress, String path, Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request) {
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
		String rs = process();
//		System.out.println(rs);
		this.clear();
		if (rs != null) {
			return NettyHttpUtil.theHttpContent(rs, contentType);
		} else {
			return NettyHttpUtil.theHttpContent(StringPool.BLANK);
		}
	}
	
	///////////// for the implementation class /////////////
	protected abstract String process();	
	protected String render(Object model) {
		return MustacheUtil.execute(templatePath, model);
	}
	
	protected String param(String name){
		return NettyHttpUtil.getParamValue(name, params);
	}
	
	protected String param(String name, String defaultVal){
		return NettyHttpUtil.getParamValue(name, params, defaultVal);
	}
	
	protected String getHost(){
		return request.headers().get(Names.HOST);
	}
	
	protected String getDomain(){
		return getHost().replaceAll(".*\\.(?=.*\\.)", "");
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
		this.params.clear();
		this.request = null;		
		this.path = null;
		this.ctx = null;		
		this.ipAddress = null;
	}

}
