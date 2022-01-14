package se.hb.udemy.vertxstarter.eventbus;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import static se.hb.udemy.vertxstarter.eventbus.RequestResponseExample.ADDRESS;

public class RequestResponseExample {

  public static final String ADDRESS = "my.vert.address";

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVerticle());
    vertx.deployVerticle(new ResponseVerticle());
  }
}

@Slf4j
class RequestVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    EventBus eventBus = vertx.eventBus();
    eventBus.<String>rxRequest(ADDRESS, "My message to eventbus")
      .subscribe(response -> log.info("Response: {}", response.body()),
        error -> log.error("Error! {}", error.getMessage()));
    return Completable.complete();
  }
}

@Slf4j
class ResponseVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    EventBus eventBus = vertx.eventBus();
    return eventBus.<String>consumer(ADDRESS, handler -> {
      Object message = handler.body();
      log.info("Received message: {}", message);
      String responseMessage = String.format("Received %s and replying with '%s->customized'",
        message, message);
      handler.reply(responseMessage);
    }).rxCompletionHandler();
    //return Completable.complete();
  }
}
