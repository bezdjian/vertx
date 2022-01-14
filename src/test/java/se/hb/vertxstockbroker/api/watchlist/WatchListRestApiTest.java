package se.hb.vertxstockbroker.api.watchlist;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.hb.vertxstockbroker.MainVerticle;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.api.watchlist.model.WatchList;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(VertxExtension.class)
class WatchListRestApiTest {

  public static final String ACCOUNT_WATCHLIST_URL = "/account/watchlist/";
  private WebClient client;

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    client = WebClient.create(vertx,
      new WebClientOptions().setDefaultPort(8080));

    vertx.deployVerticle(new MainVerticle())
      .doOnSuccess(id -> testContext.completeNow())
      .subscribe();
  }

  @Test
  void shouldAddAndReturnWatchListsByAccountId(VertxTestContext testContext) {
    //Given
    var accountIdRequest = UUID.randomUUID();
    JsonObject jsonBodyRequest = createWatchlistJsonBody();
    String expectedAssetsResponse = "[{\"name\":\"AMZN-TST\"},{\"name\":\"TSLA-TST\"}]";

    //When
    client.put(ACCOUNT_WATCHLIST_URL + accountIdRequest)
      .sendJsonObject(jsonBodyRequest)
      .doOnSuccess(response -> testContext.verify(() -> {
        //Then
        var accountIdResult = response.bodyAsJsonObject().getString("accountId");
        //Verify
        assertEquals(200, response.statusCode());
        assertEquals(accountIdRequest.toString(), accountIdResult);
      }))
      .doAfterSuccess(response -> {
        var accountIdResponse = response.bodyAsJsonObject().getString("accountId");
        assertEquals(accountIdRequest.toString(), accountIdResponse);

        client.get(ACCOUNT_WATCHLIST_URL + accountIdResponse)
          .send()
          .doOnSuccess(watchlist -> testContext.verify(() -> {
            var assetsResult = watchlist.bodyAsJsonObject().getJsonArray("assets");
            assertEquals(expectedAssetsResponse, assetsResult.encode());
            testContext.completeNow();
          })).subscribe();
      }).subscribe();
  }

  @Test
  void shouldAddAndDeleteWatchListsByAccountId(VertxTestContext testContext) {
    //Given
    var accountIdRequest = UUID.randomUUID();
    JsonObject jsonBodyRequest = createWatchlistJsonBody();

    //When
    client.put(ACCOUNT_WATCHLIST_URL + accountIdRequest)
      .sendJsonObject(jsonBodyRequest)
      //.blockingGet()
      .doOnSuccess(putResponse -> testContext.verify(() -> {
        //Then
        var accountIdResult = putResponse.bodyAsJsonObject().getString("accountId");
        //Verify
        assertEquals(200, putResponse.statusCode());
        assertEquals(accountIdRequest.toString(), accountIdResult);
      }))
      .doAfterSuccess(putResponse -> {
        var accountIdResponse = putResponse.bodyAsJsonObject().getString("accountId");
        assertEquals(accountIdRequest.toString(), accountIdResponse);

        client.delete(ACCOUNT_WATCHLIST_URL + accountIdResponse)
          .send()
          .doOnSuccess(deleteResponse -> testContext.verify(() -> {
            assertNull(deleteResponse.body());
            assertEquals(204, deleteResponse.statusCode());

            assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(),
              deleteResponse.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
            testContext.completeNow();
          })).subscribe();
      }).subscribe();
  }

  private JsonObject createWatchlistJsonBody() {
    return new WatchList(List.of(
      new Asset(1, "AMZN-TST"),
      new Asset(2, "TSLA-TST")))
      .toJsonObject();
  }
}
