package rfx.server.http;

import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;

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
	protected String uriPath;
	protected HttpRequest request;
	protected Map<String, List<String>> params;

	/**
	 * always called by HttpProcessorManager
	 * 
	 * @return processed model object
	 */
	public BaseModel doProcessing(HttpRequestEvent requestEvent) {
		
		this.ipAddress = requestEvent.getIpAddress();
		this.uriPath = requestEvent.getUriPath();
		this.params = requestEvent.getParams();
		this.request = requestEvent.getRequest();	
			
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

	public String getUriPath() {
		return uriPath;
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
		this.uriPath = null;	
		this.ipAddress = null;
	}
	
	
	///////////// for the implementation class /////////////
	protected abstract BaseModel process();

}
