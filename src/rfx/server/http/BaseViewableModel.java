package rfx.server.http;

/**
 * 
 * the base model for HttpProcessor implementations
 * 
 * @author trieu <br>
 *
 */
public abstract class BaseViewableModel implements BaseModel{	
		
	public abstract BaseViewableModel prepareData();
		
	
	@Override
	public boolean isOutputableText() {	
		return true;
	}
}
