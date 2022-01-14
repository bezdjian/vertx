package se.hb.udemy.vertxstarter.customcodec;

import io.vertx.core.json.JsonObject;

public class Pong {

  private final String responseMessage;

  public Pong(String responseMessage) {
    this.responseMessage = responseMessage;
  }

  public String getResponseMessage() {
    return responseMessage;
  }

  @Override
  public String toString() {
    return new JsonObject().put("message", responseMessage).encodePrettily();
  }
}
