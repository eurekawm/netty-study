package netty.zhyf.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
abstract public class AbstractResponseMessage extends Message {
    private String code;
    private String reason;
}
