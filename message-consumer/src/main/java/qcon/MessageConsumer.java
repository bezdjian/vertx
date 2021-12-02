package qcon;

import io.vertx.rxjava3.core.AbstractVerticle;

public class MessageConsumer extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().consumer("greeting", msg -> {
            System.out.println("Got message: " + msg.body());
            msg.reply(String.format("Hello %s from Greeter!", msg.body()));
        });
    }
}
