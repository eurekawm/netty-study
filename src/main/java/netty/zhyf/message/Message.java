package netty.zhyf.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Message {
    @JsonIgnore
    public abstract int getMessageType();
}
