package se.hb.vertxstockbroker.api.slow;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SlowApiHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext ctx) {
    log.info("Path {} responds with {}", ctx.normalizedPath(), UUID.randomUUID());
    //TODO: Testing purposes with Vegeta!
    //Simulate slow response...
    simulateSlowResponse(ctx);
    ctx.response().end("Slow api response");
  }

  /**
   * This method simulates a slow response half of the time (% 2 == 0)
   * to showcase scaling and load testing the webserver without blocking them.
   *
   * @param ctx RoutingContext
   */
  private void simulateSlowResponse(RoutingContext ctx) {
    try {
      int random = ThreadLocalRandom.current().nextInt(100, 300);
      if (random % 2 == 0) {
        Thread.sleep(random);
        returnInternalError(ctx);
      }
    } catch (InterruptedException e) {
      log.info("Error in thread: " + e.getMessage());
    }
  }

  private void returnInternalError(RoutingContext ctx) {
    var message = new JsonObject()
      .put("error", "Sleeping still...")
      .encode();

    var responseArray = new JsonArray().add(message);
    log.info("Internal error: " + message);
    ctx.response()
      .setStatusCode(500)
      .end(responseArray.encode());
  }
}
