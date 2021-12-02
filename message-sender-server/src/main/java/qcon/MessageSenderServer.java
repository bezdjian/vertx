package qcon;

import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;

public class MessageSenderServer extends AbstractVerticle {

    @Override
    public void start() {

        Router router = Router.router(vertx);
        router.get("/greet").handler(rc -> {
            String name = rc.request().getParam("name");
            System.out.println("Sending message with name: " + name);
            vertx.eventBus().<String>rxRequest("greeting", name)
                    .subscribe(msg -> {
                        System.out.println("Got message from consumer: " + msg.body());
                        System.out.println("From address: " + msg.address());
                        rc.response().end(msg.body());
                    }, rc::fail);
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

}
