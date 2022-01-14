package se.hb.vertxstockbroker.api.slow;

import io.vertx.rxjava3.ext.web.Router;

public class SlowRestApi {

  public static void addToRoute(Router router) {
    router.get("/api/slow")
      .handler(new SlowApiHandler());
  }
}
