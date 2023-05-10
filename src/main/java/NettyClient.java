import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NettyClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(9999));
        while (true) {
            socketChannel.write(Charset.defaultCharset().encode("fdsfds"));
        }
    }
}
