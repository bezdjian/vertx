package se.hb.vertx;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class EventBusConsumer extends AbstractVerticle {

  @Override
  public Uni<Void> asyncStart() {
    vertx.eventBus().consumer(PeriodicUsersPublisher.ADDRESS, message -> {
      log.info("Consumed from eventbus {}", message.body());
      // Do some stuff with the message (List of Users JsonArray)...
    });

    return Uni.createFrom().voidItem();
  }
}
