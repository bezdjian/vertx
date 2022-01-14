package se.hb.vertxstockbroker.api.assets;

import io.vertx.pgclient.PgPool;
import io.vertx.rxjava3.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.repository.ServiceRepository;

@Slf4j
public class AssetsRestApi {

  public static void addToRoute(Router router, PgPool pgPool) {
    router.get("/api/assets")
      .handler(new GetAssetsHandler(new ServiceRepository(pgPool)));
    router.get("/api/assets/:assetId")
      .handler(new GetAssetHandler(new ServiceRepository(pgPool)));
    router.get("/api/assets/:assetId/quotes")
      .handler(new GetAssetQuotesHandler(new ServiceRepository(pgPool)));
  }
}
