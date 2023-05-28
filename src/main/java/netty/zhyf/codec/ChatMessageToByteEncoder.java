package netty.zhyf.codec;

import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import netty.zhyf.message.Message;

@Slf4j
public class ChatMessageToByteEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        log.info("开始编码消息");
        // 幻数字
        out.writeBytes(new byte[] { 's', 'u', 'n', 's' });
        // 协议版本
        out.writeByte(1);
        // 序列化方式 1 json 2 protobuf, 2 hession
        out.writeByte(1);
        // 功能指令
        out.writeByte(msg.getMessageType());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(msg);
        // 正文长度
        out.writeInt(jsonContent.length());
        // 正文
        out.writeCharSequence(jsonContent, Charset.defaultCharset());
    }

}
