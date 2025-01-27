package dev.oscarrojas.whiteboard.session;

import dev.oscarrojas.whiteboard.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface AppSessionDao {

    Optional<AppSession> get(String id);

    Optional<AppSession> getByConnectionId(String connectionId);

    /**
     * Finds all sessions with a number of connections within a specified range (inclusive).
     *
     * @param min minimum number of connections
     * @param max maximum number of connections
     */
    List<AppSession> getAllByConnectionCountRange(int min, int max);

    void save(AppSession session);

    void delete(String sessionId) throws NotFoundException;

}
