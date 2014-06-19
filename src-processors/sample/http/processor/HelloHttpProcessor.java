package sample.http.processor;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.BaseModel;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.HttpRequestEvent;

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
		
		

		public MyData(String name) {
			super();
			this.data = this.data + name;
		}



		@Override
		public void freeResource() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
