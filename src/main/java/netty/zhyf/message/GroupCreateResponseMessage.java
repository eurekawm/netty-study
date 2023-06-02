package netty.zhyf.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GroupCreateResponseMessage extends AbstractResponseMessage {


    public GroupCreateResponseMessage() {
    }

    public GroupCreateResponseMessage(String code, String reason) {
        super(code, reason);
    }

    @Override
    public int getMessageType() {
        return MessageType.GROUP_CREATE_RESPONSE_MESSAGE;
    }

}
