package se.hb.udemy.vertxstarter.customcodec;

import io.vertx.core.json.JsonObject;

public class Ping {

  private final String message;
  private final boolean enabled;

  public Ping(String message, boolean enabled) {
    this.message = message;
    this.enabled = enabled;
  }

  public String getMessage() {
    return message;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String toString() {
    return new JsonObject().put("message", message).put("enabled", enabled).encodePrettily();
  }
}
