package dev.oscarrojas.whiteboard.messaging;

import java.time.Instant;

public class BinaryAppEvent implements AppEvent<byte[]> {

    private Instant timestamp;
    private String name;
    private byte[] payload;

    public BinaryAppEvent() {
        this.timestamp = Instant.ofEpochMilli(Instant.now().toEpochMilli());
    }

    public BinaryAppEvent(String name, byte[] payload) {
        this.timestamp = Instant.ofEpochMilli(Instant.now().toEpochMilli());
        this.name = name;
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

}
