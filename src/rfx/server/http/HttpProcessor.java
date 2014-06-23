package rfx.server.http;


/**
 * the base class for HTTP processor, input: HttpRequest output: processed model object
 * 
 * @author Trieu.nguyen
 *
 */
public abstract class HttpProcessor {
	
	public static final BaseModel EMPTY_MODEL = new BaseModel() {		
		@Override
		public boolean isOutputableText() {			
			return false;
		}		
		@Override
		public void freeResource() {}
		@Override
		public String classpath() {			
			return BaseModel.class.getName();
		}
	};
	
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
	

	
	///////////// for the implementation class /////////////
	protected abstract BaseModel process(HttpRequestEvent event);

}
