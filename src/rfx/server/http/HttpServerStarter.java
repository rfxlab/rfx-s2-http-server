package rfx.server.http;


import rfx.server.configs.HttpServerConfigs;
import rfx.server.http.common.AccessLogUtil;
import rfx.server.http.common.PublicHttpServerInitializer;

/**
 * HTTP Log server.
 * based on https://github.com/netty/netty/tree/master/example/src/main/java/io/netty/example/http/snoop
 */
public class HttpServerStarter {
   
	//-Xms256m -Xmx2048m -XX:MaxNewSize=512m
    public static void main(String[] args) throws Exception {
    	HttpServerConfigs configs;
    	int customPort = 0;
    	boolean websocket = false;
    	if(args.length >= 1){
    		configs = HttpServerConfigs.load(args[0]);    		
    	} else {
    		configs = HttpServerConfigs.load();
    	}    	
    	websocket = configs.isWebsocketEnable();
    	
        int port = configs.getPort();
        if (customPort != 0 ) {
        	port = customPort;
        }
        String ip = configs.getIp();
        //HttpLogKafkaHandler.initKafkaSession();//SKIP in the first version 1.0
        AccessLogUtil.configureAccessLog(configs);
        
        System.out.println("-------------- HTTP SERVER LOG ["+ip+":"+port+"] --------------");
        if(websocket){
        	System.out.println(" #############  WebsocketEnabled Mode  #############");
        } else {
        	System.out.println(" #############  Http Server Enabled Mode  #############");
        }
        //MemoryManagementUtil.startMemoryUsageTask();
        int mode = PublicHttpServerInitializer.SINGLE_PROCESSOR_MODE;
        new HttpServer(ip,port).run(websocket,mode);
    }
}
