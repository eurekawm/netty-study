import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class TestEventLoopGroup {
    private static final Logger logger = LoggerFactory.getLogger(TestEventLoopGroup.class);

    @Test
    public void testEventLoopGroup() {
        EventLoopGroup group = new NioEventLoopGroup();
    }

    @Test
    public void testJDKFuture() throws ExecutionException, InterruptedException {
        // jdk使用future的场景 ===> 异步化工作
        // 启用一个新线程 把处理结果返回给主线程
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                logger.debug("异步的工作处理");
                return 1;
            }
        });

        logger.debug("result ={} ", future.get());
        logger.debug("--------");
    }

    @Test
    public void testNettyFuture() {
        DefaultEventLoop defaultEventLoop = new DefaultEventLoop();
        EventLoop next = defaultEventLoop.next();
        io.netty.util.concurrent.Future<Integer> future = next.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                logger.debug("异步操作处理");
                TimeUnit.SECONDS.sleep(5);
                return 8932;
            }
        });
        future.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Integer>>() {
            @Override
            public void operationComplete(io.netty.util.concurrent.Future<? super Integer> future) throws Exception {
                logger.debug("异步操作的结果是{}", future.get());
            }
        });
        logger.debug("--------");
    }

    @Test
    public void testPromise() throws ExecutionException, InterruptedException {
        EventLoop next = new DefaultEventLoop().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(next);
        new Thread(()->{
            logger.debug("异步处理");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(10);
        }).start();
        logger.debug("等待异步处理的结果");
        logger.debug("结果是{}", promise.get());
    }

    @Test
    public void testByteBuf(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer();
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
    }
}
