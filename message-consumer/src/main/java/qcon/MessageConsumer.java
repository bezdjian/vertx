package qcon;

import io.vertx.core.AbstractVerticle;

public class MessageConsumer extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().consumer("greeting")
        .handler(msg -> {
            System.out.println("Got message: " + msg.body());
            msg.reply(String.format("Hello %s from Greeter!", msg.body()));
        });
    }

}
