package netty.zhyf.message;

public interface MessageType {
    int CHAT_REQUEST_MESSAGE = 1;
    int LOGIN_REQUEST_MESSAGE = 0;
    int LOGIN_RESPONSE_MESSAGE = 3;
    int CHAT_RESPONSE_MESSAGE = 4;
    int GROUP_CREATE_REQUEST_MESSAGE = 5;
    int GROUP_CREATE_RESPONSE_MESSAGE = 6;
    int GROUP_CHAT_REQUEST_MESSAGE = 7;
    int GROUP_CHAT_RESPONSE_MESSAGE = 8;
    int PING_MESSAGE = 9;
    int PONG_MESSAGE = 10;
}
