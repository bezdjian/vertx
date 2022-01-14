package se.hb.udemy.vertxstarter.eventbus;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

public class PublishSubscribeExample {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Publisher());
    vertx.deployVerticle(new Subscriber1());
    vertx.deployVerticle(Subscriber2.class.getName(),
      new DeploymentOptions().setInstances(2));
  }

  @Slf4j
  static class Publisher extends AbstractVerticle {
    @Override
    public Completable rxStart() {
      log.info("Publishing a message...");
      vertx.setPeriodic(1000, id ->
        vertx.eventBus()
          .publish("address", "My publisher message "
            + UUID.randomUUID()));

      return Completable.complete();
    }
  }

  @Slf4j
  public static class Subscriber1 extends AbstractVerticle {
    @Override
    public Completable rxStart() {
      return vertx.eventBus()
        .consumer("address",
          message -> log.info("S1 Received {}", message.body()))
        .rxCompletionHandler();
    }
  }

  @Slf4j
  public static class Subscriber2 extends AbstractVerticle {
    @Override
    public Completable rxStart() {
      return vertx.eventBus()
        .consumer("address",
          message -> log.info("S2 Received {}", message.body()))
        .rxCompletionHandler();
    }
  }
}
