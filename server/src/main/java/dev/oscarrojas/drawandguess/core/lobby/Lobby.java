package dev.oscarrojas.drawandguess.core.lobby;

import dev.oscarrojas.drawandguess.core.exceptions.LobbyFullException;
import dev.oscarrojas.drawandguess.core.lobby.canvas.Canvas;
import dev.oscarrojas.drawandguess.core.lobby.user.User;
import dev.oscarrojas.drawandguess.dto.UserData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Lobby {

    private final String id;
    private final Canvas canvas;
    private final Set<User> users;

    Lobby(String id, Canvas canvas) {
        this.id = id;
        this.canvas = canvas;
        this.users = new HashSet<>();
    }

    String getId() {
        return id;
    }

    Canvas getCanvas() {
        return canvas;
    }

    Set<User> getUsers() {
        return users;
    }

    LobbyUserRegistration addUser(UserData userData) throws LobbyFullException {
        if (isFull()) {
            throw new LobbyFullException("Lobby is full");
        }
        User user = new User(UUID.randomUUID().toString(), userData.username());
        users.add(user);
        return new LobbyUserRegistration(id, user.getId());
    }

    boolean hasUser(String userId) {
        return false;
    }

    void removeUser(String userId) {

    }

    boolean isFull() {
        return users.size() >= 4;
    }

}
