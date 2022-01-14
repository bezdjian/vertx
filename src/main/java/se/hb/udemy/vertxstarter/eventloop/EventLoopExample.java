package se.hb.udemy.vertxstarter.eventloop;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopExample extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(
      new VertxOptions()
        .setMaxEventLoopExecuteTime(500)
        .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
        .setBlockedThreadCheckInterval(1)
        .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
        //.setEventLoopPoolSize(2)
    );
    vertx.deployVerticle(EventLoopExample.class.getName(),
      new DeploymentOptions()
        .setInstances(4));
  }

  @Override
  public Completable rxStart() {
    log.info("Start {}", getClass().getName());
    //Do not do this!
    try {
      log.info("Sleeping...");
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return Completable.complete();
  }
}
