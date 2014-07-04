package rfx.server.http;


/**
 * the base class for HTTP processor, input: HttpRequest output: processed model object
 * 
 * @author Trieu.nguyen
 *
 */
public abstract class HttpProcessor {
	
	public static final DataService EMPTY_MODEL = new DataService() {		
		@Override
		public boolean isProcessable() {			
			return false;
		}		
		@Override
		public void freeResource() {}
		@Override
		public String getClasspath() {			
			return DataService.class.getName();
		}
	};
	
	/**
	 * always called by HttpProcessorManager
	 * 
	 * @return processed model object
	 */
	public DataService doProcessing(HttpRequestEvent requestEvent) {
		//TODO support hooking, filtering HttpRequestEvent
		
		//call the implemented process method
		return process(requestEvent);
	}
	

	
	///////////// for the implementation class /////////////
	protected abstract DataService process(HttpRequestEvent event);

}
