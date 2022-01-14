package se.hb.udemy.vertxstarter;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application extends AbstractVerticle {

//  public static void main(String[] args) {
//    Vertx vertx = Vertx.vertx();
//    vertx.deployVerticle(new Application());
//  }

  @Override
  public Completable rxStart() {
    return vertx.createHttpServer()
      .requestHandler(req ->
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x Docker containerized with Jib!"))
      .rxListen(8888)
      .doOnSuccess(httpServer -> System.out.println("Server has started on " + httpServer.actualPort()))
      .ignoreElement();
  }
}
