package netty.zhyf.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.channel.Channel;

public class GroupSession {

    // 存储聊天室的信息 key = 聊天室的名字
    private static final Map<String, Group> groupMap = new HashMap<>();

    /**
     * 创建聊天室
     * 
     * @param name    聊天室名字
     * @param members 聊天室里的用户
     * @return 聊天室对象
     */
    public Group creatGroup(String name, Set<String> members) {
        // 向map中添加kv
        Group group = new Group(name, members);
        // 如果kv 不存在 返回null
        return groupMap.putIfAbsent(name, group);
    }

    /**
     * 获取聊天室成员
     * 
     * @param name
     * @return 聊天室里所有的成员
     */

    public Set<String> getMembers(String name) {
        return groupMap.get(name).getMembers();
    }

    /**
     * 获取聊天室成员的channel
     * 
     * @param name 聊天室名字
     * @return 聊天室里所有用户对应的channel
     */
    public List<Channel> getMembersChannel(String name) {
        List<Channel> channelList = new ArrayList<>();
        // 所有成员用户名
        Set<String> members = getMembers(name);
        for (String member : members) {
            Session session = new Session();
            Channel channel = session.getChannel(member);
            channelList.add(channel);
        }
        return channelList;
    }
}
