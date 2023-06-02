package netty.zhyf.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.zhyf.domain.Session;
import netty.zhyf.message.ChatRequestMessage;
import netty.zhyf.message.ChatResponseMessage;

public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String toUser = msg.getTo();
        String content = msg.getContent();
        String from = msg.getFrom();
        Session session = new Session();
        Channel channel = session.getChannel(toUser);
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage("200", "send ok", content, from));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage("500", "send fail"));
        }

    }

}
