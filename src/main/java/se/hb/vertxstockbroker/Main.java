package se.hb.vertxstockbroker;

import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();

    // Deploy multiple instances.
    //Vertx.vertx()
    // .deployVerticle(Application.class.getName(), new DeploymentOptions()
    // .setInstances(Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));

    //Deploy single instance
    vertx.deployVerticle(new MainVerticle())
      .doOnError(err -> log.info("Error while deploying verticle: {}", err.getMessage()))
      .doOnSuccess(s -> log.info("Successfully deployed MainVerticle: {}", s))
      .subscribe();
  }
}
