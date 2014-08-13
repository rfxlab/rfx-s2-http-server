package sample.d2ux.http.processor;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.data.DataServiceConfig;
import rfx.server.http.data.HttpRequestEvent;
import rfx.server.http.data.service.DataService;
import rfx.server.http.data.service.WebDataService;

@HttpProcessorConfig(uriPattern = "ad-demo",contentType = ContentTypePool.HTML_UTF8)
public class AdDemoHttpProcessor extends HttpProcessor {

	@Override
	protected DataService process(HttpRequestEvent e) {	
		String testwhat = e.param("testwhat","InpageAdDemo");
		WebDataService ds = null;
		switch (testwhat) {			
			case "ChromeExtAdDemo":
				ds = new ChromeExtAdDemo();
				break;	
			default:
				ds = new ChromeExtAdDemo();
				break;
		}		
		return ds.build();
	}	


	
	
	
	@DataServiceConfig(template = "test/chrome-ext-ad-demo" )
	public class ChromeExtAdDemo extends WebDataService {
		String clickUrl;
		
		public String getClickUrl() {
			return clickUrl;
		}
		
		@Override
		public WebDataService build() {
			clickUrl = "http://www.mc2ads.com";
			return this;
		}		
	}
	
}
