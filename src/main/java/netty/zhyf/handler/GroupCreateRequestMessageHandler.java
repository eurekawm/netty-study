package netty.zhyf.handler;

import io.netty.channel.Channel;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.zhyf.domain.Group;
import netty.zhyf.domain.GroupSession;
import netty.zhyf.message.GroupCreateRequstMessage;
import netty.zhyf.message.GroupCreateResponseMessage;

public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequstMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequstMessage msg) throws Exception {
        GroupSession groupSession = new GroupSession();
        Group group = groupSession.creatGroup(msg.getGroupName(), msg.getMembers());
        if (group == null) {
            // 成功 给所有成员发消息 告知加入了该组
            List<Channel> membersChannel = groupSession.getMembersChannel(msg.getGroupName());
            membersChannel.forEach(
                    item -> item.writeAndFlush(new GroupCreateResponseMessage("200",
                            "you are added into this group : " + msg.getGroupName())));
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage("500", "group create fail or already exsits"));
        }

    }

}
