package netty.zhyf.message;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequstMessage extends Message{

    private String groupName;

    private Set<String> members;

    @Override
    public int getMessageType() {
        return MessageType.GROUP_CREATE_REQUEST_MESSAGE;
    }
}
