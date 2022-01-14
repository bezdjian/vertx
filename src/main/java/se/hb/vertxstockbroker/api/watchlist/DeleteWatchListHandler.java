package se.hb.vertxstockbroker.api.watchlist;

import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlResult;
import lombok.RequiredArgsConstructor;
import se.hb.vertxstockbroker.repository.ServiceRepository;
import se.hb.vertxstockbroker.util.ResponseUtil;

@RequiredArgsConstructor
public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    var accountId = WatchListRestApi.logAndGetAccountId(ctx);
    serviceRepository.deleteWatchList(accountId)
      .onFailure(err -> ResponseUtil.returnResponse(ctx, err.getMessage(), 500))
      .onSuccess(event -> returnResponse(ctx, accountId, event));
  }

  private void returnResponse(RoutingContext ctx, String accountId, SqlResult<Void> event) {
    String message = String.format("Successfully deleted %d watchlist(s) with account %s", event.rowCount(), accountId);
    ResponseUtil.returnResponse(ctx, message, 200);
  }
}
