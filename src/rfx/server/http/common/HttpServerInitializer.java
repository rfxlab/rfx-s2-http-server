package rfx.server.http.common;


import rfx.server.http.HttpLogChannelHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	
	public static final int HTTP_KAFKA_LOG = 2;
		
	private int logType = 0;
	ChannelHandler getLogChannelHandler(){
		if(logType == HTTP_KAFKA_LOG){
			return new HttpLogChannelHandler();
		} else {
			throw new IllegalArgumentException("");
		}
	}	

	public HttpServerInitializer(int logType) {
		super();
		this.logType = logType;
	}

	@Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //p.addLast("ssl", new SslHandler(engine));

        p.addLast("decoder", new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast("aggregator", new HttpObjectAggregator(1048576));
        p.addLast("encoder", new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast("deflater", new HttpContentCompressor());
        p.addLast("handler", getLogChannelHandler());         
    }
}