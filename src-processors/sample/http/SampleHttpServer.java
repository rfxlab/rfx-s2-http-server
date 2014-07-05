package sample.http;

import rfx.server.configs.HttpServerConfigs;
import rfx.server.http.HttpServer;

public class SampleHttpServer {

	public static void main(String[] args) throws Exception {
    	HttpServerConfigs configs = HttpServerConfigs.load();
    	int customPort = 0;    	
    	
        int port = configs.getPort();
        if (customPort != 0 ) {
        	port = customPort;
        }
        String ip = configs.getIp();  
        
        System.out.println("-------------- SAMPLE HTTP SERVER with HOST["+ip+":"+port+"] --------------");        
        
        //MemoryManagementUtil.startMemoryUsageTask();
        String publicClasspath = "sample";
        int poolSize = 30000;
        
        new HttpServer(ip,port,poolSize).run(false,publicClasspath);
	}
}
