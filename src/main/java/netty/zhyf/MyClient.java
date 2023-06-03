package netty.zhyf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.codec.ChatByteToMessageDecoder;
import netty.zhyf.codec.ChatMessageToByteEncoder;
import netty.zhyf.message.ChatRequestMessage;
import netty.zhyf.message.GroupChatRequestMessage;
import netty.zhyf.message.GroupCreateRequstMessage;
import netty.zhyf.message.LoginRequestMessage;
import netty.zhyf.message.LoginResponseMessage;
import netty.zhyf.message.PingMessage;
import netty.zhyf.message.PongMessage;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MyClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        Scanner scanner = new Scanner(System.in);
        CountDownLatch waitLogin = new CountDownLatch(1);
        AtomicBoolean login = new AtomicBoolean(false);
        AtomicBoolean server_error = new AtomicBoolean(false);
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
                    ch.pipeline().addLast(new IdleStateHandler(8, 3, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                            if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
                                log.debug("client received notion from server after 3 seconds");
                                ctx.writeAndFlush(new PingMessage("client"));
                            } else if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
                                log.debug("服务端已经8秒钟没有响应数据了");
                                log.debug("关闭 channel");
                                log.debug("重新连接");
                                server_error.set(true);
                                ctx.channel().close();
                            }
                        }

                    });
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
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("client close");
                            // 关闭channel的时候 进行重新连接
                            if (server_error.get()) {
                                log.debug("reconnect channel");
                                EventLoop eventLoop = ctx.channel().eventLoop();
                                eventLoop.submit(() -> extracted(bootstrap));
                            } else {
                            }
                        }

                        /**
                         * @param bootstrap
                         */
                        private void extracted(Bootstrap bootstrap) {
                            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(9999));
                            Channel channel = null;
                            try {
                                channel = channelFuture.sync().channel();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            try {
                                channel.closeFuture().sync();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("client close", cause);
                            super.exceptionCaught(ctx, cause);
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
                                            break;
                                        case "gcreate":
                                            String goupName = split[1];
                                            String[] members = split[2].split(",");
                                            Set<String> memberSet = new HashSet<>(Arrays.asList(members));
                                            // 群聊用户包括自己自己
                                            memberSet.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequstMessage(goupName, memberSet));
                                            break;
                                        case "gsend":
                                            String gName = split[1];
                                            String content = split[2];
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, gName, content));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
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
            log.debug("error");
        } finally {
            group.shutdownGracefully();
        }

    }
}
