package netty.zhyf.codec;

import java.nio.charset.Charset;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import netty.zhyf.message.Message;
import netty.zhyf.message.MessageTypeToClass;

public class ChatByteToMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 魔数字
        ByteBuf byteBuf = in.readBytes(4);

        // 协议版本
        byte protoVersion = in.readByte();

        // 序列化方式
        byte serializableNo = in.readByte();
        // 功能
        byte funNo = in.readByte();
        // 正文长度
        int contentLength = in.readInt();
        // 正文
        Message message = null;
        if (serializableNo == 1) {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.readValue(in.readCharSequence(contentLength, Charset.defaultCharset()).toString(),
                    MessageTypeToClass.messageClasses.get((int) funNo));
        }
        out.add(message);
    }

}
