package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestMessage extends Message {
    private String from;
    private String to;
    private String content;

    @Override
    public int getMessageType() {
        return MessageType.CHAT_REQUEST_MESSAGE;
    }

}
