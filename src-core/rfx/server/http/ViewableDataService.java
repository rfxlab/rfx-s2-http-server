package rfx.server.http;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 * the base model for HttpProcessor implementations, that use Template Engine for outputable text
 * 
 * @author trieu <br>
 *
 */
public abstract class ViewableDataService implements DataService{	
		
	public abstract ViewableDataService build();
	protected List<HttpHeaders> httpHeaders = new ArrayList<>();		
	
	@Override
	public boolean isOutputable() {	
		return true;
	}
	
	@Override
	public String getClasspath() {
		return DataService.getClasspath(this);
	}
	
	@Override
	public void freeResource() {}
	
	@Override
	public List<HttpHeaders> getHttpHeaders() {
		return httpHeaders;
	}
}
