package netty.zhyf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServer {

    private static final Logger log = LoggerFactory.getLogger(MyServer.class);

    public static void main(String[] args) {
        log.debug("SERVER START-------");
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        // boss 组和worker组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new StringDecoder());
                // 这个handler交给defaultEventLoop处理
                nioSocketChannel.pipeline().addLast(defaultEventLoop, new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("msg = " + msg);
                    }
                });
            }
        });
        bootstrap.bind(9999);
    }
}
