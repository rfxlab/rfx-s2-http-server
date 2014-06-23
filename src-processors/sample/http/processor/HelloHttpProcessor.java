package sample.http.processor;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.BaseModel;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.HttpRequestEvent;

/**
 * @author trieu
 * 
 * simple sample processor 
 *
 */
@HttpProcessorConfig(uriPath= "/hello", contentType = ContentTypePool.JSON)
public class HelloHttpProcessor extends HttpProcessor {
	
	@Override
	protected BaseModel process(HttpRequestEvent requestEvent) {		
		String name = requestEvent.param("name", "guest");
		System.out.println("name: " + name);
		return new MyData(name);
	}

	static class MyData implements BaseModel{		
		String data = "Hello ";		
		static final String classpath = MyData.class.getName();

		public MyData(String name) {
			super();
			this.data = this.data + name;
		}
		
		@Override
		public void freeResource() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isOutputableText() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String classpath() {			
			return classpath;
		}
		
	}

}
