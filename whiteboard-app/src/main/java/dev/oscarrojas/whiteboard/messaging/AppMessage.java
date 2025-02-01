package dev.oscarrojas.whiteboard.messaging;

import java.time.Instant;

public class AppMessage {

    private Instant timestamp;
    private String channel;
    private String action;
    private byte[] payload;

    public AppMessage() {
        this.timestamp = Instant.ofEpochMilli(Instant.now().toEpochMilli());
    }

    public AppMessage(String channel, String action, byte[] payload) {
        this.timestamp = Instant.ofEpochMilli(Instant.now().toEpochMilli());
        this.channel = channel;
        this.action = action;
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String event) {
        this.action = event;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

}
