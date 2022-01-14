package se.hb.vertxstockbroker.api.assets;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.repository.ServiceRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetAssetsHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    log.info("Path {} responds with {}", ctx.normalizedPath(), UUID.randomUUID());
    serviceRepository.getAssets()
      .onFailure(err -> respondFailure(ctx, err))
      .onSuccess(result -> returnAssets(ctx, result));
  }

  private void returnAssets(RoutingContext ctx, RowSet<Row> result) {
    final JsonArray response = new JsonArray();
    log.info("Retrieved {} assets from database", result.size());
    result.forEach(row -> addAssetsToResponse(row, response));
    ctx.response().end(response.encode());
  }

  private void addAssetsToResponse(Row row, JsonArray response) {
    var asset = new Asset(row.getInteger("id"),
      row.getString("name"));
    response.add(asset);
  }

  private void respondFailure(RoutingContext ctx, Throwable err) {
    log.info("Failed to get assets from DB: {}", err.getMessage());
    ctx.response()
      .setStatusCode(500)
      .end(new JsonObject()
        .put("message", err.getMessage())
        .put("path", ctx.normalizedPath())
        .encode());
  }
}
