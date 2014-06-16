package rfx.server.http.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorMapper;

@HttpProcessorMapper(uriPath = "/server-info", templatePath = "server-info.mustache", contentType = ContentTypePool.HTML_UTF8)
public class ServerInfoHttpProcessor extends HttpProcessor {

	@Override
	protected String process() {
//		HttpHeaders headers = request.headers();
//		String referer = headers.get(REFERER);
//		String userAgent = headers.get(USER_AGENT);
//		System.out.println("referer: "+referer);
//		System.out.println("userAgent: "+userAgent);
		System.out.println("filter: "+param("filter"));
		
		return render(new ServerInfoModel());
	}

	static class ServerInfoModel {
		String time;
		List<String> infos = new ArrayList<>();

		public ServerInfoModel() {
			time = new Date().toString();
			Properties props = System.getProperties();
			Enumeration e = props.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if (key.startsWith("java.vm")) {
					String s = (key + " : " + props.getProperty(key));
					infos.add(s);
				}
			}
		}
	}

}
