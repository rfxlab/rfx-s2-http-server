package rfx.server.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import rfx.server.configs.HttpServerConfigs;
import rfx.server.http.common.AccessLogUtil;
import rfx.server.http.common.HttpServerInitializer;
import rfx.server.http.websocket.WebSocketServerInitializer;
import rfx.server.log.kafka.HttpLogKafkaHandler;
import rfx.server.util.LogUtil;

/**
 * HTTP Log server.
 * based on https://github.com/netty/netty/tree/master/example/src/main/java/io/netty/example/http/snoop
 */
public class HttpServerStarter {

    int port;
    String ip;
    static String host = "localhost:8080";

    public HttpServerStarter(String ip, int port) {
        this.port = port;
        this.ip = ip;
        host = this.ip+":"+port;
    }
    
    public static String getHost() {
		return host;
	}
    
	public void run(boolean websocket, int processingMode) throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {        	
            ServerBootstrap b = new ServerBootstrap();            
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            if(websocket){
            	b.childHandler(new WebSocketServerInitializer());
            } else {
            	b.childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childHandler(new HttpServerInitializer(processingMode));
            }

            Channel ch = b.bind(ip,port).sync().channel();  
            ch.config().setConnectTimeoutMillis(1800);
            LogUtil.i("HttpLogServer ", "is started and listening at " + this.ip + ":" + this.port);
            ch.closeFuture().sync();
        } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

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
        HttpLogKafkaHandler.initKafkaSession();
        AccessLogUtil.configureAccessLog(configs);
        
        System.out.println("-------------- HTTP SERVER LOG ["+ip+":"+port+"] --------------");
        if(websocket){
        	System.out.println(" #############  WebsocketEnabled Mode  #############");
        } else {
        	System.out.println(" #############  Http Enabled Mode  #############");
        }
        //MemoryManagementUtil.startMemoryUsageTask();
        int mode = HttpServerInitializer.SINGLE_PROCESSOR_MODE;
        new HttpServerStarter(ip,port).run(websocket,mode);
    }
}
