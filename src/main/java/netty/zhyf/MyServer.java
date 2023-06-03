package netty.zhyf;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.codec.ChatByteToMessageDecoder;
import netty.zhyf.codec.ChatMessageToByteEncoder;
import netty.zhyf.handler.ChatRequestMessageHandler;
import netty.zhyf.handler.GroupChatRequestMessageHandler;
import netty.zhyf.handler.GroupCreateRequestMessageHandler;
import netty.zhyf.handler.LoginRequestMessageHandler;
import netty.zhyf.handler.QuitHandler;
import netty.zhyf.message.PongMessage;

@Slf4j
public class MyServer {

    public static void main(String[] args) {

        LoggingHandler loggingHandler = new LoggingHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 7, 4, 0, 0));
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(new ChatByteToMessageDecoder());
                    ch.pipeline().addLast(new ChatMessageToByteEncoder());
                    ch.pipeline().addLast(new IdleStateHandler(8, 3, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                            if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
                                log.debug("client send nothing after 8 seconds;");
                                ctx.channel().close();
                            }else if(idleStateEvent.state().equals(IdleState.WRITER_IDLE)){
                                ctx.writeAndFlush(new PongMessage("server"));
                            }
                        }

                    });

                    ch.pipeline().addLast(new LoginRequestMessageHandler());
                    ch.pipeline().addLast(new ChatRequestMessageHandler());
                    ch.pipeline().addLast(new GroupCreateRequestMessageHandler());
                    ch.pipeline().addLast(new GroupChatRequestMessageHandler());
                    ch.pipeline().addLast(new QuitHandler());
                }
            });
            Channel channel = bootstrap.bind(9999).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("error");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
