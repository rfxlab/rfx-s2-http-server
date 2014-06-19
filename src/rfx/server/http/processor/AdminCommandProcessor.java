package rfx.server.http.processor;

import rfx.server.configs.ContentTypePool;
import rfx.server.http.BaseModel;
import rfx.server.http.HttpProcessor;
import rfx.server.http.HttpProcessorConfig;
import rfx.server.http.HttpRequestEvent;
import rfx.server.util.template.MustacheTemplateUtil;

@HttpProcessorConfig(privateAccess = HttpProcessorConfig.PRIVATE_ACCESS, uriPath = "/admin-command", contentType = ContentTypePool.JSON)
public class AdminCommandProcessor extends HttpProcessor {

	@Override
	protected BaseModel process(HttpRequestEvent requestEvent) {
		// HttpHeaders headers = request.headers();
		// String referer = headers.get(REFERER);
		// String userAgent = headers.get(USER_AGENT);
		// System.out.println("referer: "+referer);
		// System.out.println("userAgent: "+userAgent);

		String cmd = requestEvent.param("cmd", "");
		System.out.println("cmd: " + cmd);
		String status = "fail";

		if (cmd.equals("refresh-templates")) {
			MustacheTemplateUtil.refreshCache();
			status = cmd + " success";
		}
		return new AdminCommandResult(status);
	}

	static class AdminCommandResult implements BaseModel {
		String status;

		public AdminCommandResult(String status) {
			super();
			this.status = status;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		@Override
		public void freeResource() {
			status = null;
		}
	}

}
