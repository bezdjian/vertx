package se.hb.udemy.vertxstarter;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class FuturePromiseTest {

  public static final Logger log = LoggerFactory.getLogger(FuturePromiseTest.class);

  @Test
  void shouldPromiseSuccess(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();
    log.info("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      log.info("Success!");
      context.completeNow();
    });

    log.info("End");
  }

  @Test
  void shouldPromiseFail(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();
    log.info("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Failed!"));
      log.info("Fail!");
      context.completeNow();
    });

    log.info("End");
  }

  @Test
  void shouldFutureSuccess(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();
    log.info("Start");
    vertx.setTimer(500, id -> {
      promise.complete("FFFF");
      log.info("Timer done");
      context.completeNow();
    });
    final Future<String> future = promise.future();
    future.onSuccess(s -> log.info("Future success! {}", s))
      .onFailure(context::failNow);

    log.info("End");
  }

  @Test
  void shouldFutureFail(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();
    log.info("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Some future exception"));
      log.info("Timer done");
      context.completeNow();
    });
    final Future<String> future = promise.future();
    future
      .onSuccess(context::failNow)
      .onFailure(f -> {
        log.info("Future failed! {}", f.getMessage());
        context.completeNow();
      });

    log.info("End");
  }

  @Test
  void shouldFutureMapToJson(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();
    log.info("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Some string to be converted");
      log.info("Timer done");
      context.completeNow();
    });
    final Future<String> future = promise.future();
    future
      .map(s -> {
        log.info("Map {} to JsonObject", s);
        return new JsonObject().put("string", s);
      })
      .map(jsonObject -> {
        log.info("Got converted JsonObject {}, converting now to JsonArray", jsonObject);
        return new JsonArray().add(jsonObject);
      })
      .onSuccess(jsonArray -> {
        log.info("Got jsonArray {}", jsonArray.encode());
        context.completeNow();
      })
      .onFailure(f -> {
        log.info("Future failed! {}", f.getMessage());
        context.completeNow();
      });

    log.info("End");
  }

  @Test
  void shouldCoordinateFuture(Vertx vertx, VertxTestContext context) {
    vertx.createHttpServer()
      .requestHandler(request -> log.info("Request {}", request))
      .rxListen(1515)
      .compose(server -> {
        log.info("Run some task");
        context.completeNow();
        return server;
      })
      .doOnError(context::failNow)
      .doOnSuccess(server -> {
        log.info("Server started doOnSuccess {}", server.actualPort());
        context.completeNow();
      })
      .subscribe();
  }

  @Test
  void futureComposition(Vertx vertx, VertxTestContext context) {
    var one = Promise.promise();
    var two = Promise.promise();
    var three = Promise.promise();

    var futureOne = one.future().compose(s -> Future.succeededFuture("One"));
    var futureTwo = two.future().compose(s -> Future.succeededFuture("Two"));
    var futureThree = three.future().compose(s -> Future.succeededFuture("Three"));

    CompositeFuture.all(futureOne, futureTwo, futureThree)
      .onFailure(context::failNow)
      .onSuccess(future -> {
        log.info("Success {}", future.result());
        context.completeNow();
      });

    vertx.setTimer(500, id -> {
      one.complete();
      two.complete();
      three.complete();
    });
  }
}
