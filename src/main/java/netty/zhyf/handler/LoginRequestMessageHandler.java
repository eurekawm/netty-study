package netty.zhyf.handler;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.domain.Session;
import netty.zhyf.message.LoginRequestMessage;
import netty.zhyf.message.LoginResponseMessage;

/**
 * 服务器端用于登录验证的方法
 */
@Slf4j
@Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private static Map<String, String> DB = new HashMap();

    static {
        DB.put("aa", "123");
        DB.put("bb", "123");
        DB.put("cc", "123");
        DB.put("dd", "123");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        log.debug("login method invoke");
        String uername = msg.getUername();
        String password = msg.getPassword();
        // 验证
        if (login(uername, password)) {
            log.debug("login OK");
            // 建立服务器端的session
            Session session = new Session();
            session.bind(ctx.channel(), uername);
            ctx.writeAndFlush(new LoginResponseMessage("200", "OK"));
        } else {
            log.debug("login ERROR");
            ctx.writeAndFlush(new LoginResponseMessage("200", "error"));
        }

    }

    private boolean login(String username, String password) {
        if (DB.get(username) != null && DB.get(username).equals(password)) {
            return true;
        }
        return false;
    }

}
