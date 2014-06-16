package rfx.server.test;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public class TestStringProcessing {

	public final static  String[] REFERER_SEARCH_LIST = new String[]{"\t%s","\t","%s","\r\n","\n","\r"};
	public final static  String[] REFERER_REPLACE_LIST = new String[]{"","","","","",""};
	
	public static void main(String[] args) {
		String refererUrl = "ab\tc\ndef";
		System.out.println(refererUrl);
		refererUrl = StringUtils.replaceEach(refererUrl, REFERER_SEARCH_LIST,  REFERER_REPLACE_LIST);
		System.out.println(refererUrl);
		
		String u = "aHR0cDovL2dhY3NhY2guY29tL3RodS12aWVuLXNhY2g@cGFnZT00".replace("@", "/");
		
		
		System.out.println("urf: "+ new String(Base64.getDecoder().decode(u)));
	}
}
