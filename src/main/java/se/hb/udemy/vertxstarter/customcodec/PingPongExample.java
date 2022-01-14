package se.hb.udemy.vertxstarter.customcodec;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import static se.hb.udemy.vertxstarter.customcodec.PingPongExample.ADDRESS;

@Slf4j
public class PingPongExample {

  public static final String ADDRESS = "my.vert.address";

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVerticle());
    vertx.deployVerticle(new PongVerticle());
  }
}

@Slf4j
class PingVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    final Ping message = new Ping("Ping!", true);

    LocalMessageCodec<Ping> codec = new LocalMessageCodec<>(Ping.class);
    DeliveryOptions options = new DeliveryOptions().setCodecName(codec.name());

    vertx.eventBus()
      .registerCodec(codec)
      .<Pong>rxRequest(ADDRESS, message, options)
      .doOnSuccess(s -> log.info("Success!"))
      .subscribe(response -> {
          log.info("Sending ping {}", message);
          Pong pong = response.body();
          log.info("Response: {}", pong);
        },
        Throwable::printStackTrace);
    return Completable.complete();
  }
}

@Slf4j
class PongVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    LocalMessageCodec<Pong> codec = new LocalMessageCodec<>(Pong.class);
    DeliveryOptions options = new DeliveryOptions().setCodecName(codec.name());

    return vertx.eventBus()
      .registerCodec(codec)
      .<Ping>consumer(ADDRESS, handler -> {
        Object message = handler.body();
        log.info("Received message: {}", message);
        handler.reply(new Pong("Pong!"), options);
      })
      .exceptionHandler(error -> log.error("Error consumed: {}", error.getMessage()))
      .rxCompletionHandler();
  }
}
