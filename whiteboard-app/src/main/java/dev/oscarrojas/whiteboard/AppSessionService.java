package dev.oscarrojas.whiteboard;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import dev.oscarrojas.whiteboard.canvas.Canvas;
import dev.oscarrojas.whiteboard.ws.protocol.AppMessageBinaryEncoder;

@Service
public class AppSessionService {

  private AppSessionDao sessionDao;
  private AppMessageBinaryEncoder encoder;
  private String defaultSessionId = "session1";

  AppSessionService(AppSessionDao sessionDao, AppMessageBinaryEncoder encoder) {
    this.sessionDao = sessionDao;
    this.encoder = encoder;
  }

  public Optional<AppSession> getSession(String sessionId) {
    return sessionDao.get(sessionId);
  }

  public Optional<AppSession> getSessionForConnection(String connectionId) {
    return sessionDao.getByConnectionId(connectionId);
  }

  public void registerConnection(WebSocketSession conn) {
    Optional<AppSession> sessionOpt = getSession(defaultSessionId);
    AppSession session;

    if (sessionOpt.isPresent()) {
      session = sessionOpt.get();
    } else {
      session = new AppSession(defaultSessionId, new Canvas(10, 10), encoder);
    }

    session.addConnection(conn);
    sessionDao.save(session);
  }

}
