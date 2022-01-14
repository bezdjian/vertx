package se.hb.udemy.vertxstarter.verticles;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerticleA extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new VerticleA());
  }

  @Override
  public Completable rxStart() {
    log.info("Start " + getClass().getName());
    return Completable.complete();
  }
}
