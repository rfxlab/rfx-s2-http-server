package rfx.server.test;

import java.io.IOException;

import rfx.server.http.BaseModel;
import rfx.server.http.processor.model.ServerInfoModel;
import rfx.server.util.template.HandlebarsTemplateUtil;
import rfx.server.util.template.TemplateConfigUtil;

public class TestProcessModel {

	public static void main(String[] args) throws IOException {
		
		
		
		BaseModel model = new ServerInfoModel("all").prepareData();
		String templateLocation = TemplateConfigUtil.getTemplateLocation(model);
		String text = HandlebarsTemplateUtil.execute(templateLocation, model);
		System.out.println(templateLocation);
		
		
		String BASE_PACKAGE = "sample.http";
		TemplateConfigUtil.initTemplateConfigCache(BASE_PACKAGE );
		
	}
}
