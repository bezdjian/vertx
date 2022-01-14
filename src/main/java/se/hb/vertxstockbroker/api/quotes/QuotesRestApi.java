package se.hb.vertxstockbroker.api.quotes;

import io.vertx.pgclient.PgPool;
import io.vertx.rxjava3.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.GetAssetQuotesHandler;
import se.hb.vertxstockbroker.repository.ServiceRepository;

@Slf4j
public class QuotesRestApi {

  public static void addToRoute(Router router, PgPool pgPool) {
    router.get("/api/quotes")
      .handler(new GetQuotesHandler(new ServiceRepository(pgPool)));
  }
}
