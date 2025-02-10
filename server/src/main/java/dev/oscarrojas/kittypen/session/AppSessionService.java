package dev.oscarrojas.kittypen.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.oscarrojas.kittypen.canvas.Canvas;
import dev.oscarrojas.kittypen.ws.protocol.AppEventBinaryConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppSessionService {

    private AppSessionDao sessionDao;
    private AppEventBinaryConverter converter;
    private ObjectMapper mapper;

    AppSessionService(
        AppSessionDao sessionDao, AppEventBinaryConverter converter,
        ObjectMapper mapper
    ) {
        this.sessionDao = sessionDao;
        this.converter = converter;
        this.mapper = mapper;
    }

    public Optional<AppSession> getSession(String sessionId) {
        return sessionDao.get(sessionId);
    }

    /**
     * Gets the session this websocket connection belongs to.
     *
     * @param ws websocket connection
     * @return the session this connection belongs to, or registers it to an available
     * session and returns it if it doesn't belong to one
     */
    public AppSession getSession(WebSocketSession ws) {
        Optional<AppSession> sessionOpt = sessionDao.getByConnectionId(ws.getId());
        AppSession session;

        if (sessionOpt.isPresent()) {
            session = sessionOpt.get();
        } else {
            session = findAvailableSession();
            session.addConnection(ws);
            sessionDao.save(session);
        }

        return session;
    }

    /**
     * Finds an available session for a new connection. Attempts to find the session with the
     * greatest number of connections that isn't full. If no session is available, creates a new
     * one.
     *
     * @return An available session that a new connection may be added to.
     */
    private AppSession findAvailableSession() {
        List<AppSession> nonFullSessions = sessionDao.getAllByConnectionCountRange(0, 9);

        AppSession optimalSession = null;

        for (AppSession session : nonFullSessions) {
            // prioritize sessions that are closer to full
            if (optimalSession == null
                || session.getConnectionCount() > optimalSession.getConnectionCount()
            ) {
                optimalSession = session;
            }
        }

        if (optimalSession == null) {
            optimalSession = new AppSession(
                UUID.randomUUID().toString(),
                new Canvas(1000, 1000),
                converter,
                mapper
            );
        }

        return optimalSession;
    }

    /**
     * Saves the session's current state
     *
     * @param session the AppSession to save
     */
    public void saveSession(AppSession session) {
        sessionDao.save(session);
    }
}
