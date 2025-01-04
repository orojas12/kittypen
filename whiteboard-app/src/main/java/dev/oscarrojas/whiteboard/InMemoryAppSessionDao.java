package dev.oscarrojas.whiteboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryAppSessionDao implements AppSessionDao {

  private Map<String, AppSession> sessions = new HashMap<>();

  @Override
  public Optional<AppSession> get(String id) {

    return Optional.ofNullable(sessions.get(id));

  }

  @Override
  public Optional<AppSession> getByConnectionId(String connectionId) {
    AppSession session = null;

    for (AppSession sessionEntry : sessions.values()) {
      if (sessionEntry.hasConnection(connectionId)) {
        session = sessionEntry;
        break;
      }
    }

    return Optional.ofNullable(session);

  }

  @Override
  public void save(AppSession session) {
    assert session.getId() != null;
    sessions.put(session.getId(), session);
  }

}
