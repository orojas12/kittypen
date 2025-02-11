package dev.oscarrojas.kittypen.event;

import java.time.Instant;

public class ClientEvent<T> {

    private String id;
    private Instant timestamp;
    private String name;
    private String clientId;
    private T payload;

}
