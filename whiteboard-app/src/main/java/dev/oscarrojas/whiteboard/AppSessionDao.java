package dev.oscarrojas.whiteboard;

import java.util.Optional;

public interface AppSessionDao {

  Optional<AppSession> get(String id);

  Optional<AppSession> getByConnectionId(String connectionId);

  void save(AppSession session);

}
