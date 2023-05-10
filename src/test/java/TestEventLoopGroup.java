import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Test;

public class TestEventLoopGroup {

    @Test
    public void testEventLoopGroup(){
        EventLoopGroup group = new NioEventLoopGroup();
    }
}
