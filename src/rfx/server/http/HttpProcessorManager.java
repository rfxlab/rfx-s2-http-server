package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.StringPool;
import rfx.server.util.template.MustacheUtil;

/**
 * @author Trieu.nguyen
 * 
 * the manager, the factory for HTTP processor instances, input: HttpRequest output: FullHttpResponse
 *
 */
public class HttpProcessorManager {
	
	private String contentType;
	private String templatePath;
	Class<?> httpProcessorClass;
	
	
	
	public HttpProcessorManager(String contentType, String templatePath,
			Class<?> httpProcessorClass) {
		super();
		this.contentType = contentType;
		this.templatePath = templatePath;
		this.httpProcessorClass = httpProcessorClass;
	}

	/**
	 * always called by RountingHttpProcessorHandler.channelRead0
	 * 
	 * @return FullHttpResponse
	 */
	public FullHttpResponse doProcessing(String ipAddress, String path,	Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request) {		
		
		HttpProcessor httpProcessor;
		String rs = null;
		try {
			httpProcessor = (HttpProcessor) httpProcessorClass.newInstance();
			Object model = httpProcessor.doProcessing(ipAddress, path, params, ctx, request);			
			rs = render(model);			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO log the result 
		
		FullHttpResponse response;
		if (rs != null) {
			response = NettyHttpUtil.theHttpContent(rs, contentType);
		} else {
			response = NettyHttpUtil.theHttpContent(StringPool.BLANK);
		}
		return response;
	}
	
	protected String render(Object model) {
		return MustacheUtil.execute(templatePath, model);
	}
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public String getTemplatePath() {
		return templatePath;
	}
	
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}	


}
