package netty.zhyf.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class ChatResponseMessage extends AbstractResponseMessage {
     
    public ChatResponseMessage(String code, String reason){
        super(code, reason);
    }

    @Override
    public int getMessageType() {
        return MessageType.CHAT_RESPONSE_MESSAGE;
    }

}