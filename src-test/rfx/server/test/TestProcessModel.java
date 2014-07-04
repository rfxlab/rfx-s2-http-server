package rfx.server.test;

import java.io.IOException;

import rfx.server.http.DataService;
import rfx.server.http.data.service.ServerInfoService;
import rfx.server.util.template.HandlebarsTemplateUtil;
import rfx.server.util.template.OutputConfigUtil;

public class TestProcessModel {

	public static void main(String[] args) throws IOException {
			
		
		OutputConfigUtil.initTemplateConfigCache("rfx.server");
		
		DataService model = new ServerInfoService("all").build();
		String templateLocation = OutputConfigUtil.getOutputConfig(model).location();
		String text = HandlebarsTemplateUtil.execute(templateLocation, model);
		System.out.println(templateLocation);
		
		
		String BASE_PACKAGE = "sample.http";
		OutputConfigUtil.initTemplateConfigCache(BASE_PACKAGE );
		
	}
}
