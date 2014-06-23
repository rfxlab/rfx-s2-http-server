package rfx.server.http;

/**
 * the base model for HttpProcessor implementations
 * 
 * @author trieu
 *
 */
public interface BaseModel {	
	/**
	 * free & clear for JVM GC
	 */
	public void freeResource();
	
	
	/**
	 * @return true if the model can be rendered with template engine
	 */
	public boolean isOutputableText();
}
