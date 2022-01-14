package se.hb.vertxstockbroker;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.AssetsRestApi;
import se.hb.vertxstockbroker.api.quotes.QuotesRestApi;
import se.hb.vertxstockbroker.api.slow.SlowRestApi;
import se.hb.vertxstockbroker.api.watchlist.WatchListRestApi;
import se.hb.vertxstockbroker.config.BrokerConfig;
import se.hb.vertxstockbroker.config.ConfigLoader;
import se.hb.vertxstockbroker.config.DbConfig;
import se.hb.vertxstockbroker.db.FlywayMigration;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    ConfigLoader.load(vertx)
      .doOnSuccess(this::migrateDatabase)
      .doOnSuccess(this::startHttpServer)
      .doOnError(this::logAndThrow)
      .subscribe();
    //return startHttpServer(8080);
    //TODO: Find a better solution
    return Completable.complete();
  }

  private void migrateDatabase(BrokerConfig config) {
    log.info("*** ConfigLoader.load success. Migrating database...");
    FlywayMigration.migrate(vertx, config)
      .doOnComplete(() -> log.info("*** Migration complete!"))
      .doOnError(this::logAndThrow)
      .subscribe();
  }

  private void startHttpServer(BrokerConfig config) {
    log.info("Starting server on port {}...", config.getServerPort());
    final Router router = Router.router(vertx);
    router.route().failureHandler(this::handleRoutingFailure);

    var pgPool = createPgPool(config.getDbConfig());

    log.info("PgPool created. {}", pgPool.getConnection().toString());
    addApisToRouter(router, pgPool);

    vertx.createHttpServer()
      .requestHandler(router)
      .exceptionHandler(this::logError)
      .rxListen(config.getServerPort())
      .doOnSuccess(http -> log.info("HTTP server started on port {}", config.getServerPort()))
      .doOnError(this::logAndThrow)
      .subscribe();
  }

  private PgPool createPgPool(DbConfig dbConfig) {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setHost(dbConfig.getHost())
      .setPort(dbConfig.getPost())
      .setPassword(dbConfig.getPassword())
      .setUser(dbConfig.getUser())
      .setDatabase(dbConfig.getName());

    return PgPool.pool(vertx.getDelegate(), pgConnectOptions,
      new PoolOptions().setMaxSize(4));
  }

  private void logAndThrow(Throwable e) throws Throwable {
    log.info("Error occurred: {}", e.getMessage());
    throw e;
  }

  private void addApisToRouter(Router router, PgPool pgPool) {
    AssetsRestApi.addToRoute(router, pgPool);
    WatchListRestApi.addToRoute(router, pgPool);
    QuotesRestApi.addToRoute(router, pgPool);
    SlowRestApi.addToRoute(router);
  }

  private void handleRoutingFailure(RoutingContext ctx) {
    log.info("Error while routing: {}", ctx.failure().getMessage());
    ctx.failure().printStackTrace();
    JsonObject response = new JsonObject()
      .put("status", 500)
      .put("message", ctx.failure().getMessage());
    ctx.response().end(response.encode());
  }

  private void logError(Throwable error) {
    error.printStackTrace();
    log.error("HTTP server error: {}", error.getMessage());
  }
}
