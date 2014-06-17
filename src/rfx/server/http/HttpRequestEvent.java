package rfx.server.http;

import io.netty.handler.codec.http.HttpRequest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class HttpRequestEvent implements Serializable{

	private static final long serialVersionUID = 4820504738374857535L;
	
	String ipAddress;
	String uriPath; 
	Map<String, List<String>> params;
	HttpRequest request;
	
	public HttpRequestEvent(String ipAddress, String uriPath,	Map<String, List<String>> params, HttpRequest request) {
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
	
	
}
