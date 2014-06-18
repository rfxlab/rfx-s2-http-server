package rfx.server.http.common;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import rfx.server.http.PublicHttpProcessorRoutingHandler;

public class PublicHttpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	
	public static final int SINGLE_PROCESSOR_MODE = 1;
	public static final int MULTI_PROCESSOR_MODE = 2;
		
	private int mode = 0;
	ChannelHandler getLogChannelHandler(){
//		System.out.println("-----------------getLogChannelHandler-----------------");
		if(mode == SINGLE_PROCESSOR_MODE){			
			return new PublicHttpProcessorRoutingHandler();
		} 
		else if(mode == MULTI_PROCESSOR_MODE){			
			throw new IllegalArgumentException("Not support MULTI_PROCESSOR_MODE in version 1.0");
		}
		else {
			throw new IllegalArgumentException("Bad http server processer mode");
		}
	}	

	public PublicHttpServerInitializer(int mode) throws Exception {
		super();
		this.mode = mode;
		if(mode == SINGLE_PROCESSOR_MODE){			
			PublicHttpProcessorRoutingHandler.init();
		} 
		else if(mode == MULTI_PROCESSOR_MODE){
			throw new IllegalArgumentException("Not support MULTI_PROCESSOR_MODE in version 1.0");
		}
		else {
			throw new IllegalArgumentException("Bad http server processer mode");
		}    	
	}

	@Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //p.addLast("ssl", new SslHandler(engine));
        //TODO support SSL HTTP

        p.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast("aggregator", new HttpObjectAggregator(1048576));
        p.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast("deflater", new HttpContentCompressor());
        p.addLast("handler", getLogChannelHandler());         
    }
}