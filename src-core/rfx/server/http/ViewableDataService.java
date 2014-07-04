package rfx.server.http;

/**
 * 
 * the base model for HttpProcessor implementations, that use Template Engine for outputable text
 * 
 * @author trieu <br>
 *
 */
public abstract class ViewableDataService implements DataService{	
		
	public abstract ViewableDataService build();
		
	
	@Override
	public boolean isProcessable() {	
		return true;
	}
	
	@Override
	public String getClasspath() {
		return DataService.getClasspath(this);
	}
	
	@Override
	public void freeResource() {}
}
