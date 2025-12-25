package dev.oscarrojas.drawandguess.io;

import dev.oscarrojas.drawandguess.dto.CreateLobby;

public enum Action {

    CREATE_USER(CreateLobby.class);

    public final Class<?> payloadType;

    Action(Class<?> payloadType) {
        this.payloadType = payloadType;
    }

}
