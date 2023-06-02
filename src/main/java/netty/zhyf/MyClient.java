package netty.zhyf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.codec.ChatByteToMessageDecoder;
import netty.zhyf.codec.ChatMessageToByteEncoder;
import netty.zhyf.message.ChatRequestMessage;
import netty.zhyf.message.LoginRequestMessage;
import netty.zhyf.message.LoginResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MyClient {
    private static final Logger logger = LoggerFactory.getLogger(MyClient.class);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        Scanner scanner = new Scanner(System.in);
        CountDownLatch waitLogin = new CountDownLatch(1);
        AtomicBoolean login = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 7, 4, 0, 0));
                    ch.pipeline().addLast(new ChatByteToMessageDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(new ChatMessageToByteEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("recive data {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                log.info("收到了消息了 ！ 是LoginResponseMessage");
                                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                if (loginResponseMessage.getCode().equals("200")) {
                                    login.set(true);
                                }
                                waitLogin.countDown();
                            }

                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                // 发送登录操作
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(loginRequestMessage);
                                try {
                                    waitLogin.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                // 没登陆成功
                                if (!login.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                log.info("开始处理命令");
                                
                                while (true) {

                                    System.out.println("=================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("quit");
                                    System.out.println("==================");
                                    String command = scanner.nextLine();
                                    String[] split = command.split(" ");
                                    switch (split[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, split[1], split[2]));
                                        case "gcreate":
                                            ;
                                        case "gsend":
                                            ;
                                        case "quit":
                                            ;
                                    }

                                }

                            }, "client UI").start();

                        }

                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(9999));
            Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.debug("error");
        } finally {
            group.shutdownGracefully();
        }

    }
}
