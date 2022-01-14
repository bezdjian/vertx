package se.hb.vertxstockbroker.api.quotes;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.api.quotes.model.Quote;
import se.hb.vertxstockbroker.repository.ServiceRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetQuotesHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    log.info("Path {} responds with {}", ctx.normalizedPath(), UUID.randomUUID());
    serviceRepository.getQuotes()
      .onFailure(err -> respondFailure(ctx, err))
      .onSuccess(result -> returnQuotes(ctx, result));
  }

  private void returnQuotes(RoutingContext ctx, RowSet<Row> result) {
    final JsonArray response = new JsonArray();
    log.info("Retrieved {} assets from database", result.size());
    result.forEach(row -> addQuotesToResponse(row, response));
    ctx.response().end(response.encode());
  }

  private void addQuotesToResponse(Row row, JsonArray response) {
    var quote = Quote.builder()
      .id(row.getInteger("id"))
      .asset(new Asset(row.getInteger("asset_id"), row.getString("asset_name")))
      .bid(row.getBigDecimal("bid"))
      .ask(row.getBigDecimal("ask"))
      .volume(row.getBigDecimal("volume"))
      .lastPrice(row.getBigDecimal("last_price"))
      .build();
    response.add(quote);
  }

  private void respondFailure(RoutingContext ctx, Throwable err) {
    log.info("Failed to get quotes from DB: {}", err.getMessage());
    ctx.response()
      .setStatusCode(500)
      .end(new JsonObject()
        .put("message", err.getMessage())
        .put("path", ctx.normalizedPath())
        .encode());
  }
}
