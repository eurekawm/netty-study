package reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Worker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Worker.class);
    private Selector selector;
    private Thread thread;
    private String name;

    private volatile boolean isCreated;

    ConcurrentLinkedDeque<Runnable> runnables = new ConcurrentLinkedDeque<>();

    public Worker(String name) throws IOException {
        this.name = name;
    }

    public void register(SocketChannel socketChannel) throws IOException {
        log.debug("worker register invoke...");
        if (!isCreated) {
            selector = Selector.open();
            thread = new Thread(this, name);
            thread.start();
            isCreated = true;
        }
        runnables.add(() -> {
            try {
                socketChannel.register(selector, SelectionKey.OP_READ); // 这个一定要在 selector.select();之前运行
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        });
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            // run方法的异常不能抛出
            log.info("worker run method invoke");
            try {
                Runnable runnable = runnables.poll();
                if (runnable != null) {
                    runnable.run();
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        String result = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                        System.out.println(result);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
