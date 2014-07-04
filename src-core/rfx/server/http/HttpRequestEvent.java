package rfx.server.http;

import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import rfx.server.http.common.NettyHttpUtil;

/**
 * the HTTP data request
 * 
 * @author trieu
 *
 */
public class HttpRequestEvent implements Serializable{

	private static final long serialVersionUID = 4820504738374857535L;
	final static String REGEX_FOR_ROOT_DOMAIN = ".*\\.(?=.*\\.)";
	
	String ipAddress;
	String uriPath; 
	Map<String, List<String>> params;
	HttpRequest request;
	
	public HttpRequestEvent(String ipAddress, String uriPath, Map<String, List<String>> params, HttpRequest request) {
		super();
		this.ipAddress = ipAddress;
		this.uriPath = uriPath;
		this.params = params;
		this.request = request;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUriPath() {
		return uriPath;
	}

	public Map<String, List<String>> getParams() {
		return params;
	}

	public HttpRequest getRequest() {
		return request;
	}
	
	public String getHost(){
		return request.headers().get(Names.HOST);
	}
	
	public String getRootDomain(){
		return getHost().replaceAll(REGEX_FOR_ROOT_DOMAIN, "");
	}
	
	public String param(String name){
		return NettyHttpUtil.getParamValue(name, params);
	}
	
	public String param(String name, String defaultVal){
		return NettyHttpUtil.getParamValue(name, params, defaultVal);
	}
			
	public void clear(){
		this.ipAddress = null;
		this.uriPath = null;
		this.params = null;
		this.request = null;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(" ipAddress: ").append(ipAddress);
		s.append(" uriPath: ").append(uriPath);
		s.append(" params: ").append(params);		
		return s.toString();
	}
}
