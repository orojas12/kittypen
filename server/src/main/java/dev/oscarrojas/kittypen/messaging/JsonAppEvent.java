package dev.oscarrojas.kittypen.messaging;

import java.time.Instant;
import java.util.Map;

public class JsonAppEvent implements AppEvent<Map<String, Object>> {

    private Instant timestamp;
    private String name;
    private Map<String, Object> payload;

    public JsonAppEvent() {
        this.timestamp = Instant.ofEpochMilli(Instant.now().toEpochMilli());
    }

    public JsonAppEvent(String name, Map<String, Object> payload) {
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

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

}
