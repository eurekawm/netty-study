package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class GroupChatResponseMessage extends AbstractResponseMessage {
    private String from;
    private String content;

    

    

    public GroupChatResponseMessage() {
    }



    @Override
    public int getMessageType() {
        return MessageType.GROUP_CHAT_RESPONSE_MESSAGE;
    }

}
