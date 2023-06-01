package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestMessage extends Message {
    private String username;
    private String password;


    @Override
    public int getMessageType() {
        return MessageType.LOGIN_REQUEST_MESSAGE;
    }

}
