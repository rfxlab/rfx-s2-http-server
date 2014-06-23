package rfx.server.http;

import rfx.server.util.template.MustacheObjectModel;

/**
 * the base class for HTTP processor, input: HttpRequest output: processed model object
 * 
 * @author Trieu.nguyen
 *
 */
public abstract class HttpProcessor {
	
	/**
	 * always called by HttpProcessorManager
	 * 
	 * @return processed model object
	 */
	public BaseModel doProcessing(HttpRequestEvent requestEvent) {
		//TODO support hooking, filtering HttpRequestEvent
		
		//call the implemented process method
		return process(requestEvent);
	}
	
	
	protected MustacheObjectModel createModel() {		
		return new MustacheObjectModel();
	}	
	
	protected MustacheObjectModel createModel(int numberField) {		
		return new MustacheObjectModel(numberField);
	}	
	
	///////////// for the implementation class /////////////
	protected abstract BaseModel process(HttpRequestEvent event);

}
