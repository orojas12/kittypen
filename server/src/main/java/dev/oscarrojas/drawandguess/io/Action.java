package dev.oscarrojas.drawandguess.io;

import dev.oscarrojas.drawandguess.dto.NewUser;

public enum Action {

    CREATE_USER(NewUser.class);

    public final Class<?> payloadType;

    Action(Class<?> payloadType) {
        this.payloadType = payloadType;
    }

}
