package qcon;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

public class MessageSenderServer extends AbstractVerticle {

    @Override
    public void start() {
        //TODO: rxJava3
        //io.vertx.rxjava3.ext.web.Router router = io.vertx.rxjava3.ext.web.Router.router(vertx);

        Router router = Router.router(vertx);
        router.get("/greet").handler(rc -> {
            String name = rc.request().getParam("name");
            System.out.println("Sending message with name: " + name);
            vertx.eventBus().<String>request("greeting", name, ar -> {
                if (ar.succeeded()) {
                    rc.response().end(ar.result().body());
                } else {
                    rc.fail(ar.cause());
                }
            });
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

}
