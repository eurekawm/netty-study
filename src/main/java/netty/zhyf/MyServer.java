package netty.zhyf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.codec.ChatByteToMessageDecoder;
import netty.zhyf.codec.ChatMessageToByteEncoder;
import netty.zhyf.message.ChatRequestMessage;
import netty.zhyf.message.ChatResponseMessage;
import netty.zhyf.message.LoginRequestMessage;
import netty.zhyf.message.LoginResponseMessage;

@Slf4j
public class MyServer {

    public static void main(String[] args) {

        LoggingHandler loggingHandler = new LoggingHandler();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ChatMessageToByteEncoder chatMessageToByteEncoder = new ChatMessageToByteEncoder();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 7, 4, 0, 0));
                    nioSocketChannel.pipeline().addLast(loggingHandler);
                    nioSocketChannel.pipeline().addLast(new ChatByteToMessageDecoder());
                    nioSocketChannel.pipeline().addLast(chatMessageToByteEncoder);
                    nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg)
                                throws Exception {
                            log.info("FDSSSSSSSSS {}", msg);
                            ctx.writeAndFlush(new LoginResponseMessage("200", "hello"));
                        }

                    });
                    nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ChatRequestMessage>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg)
                                throws Exception {
                            log.info("FDSSSSSSSSS {}", msg);
                            ctx.writeAndFlush(new ChatResponseMessage("300", "hello"));
                        }

                    });
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
