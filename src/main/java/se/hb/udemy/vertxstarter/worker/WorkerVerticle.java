package se.hb.udemy.vertxstarter.worker;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerVerticle());
  }

  @Override
  public Completable rxStart() {
    log.info("Deployed as worker verticle {}", getClass().getName());
    try {
      Thread.sleep(5000);
      log.info("Did some logic that took 5 seconds!");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return Completable.complete();
  }
}
