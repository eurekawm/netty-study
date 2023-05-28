package netty.zhyf.message;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeToClass {

    public static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(MessageType.CHAT_REQUEST_MESSAGE, ChatRequestMessage.class);
        messageClasses.put(MessageType.LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
        messageClasses.put(MessageType.LOGIN_RESPONSE_MESSAGE, LoginResponseMessage.class);
        messageClasses.put(MessageType.CHAT_RESPONSE_MESSAGE, ChatResponseMessage.class);
    }
}
