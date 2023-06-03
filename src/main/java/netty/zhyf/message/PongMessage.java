package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PongMessage extends AbstractResponseMessage {

    private String source;

    @Override
    public int getMessageType() {
        return MessageType.PONG_MESSAGE;
    }

}
