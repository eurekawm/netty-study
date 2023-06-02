package netty.zhyf.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChatResponseMessage extends AbstractResponseMessage {

    private String from;
    private String content;

    public ChatResponseMessage(String code, String reason, String content, String from) {
        super(code, reason);
        this.content = content;
        this.from = from;
    }

    public ChatResponseMessage(String content) {
        this.content = content;
    }

    public ChatResponseMessage(String code, String reason) {
        super(code, reason);
    }

    @Override
    public int getMessageType() {
        return MessageType.CHAT_RESPONSE_MESSAGE;
    }

}