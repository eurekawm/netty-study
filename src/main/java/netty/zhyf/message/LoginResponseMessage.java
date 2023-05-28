package netty.zhyf.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(String code, String reason) {
        super(code, reason);
    }

    @Override
    public int getMessageType() {
        return MessageType.LOGIN_RESPONSE_MESSAGE;
    }

}
