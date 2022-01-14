package se.hb.websockets;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle())
      .doOnSuccess(s -> testContext.completeNow())
      .subscribe();
  }

  @Test
  void shouldConnectToWebSocketServer(Vertx vertx, VertxTestContext context) {
    var client = vertx.createHttpClient();
    client.rxWebSocket(8888, "localhost", WebSocketHandler.PATH)
      .doOnError(context::failNow)
      .doOnSuccess(webSocket -> context.verify(() ->
        webSocket.handler(data -> {
          var msg = data.toString();
          assertEquals("Connected!", msg);
          context.completeNow();
          client.close();
        })))
      .subscribe();
  }
}
