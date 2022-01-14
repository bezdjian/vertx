package se.hb.vertx;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;

@Slf4j
@ApplicationScoped
public class VerticleStarter {

  public void init(@Observes StartupEvent event, Vertx vertx,
                   Instance<AbstractVerticle> verticles) {
    verticles.forEach(v -> {
      log.info("Verticle {}", v.toString());
      vertx.deployVerticle(v).await().indefinitely();
    });
  }
}
