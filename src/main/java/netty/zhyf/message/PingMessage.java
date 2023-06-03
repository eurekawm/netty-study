package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PingMessage extends Message {

    private String source;

    @Override
    public int getMessageType() {
        return MessageType.PING_MESSAGE;
    }

}
