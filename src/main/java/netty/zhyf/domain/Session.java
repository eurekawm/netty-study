package netty.zhyf.domain;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class Session {

    private static final Map<String, Channel> userChannelMap = new HashMap<>();
    private static final Map<Channel, String> channelUserMap = new HashMap<>();

    public void bind(Channel channel, String username) {
        userChannelMap.put(username, channel);
        channelUserMap.put(channel, username);
    }

    public void unBind(Channel channel) {
        String username = channelUserMap.remove(channel);
        if (username != null) {
            userChannelMap.remove(username);
        }
    }
    public Channel getChannel(String username){
        return userChannelMap.get(username);
    }
}
