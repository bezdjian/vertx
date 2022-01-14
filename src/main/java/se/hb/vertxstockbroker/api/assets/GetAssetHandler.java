package se.hb.vertxstockbroker.api.assets;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.repository.ServiceRepository;

@Slf4j
@RequiredArgsConstructor
public class GetAssetHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    log.info("Requesting path {} ", ctx.normalizedPath());
    final var assetId = ctx.pathParam("assetId");
    serviceRepository.getAssetOptional(Integer.parseInt(assetId))
      .onFailure(err -> ctx.response().setStatusCode(404).end("Asset not found"))
      .onSuccess(asset ->
        asset.ifPresentOrElse(a -> returnAssetResponse(ctx, a),
          () -> ctx.response()
            .setStatusCode(404)
            .end(new JsonObject().put("message", "Asset not found").encode())));
  }

  private void returnAssetResponse(RoutingContext ctx, Asset asset) {
    log.info("Retrieved {} assets from database", asset);
    final JsonArray response = new JsonArray().add(asset);
    ctx.response().end(response.encode());
  }
}
