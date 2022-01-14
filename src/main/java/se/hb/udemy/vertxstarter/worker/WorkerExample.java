package se.hb.udemy.vertxstarter.worker;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerExample extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
  }

  @Override
  public Completable rxStart() {
    vertx.deployVerticle(new WorkerVerticle(),
      new DeploymentOptions()
        .setWorker(true)
        .setWorkerPoolSize(1)
        .setWorkerPoolName("my-worker-verticle"));

    log.info("Start {}", getClass().getName());
    executeBlockingCode();

    return Completable.complete();
  }

  private void executeBlockingCode() {
    vertx.rxExecuteBlocking(handler -> {
      log.info("Executing blocking code..");
      try {
        Thread.sleep(5000);
        handler.complete("S");
      } catch (InterruptedException e) {
        e.printStackTrace();
        log.error("Failed: {}", e.getMessage());
        handler.fail(e);
      }
    }).subscribe(s -> log.info("Success! {}", s),
      error -> log.error("Error! {}", error.getMessage())
    );
  }
}
