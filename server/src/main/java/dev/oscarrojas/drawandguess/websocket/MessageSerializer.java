package dev.oscarrojas.drawandguess.websocket;

import dev.oscarrojas.drawandguess.io.InboundMessage;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;

public class MessageSerializer {

    InboundMessage<Object> deserialize(TextMessage message) {
        return null;
    }

    InboundMessage<byte[]> deserialize(BinaryMessage message) {
        return null;
    }

}
