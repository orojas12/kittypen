package dev.oscarrojas.drawandguess.core.lobby;

import dev.oscarrojas.drawandguess.core.canvas.Canvas;
import dev.oscarrojas.drawandguess.core.user.User;

import java.util.HashSet;
import java.util.Set;

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

    void addUser(User user) {

    }

    boolean hasUser(String userId) {
        return false;
    }

    void removeUser(String userId) {

    }

}
