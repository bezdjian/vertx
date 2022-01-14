package se.hb.vertxstockbroker.api.watchlist;

import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.repository.ServiceRepository;
import se.hb.vertxstockbroker.util.ResponseUtil;

@Slf4j
@RequiredArgsConstructor
public class SaveWatchListHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    var accountId = WatchListRestApi.logAndGetAccountId(ctx);
    var body = ctx.getBodyAsJson();
    log.info("*** Save watchlist body: {}", body.encode());
    serviceRepository.saveWatchList(accountId, body)
      .onFailure(err -> returnResponse(ctx, err))
      .onSuccess(event -> ResponseUtil.returnResponse(ctx,
        "Successfully inserted watchlist asset for account " + accountId, 201));
  }

  private void returnResponse(RoutingContext ctx, Throwable err) {
    String message = err.getMessage();
    int statusCode = 500;
    if (message.contains("duplicate key value")) {
      message = "Account id already has the asset associated";
      statusCode = 400;
    }
    ResponseUtil.returnResponse(ctx, message, statusCode);
  }
}
