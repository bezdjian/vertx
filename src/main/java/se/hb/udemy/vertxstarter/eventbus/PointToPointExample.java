package se.hb.udemy.vertxstarter.eventbus;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

public class PointToPointExample {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Sender());
    vertx.deployVerticle(new Receiver());
  }

  @Slf4j
  static class Sender extends AbstractVerticle {
    @Override
    public Completable rxStart() {
      log.info("Sending a message...");
      vertx.setPeriodic(1000,
        id -> vertx.eventBus()
          .send("sender.address", "Sending point to point " + UUID.randomUUID()));

      return Completable.complete();
    }
  }

  @Slf4j
  static class Receiver extends AbstractVerticle {
    @Override
    public Completable rxStart() {
      return vertx.eventBus()
        .consumer("sender.address",
          message -> log.info("Received {}", message.body()))
        .rxCompletionHandler();
    }
  }
}
