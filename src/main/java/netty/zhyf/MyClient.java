package netty.zhyf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class MyClient {
    private static final Logger logger = LoggerFactory.getLogger(MyClient.class);

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringEncoder());
            }
        });
        // 新线程 异步进行网络连接
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(9999));
        Channel channel = channelFuture.channel();
        channel.writeAndFlush("hello world");
        channel.close();

    }
}
