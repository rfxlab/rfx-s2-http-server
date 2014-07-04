package rfx.server.http.data.service;

import rfx.server.http.OutputConfig;
import rfx.server.http.ViewableDataService;

@OutputConfig(location = "system/server-info")
public class ServerErrorService extends ViewableDataService {
	static final String classpath = ServerErrorService.class.getName();


	@Override
	public ViewableDataService build() {
		//TODO
		return this;
	}

	@Override
	public String getClasspath() {
		return classpath;
	}
	
}
