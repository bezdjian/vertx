package se.hb.websockets;


import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }

  @Override
  public Completable rxStart() {
    return vertx.createHttpServer()
      .requestHandler(this::respond)
      .webSocketHandler(new WebSocketHandler(vertx))
      .rxListen(8888)
      .doOnSuccess(s -> log.info("HTTP server started on port 8888"))
      .doOnError(err -> log.info("HTTP server failed to start"))
      .ignoreElement();
  }

  private void respond(HttpServerRequest req) {
    req.response()
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("message", "Hello from Vert.x!").encode());
  }
}
