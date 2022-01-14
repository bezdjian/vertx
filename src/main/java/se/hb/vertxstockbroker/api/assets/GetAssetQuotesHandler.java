package se.hb.vertxstockbroker.api.assets;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.sqlclient.RowSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.api.quotes.model.Quote;
import se.hb.vertxstockbroker.api.quotes.model.AssetQuote;
import se.hb.vertxstockbroker.repository.ServiceRepository;

@Slf4j
@RequiredArgsConstructor
public class GetAssetQuotesHandler implements Handler<RoutingContext> {

  private final ServiceRepository serviceRepository;

  @Override
  public void handle(RoutingContext ctx) {
    final var assetId = ctx.pathParam("assetId");
    log.info("Asset ID in param {}", assetId);

    serviceRepository.getAssetQuotes(Integer.parseInt(assetId))
      .onFailure(err -> returnError(ctx, assetId, err))
      .onSuccess(quoteRows -> {
        log.info("*** Found {} quotes with asset ID {}", quoteRows.size(), assetId);
        if (quoteRows.size() == 0) {
          returnNotFound(ctx, "Quote with asset id %s not found", assetId);
        }
        returnAssetQuotes(ctx, quoteRows);
      });
  }

  private void returnAssetQuotes(RoutingContext ctx, RowSet<AssetQuote> quoteRows) {
    final var quotesJsonArray = new JsonArray();
    quoteRows.forEach(quoteRow -> {
      final var assetName = quoteRow.getAssetName();
      Integer quoteId = quoteRow.getId();
      log.info("*** Successfully fetched quote with ID {} with asset {}", quoteId, assetName);
      addQuotesInResponseObject(quotesJsonArray, quoteRow);
    });
    ctx.response().end(quotesJsonArray.encode());
  }

  private void addQuotesInResponseObject(JsonArray response, AssetQuote assetQuote) {
    log.info("*** Creating Quote object for quote ID {}", assetQuote.getId());
    var quote = Quote.builder()
      .asset(new Asset(assetQuote.getAssetId(), assetQuote.getAssetName()))
      .bid(assetQuote.getBid())
      .ask(assetQuote.getAsk())
      .volume(assetQuote.getVolume())
      .lastPrice(assetQuote.getLastPrice())
      .build();
    response.add(quote);
  }

  private void returnError(RoutingContext ctx, String assetId, Throwable err) {
    log.info("Failed while fetching asset with id {}: {}", assetId, err.getMessage());
    ctx.response()
      .setStatusCode(500)
      .end(new JsonObject()
        .put("message", err.getMessage())
        .encode());
  }

  private void returnNotFound(RoutingContext ctx, String message, Object... args) {
    String messageBody = String.format(message, args);
    String responseJson = createResponseJson(messageBody);
    ctx.response()
      .setStatusCode(404)
      .end(responseJson);
  }

  private String createResponseJson(String message) {
    return new JsonObject()
      .put("status", 404)
      .put("message", message)
      .encode();
  }
}
