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

public class HttpServer {
	 static String host = "localhost:8080";

    int port;
    String ip;
    public HttpServer(String ip, int port) {
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
        	
        	//public service processor
            ServerBootstrap publicServerBootstrap = new ServerBootstrap();            
            publicServerBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            if(websocket){
            	publicServerBootstrap.childHandler(new WebSocketServerInitializer());
            } else {
            	publicServerBootstrap.childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childHandler(new PublicHttpServerInitializer(processingMode));
            }
            //bind to public access host info
            Channel ch1 = publicServerBootstrap.bind(ip,port).sync().channel();  
            ch1.config().setConnectTimeoutMillis(1800);
                        
            //admin service processor
            ServerBootstrap adminServerBootstrap = new ServerBootstrap();            
            adminServerBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childOption(ChannelOption.TCP_NODELAY, false).childOption(ChannelOption.SO_KEEPALIVE, false)
            .childHandler(new PrivateHttpServerInitializer());

            //bind to private access (for administrator only) host info, default 10000
            Channel ch2 = adminServerBootstrap.bind(ip,10000).sync().channel();  
            ch2.config().setConnectTimeoutMillis(1800);
            
            LogUtil.i("publicServerBootstrap ", "is started and listening at " + this.ip + ":" + this.port);
            LogUtil.i("adminServerBootstrap ", "is started and listening at " + this.ip + ":" + 10000);
            ch1.closeFuture().sync();            
            ch2.closeFuture().sync();
            System.out.println("Shutdown...");
            
        } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
