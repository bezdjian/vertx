package se.hb.udemy.vertxstarter.verticles;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public Completable rxStart() {
    log.debug("Start " + getClass().getName());
    vertx.deployVerticle(new VerticleA())
      .doOnSuccess(s -> {
        log.info("VerticleA is success. {}", s);
        vertx.deployVerticle(new VerticleB())
          .doOnSuccess(ss -> log.info("VerticleB is success. {}", ss))
          .subscribe();
      }).subscribe();

    // class.getName to deploy scaled
    vertx.deployVerticle(VerticleScale.class.getName(),
        new DeploymentOptions()
          .setInstances(4)
          .setConfig(new JsonObject()
            .put("id", UUID.randomUUID().toString())
            .put("name", VerticleScale.class.getName())))
      .doOnSuccess(s -> log.info("Vertical Scale is success {}", s))
      .subscribe();

    return Completable.complete();
  }
}
