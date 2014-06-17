package rfx.server.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

import rfx.server.configs.ErrorMessagePool;
import rfx.server.http.common.NettyHttpUtil;
import rfx.server.util.template.MustacheObjectModel;

/**
 * @author Trieu.nguyen
 * 
 * the base class for HTTP processor, input: HttpRequest output: processed model object
 *
 */
public abstract class HttpProcessor {
	
	//for implementer access
	protected String ipAddress;
	protected String path;
	protected ChannelHandlerContext ctx;
	protected HttpRequest request;
	protected Map<String, List<String>> params;

	/**
	 * always called by HttpProcessorManager
	 * 
	 * @return processed model object
	 */
	public Object doProcessing(String ipAddress, String path, Map<String, List<String>> params, ChannelHandlerContext ctx, HttpRequest request) {
		if (ctx == null) {
			throw new IllegalArgumentException(ErrorMessagePool.INIT_BEFORE_PROCESS);
		}
		this.ipAddress = ipAddress;
		this.path = path;
		this.params = params;
		this.ctx = ctx;
		this.request = request;	
			
		//call the implemented process method
		return process();
	}
	
	
	protected MustacheObjectModel createModel() {		
		return new MustacheObjectModel();
	}	
	
	protected MustacheObjectModel createModel(int numberField) {		
		return new MustacheObjectModel(numberField);
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
	
	public String getIpAddress() {
		return ipAddress;
	}

	public String getUri() {
		return path;
	}

	public HttpRequest getRequest() {
		return request;
	}
	
	@Override
	protected void finalize() throws Throwable {		
		super.finalize();
		if(this.params != null){
			this.params.clear();
		}
		this.request = null;		
		this.path = null;
		this.ctx = null;		
		this.ipAddress = null;
	}
	
	
	///////////// for the implementation class /////////////
	protected abstract Object process();

}
