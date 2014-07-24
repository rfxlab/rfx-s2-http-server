package sample.http.processor;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.DataService;
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
	protected DataService process(HttpRequestEvent requestEvent) {		
		String name = requestEvent.param("name", "guest");
		System.out.println("name: " + name);
		return new MyData(name);
	}

	static class MyData implements DataService{		
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
		public String getClasspath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isOutputable() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public List<HttpHeaders> getHttpHeaders() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
