package se.hb.udemy.vertxstarter.eventbus;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import static se.hb.udemy.vertxstarter.eventbus.RequestResponseJsonExample.ADDRESS;

public class RequestResponseJsonExample {

  public static final String ADDRESS = "my.vert.address";

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestJsonVerticle());
    vertx.deployVerticle(new ResponseJsonVerticle());
  }
}

@Slf4j
class RequestJsonVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    EventBus eventBus = vertx.eventBus();
    JsonObject request = new JsonObject()
      .put("message", "My message to eventbus")
      .put("version", 1);

    eventBus.<JsonObject>rxRequest(ADDRESS, request)
      .subscribe(response -> log.info("Response: {}", response.body()),
        error -> log.error("Error! {}", error.getMessage()));
    return Completable.complete();
  }
}

@Slf4j
class ResponseJsonVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    EventBus eventBus = vertx.eventBus();
    return eventBus.<JsonObject>consumer(ADDRESS, handler -> {
      JsonObject message = handler.body();
      log.info("Received message: {}", message);
      String responseMessage = String.format("Received %s ", message);
      handler.reply(
        new JsonArray().add(responseMessage)
          .add(message.getString("version")));
    }).rxCompletionHandler();
    //return Completable.complete();
  }
}
