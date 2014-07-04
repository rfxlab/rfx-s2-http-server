package rfx.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import rfx.server.http.common.PrivateHttpServerInitializer;
import rfx.server.http.common.PublicHttpServerInitializer;
import rfx.server.http.websocket.WebSocketServerInitializer;
import rfx.server.util.LogUtil;
import rfx.server.util.template.OutputConfigUtil;

public class HttpServer {
	static String host = "localhost:8080";
	static final int PRIVATE_HTTP_PORT = 31000;
	public final static String DEFAULT_CLASSPATH = "rfx";
	public final static String SERVER_INFO_VERSION = "RfxHttpServer/0.1";
	
	
    int port;
    String ip;
    int publicPoolSize = PublicHttpProcessorRoutingHandler.DEFAULT_MAX_POOL_SIZE;
    int privatePoolSize = PrivateHttpProcessorRoutingHandler.DEFAULT_MAX_POOL_SIZE;
    
    void setHost(String ip, int port) {
        this.port = port;
        this.ip = ip;
        host = this.ip+":"+port;
    }
    
    public HttpServer(String ip, int port) {
    	setHost(ip, port);
    }
    
    public HttpServer(String ip, int port, int processorPoolSize) {
    	setHost(ip, port);
    	this.privatePoolSize = this.publicPoolSize = processorPoolSize;
    }
    
    public HttpServer(String ip, int port, int processorPoolSize, boolean noCacheMode) {
    	setHost(ip, port);
    	this.privatePoolSize = this.publicPoolSize = processorPoolSize;    	
    }
    
    public static String getHost() {
		return host;
	}
    
	public void run(boolean websocket, String classpath) throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {        
        	//init cache for all public model
        	OutputConfigUtil.initTemplateConfigCache(DEFAULT_CLASSPATH);
        	OutputConfigUtil.initTemplateConfigCache(classpath);
        	
        	//public service processor
            ServerBootstrap publicServerBootstrap = new ServerBootstrap();            
            publicServerBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            if(websocket){
            	publicServerBootstrap.childHandler(new WebSocketServerInitializer());
            } else {
            	publicServerBootstrap.childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childHandler(new PublicHttpServerInitializer(classpath, this.publicPoolSize));            	
            }
            //bind to public access host info
            Channel ch1;
            if("*".equals(ip)){
            	ch1 = publicServerBootstrap.bind(port).sync().channel();
            } else {
            	ch1 = publicServerBootstrap.bind(ip, port).sync().channel();
            }
            ch1.config().setConnectTimeoutMillis(1800);
                        
            //admin service processor
            ServerBootstrap adminServerBootstrap = new ServerBootstrap();            
            adminServerBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childOption(ChannelOption.TCP_NODELAY, false).childOption(ChannelOption.SO_KEEPALIVE, false)
            .childHandler(new PrivateHttpServerInitializer(DEFAULT_CLASSPATH, this.privatePoolSize ));

            //bind to private access (for administrator only) host info, default 10000
            Channel ch2;
            if("*".equals(ip)){
            	ch2 = adminServerBootstrap.bind(PRIVATE_HTTP_PORT).sync().channel();
            } else {
            	ch2 = adminServerBootstrap.bind(ip,PRIVATE_HTTP_PORT).sync().channel();
            }                        
            ch2.config().setConnectTimeoutMillis(1800);            
            
            LogUtil.i("publicServerBootstrap ", "is started and listening at " + this.ip + ":" + this.port);
            LogUtil.i("adminServerBootstrap ", "is started and listening at " + this.ip + ":" + PRIVATE_HTTP_PORT);
            ch1.closeFuture().sync();            
            ch2.closeFuture().sync();
            System.out.println("Shutdown...");
            
        } catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}