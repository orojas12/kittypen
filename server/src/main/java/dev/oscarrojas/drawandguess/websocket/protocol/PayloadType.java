package dev.oscarrojas.drawandguess.websocket.protocol;

public enum PayloadType {
    BINARY(0),
    JSON(1);

    final int value;

    PayloadType(int value) {
        this.value = value;
    }

}
