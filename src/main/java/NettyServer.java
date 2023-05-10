import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NettyServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // serverSocketChannel非阻塞
        serverSocketChannel.configureBlocking(false); // selector 只有非阻塞的时候才能使用
        serverSocketChannel.bind(new InetSocketAddress(9999));

        // 引入监管者
        Selector selector = Selector.open();
        // 监管者来监管谁？来监管这个serverSocketChannel
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, null);
        // selector对什么类型感兴趣？
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        // 监控 发生连接事件后 会处理
        while (true) {
            // 只有监控到了实际的连接和读写操作才会处理
            // 对应的ACCEPT状态的serverSocketChannel和READ WRITE状态的SocketChannel存起来
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 用完了就把他从selectedKeys中删除
                iterator.remove();

                // 根据key来获取serverSocketChannel 因为这里只管理了serverSocketChannel
                // OP_ACCEPT状态的channel
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    System.out.println(socketChannel);
                    // 监控SocketChannel ----------> keys
                    ByteBuffer byteBuffer = ByteBuffer.allocate(7);
                    // 绑定bytebuffer到channel也就是register的第三个参数
                    SelectionKey scKey = socketChannel.register(selector, 0, byteBuffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    try {
                        // 读取
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        int read = socketChannel.read(byteBuffer);
                        if (read == 0){
                              key.cancel();
                        }
                        byteBuffer.flip();
                        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer));
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }

            }
        }
    }
}

