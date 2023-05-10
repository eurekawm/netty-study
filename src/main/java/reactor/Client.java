package reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel open = SocketChannel.open();
        open.connect(new InetSocketAddress(9999));
        open.write(Charset.defaultCharset().encode("fdsfdsfdsfds\n"));
        System.in.read();
    }
}
