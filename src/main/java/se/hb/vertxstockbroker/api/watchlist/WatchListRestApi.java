package se.hb.vertxstockbroker.api.watchlist;

import io.vertx.pgclient.PgPool;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.repository.ServiceRepository;

@Slf4j
public class WatchListRestApi {

  public static final int ONE_MB = 1024;

  public static void addToRoute(Router router, PgPool pgPool) {
    var basePath = "/account/watchlist/:accountId";

    router.route()
      .handler(createBodyHandler())
      .handler(ResponseContentTypeHandler.create())
      .produces("application/json")
      .failureHandler(WatchListRestApi::handleFailure);

    router.get(basePath)
      .handler(new GetWatchListHandler(new ServiceRepository(pgPool)));

    router.post(basePath)
      .handler(new SaveWatchListHandler(new ServiceRepository(pgPool)));

    router.delete(basePath)
      .handler(new DeleteWatchListHandler(new ServiceRepository(pgPool)));
  }

  public static String logAndGetAccountId(RoutingContext ctx) {
    var accountId = ctx.pathParam("accountId");
    log.info("Requested path {} {}", ctx.currentRoute().methods().toString(), ctx.normalizedPath());
    return accountId;
  }

  private static BodyHandler createBodyHandler() {
    return BodyHandler.create()
      .setHandleFileUploads(true)
      .setBodyLimit(ONE_MB);
  }

  private static void handleFailure(RoutingContext ctx) {
    String errorMessage = ctx.failure().getMessage();
    log.info("Error from Watchlist rest api router: {}", errorMessage);
    ctx.response().end(errorMessage);
  }
}
