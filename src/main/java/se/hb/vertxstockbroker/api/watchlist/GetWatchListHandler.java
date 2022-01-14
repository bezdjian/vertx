package se.hb.vertxstockbroker.api.watchlist;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.RowSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.repository.ServiceRepository;
import se.hb.vertxstockbroker.util.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class GetWatchListHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    var accountId = WatchListRestApi.logAndGetAccountId(ctx);
    serviceRepository.getWatchListByAccount(accountId)
      .onFailure(err -> ResponseUtil.returnResponse(ctx, err.getMessage(), 500))
      .onSuccess(watchlistJson -> {
        if (!watchlistJson.iterator().hasNext()) {
          returnNotFound(ctx, accountId);
        }
        returnWatchLists(ctx, watchlistJson);
      });
  }

  private void returnWatchLists(RoutingContext ctx, RowSet<JsonObject> watchlistJson) {
    var response = new JsonArray();
    watchlistJson.forEach(response::add);
    log.info("Fetched watchlist {}", response.encode());
    ctx.response()
      .end(response.encode());
  }

  private void returnNotFound(RoutingContext ctx, String accountId) {
    String message = String.format("Watchlist with account %s not found", accountId);
    ctx.response().setStatusCode(404)
      .end(new JsonObject().put("message", message).encode());
  }
}
