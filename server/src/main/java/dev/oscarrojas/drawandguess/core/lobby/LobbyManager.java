package dev.oscarrojas.drawandguess.core.lobby;

import dev.oscarrojas.drawandguess.core.exceptions.LobbyFullException;
import dev.oscarrojas.drawandguess.core.lobby.canvas.Canvas;
import dev.oscarrojas.drawandguess.core.lobby.user.User;
import dev.oscarrojas.drawandguess.dto.LobbyData;
import dev.oscarrojas.drawandguess.dto.UserData;
import dev.oscarrojas.drawandguess.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LobbyManager {

    private static final int CANVAS_WIDTH = 1000;
    private static final int CANVAS_HEIGHT = 1000;

    private final List<Lobby> lobbies;

    public LobbyManager() {
        this.lobbies = new ArrayList<>();
    }

    public Optional<LobbyData> getLobby(String lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId().equals(lobbyId)) {
                return Optional.of(new LobbyData(
                        lobby.getId(),
                        lobby.getUsers().stream().map(User::getUsername).toList()
                ));
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a new lobby
     *
     * @return Lobby id
     */
    public String createLobby() {
        Lobby lobby = new Lobby(UUID.randomUUID().toString(),
                new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT)
        );
        lobbies.add(lobby);
        return lobby.getId();
    }


    public LobbyUserRegistration addUserToLobby(UserData user, String lobbyId) throws NotFoundException, LobbyFullException {
        for (Lobby lobby : lobbies) {
            if (lobby.getId().equals(lobbyId)) {
                return lobby.addUser(user);
            }
        }

        throw new NotFoundException(String.format("Lobby id %s not found", lobbyId));
    }

}
