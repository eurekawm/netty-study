package netty.zhyf.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.domain.Session;

@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client is closed");
        Session session = new Session();
        session.unBind(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("client is close", cause);
        Session session = new Session();
        session.unBind(ctx.channel());
    }

}
