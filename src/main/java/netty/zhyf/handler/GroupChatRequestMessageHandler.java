package netty.zhyf.handler;

import io.netty.channel.Channel;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.zhyf.domain.GroupSession;
import netty.zhyf.message.GroupChatRequestMessage;
import netty.zhyf.message.GroupChatResponseMessage;

public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String groupName = msg.getGroupName();
        String content = msg.getContent();
        GroupSession groupSession = new GroupSession();
        List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(from, content));
        }
    }

}
