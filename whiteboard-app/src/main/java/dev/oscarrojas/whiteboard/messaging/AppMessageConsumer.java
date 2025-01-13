package dev.oscarrojas.whiteboard.messaging;

import dev.oscarrojas.whiteboard.messaging.annotation.Action;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AppMessageConsumer {

  private final List<ActionMethod> actionMethods;

  public AppMessageConsumer() {
    List<ActionMethod> methods = new ArrayList<>();

    for (Method method : this.getClass().getDeclaredMethods()) {
      Action action = method.getAnnotation(Action.class);

      if (action == null) {
        continue;
      }

      methods.add(new ActionMethod(action.value(), method));
    }

    this.actionMethods = methods;
  }

  public Iterable<ActionMethod> getActionMethods() {
    return actionMethods;
  }

  public static class ActionMethod {
    public String action;
    public Method method;

    ActionMethod(String action, Method method) {
      this.action = action;
      this.method = method;
    }
  }
}
