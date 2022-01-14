package se.hb.vertx;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@Slf4j
@ApplicationScoped
public class PeriodicUsersPublisher extends AbstractVerticle {

  public static final String ADDRESS = PeriodicUsersPublisher.class.getName();

  @Override
  public Uni<Void> asyncStart() {
    var client = WebClient.create(vertx,
        new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8080));

    log.info("Starting vertx...");
    vertx.periodicStream(Duration.ofSeconds(5).toMillis())
        .toMulti()
        .subscribe()
        .with(item -> {
          log.info("Fetch all users...");
          // This can be called a service that returns Users from DB directly...
          client.get("/users")
              .send()
              .subscribe()
              .with(result -> {
                var body = result.bodyAsJsonArray();
                log.info("Got result {}", body);
                vertx.eventBus().publish(ADDRESS, body);
              });
        });

    return Uni.createFrom().voidItem();
  }
}
