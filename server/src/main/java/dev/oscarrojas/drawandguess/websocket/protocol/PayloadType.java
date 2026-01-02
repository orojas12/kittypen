package dev.oscarrojas.drawandguess.websocket.protocol;

public enum PayloadType {
    BINARY(0),
    JSON(1);

    final int value;

    PayloadType(int value) {
        this.value = value;
    }

    public static PayloadType fromValue(int value) {
        for (PayloadType type : PayloadType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown payload type value: " + value);
    }
}
