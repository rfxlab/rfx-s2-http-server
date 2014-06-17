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
	protected Object process() {
		// HttpHeaders headers = request.headers();
		// String referer = headers.get(REFERER);
		// String userAgent = headers.get(USER_AGENT);
		// System.out.println("referer: "+referer);
		// System.out.println("userAgent: "+userAgent);
		
		String filter = param("filter", "");
		System.out.println("filter: " + filter);
		return new ServerInfoModel(filter);
	}

	static class ServerInfoModel {
		String time;
		List<String> infos = new ArrayList<>();
		String filter;
		boolean showAll;
		boolean showCompact;

		public ServerInfoModel(String filter) {
			this.filter = filter;
			time = new Date().toString();
			if (filter.equals("all")) {
				showAll = true;
				showCompact = false;
			} else if (filter.equals("compact")) {
				showAll = false;
				showCompact = true;
			}

			Properties props = System.getProperties();
			Enumeration e = props.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String s = (key + " : " + props.getProperty(key));
				if (showAll) {
					infos.add(s);
				} else if (showCompact) {
					{
						if (key.startsWith("java.vm")) {
							infos.add(s);
						}
					}
				}

			}
		}
	}

}
