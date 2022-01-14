package se.hb.vertxstockbroker.api.quotes;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.hb.vertxstockbroker.MainVerticle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(VertxExtension.class)
class QuotesRestApiTest {

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
  void shouldReturnQuote(Vertx vertx, VertxTestContext testContext) {
    //When
    client.get("/api/quotes/AMZN")
      .send()
      .doOnSuccess(response -> testContext.verify(() -> {
        //Then
        JsonObject responseJson = response.bodyAsJsonObject();
        String assetJson = responseJson.getJsonObject("asset").getString("name");

        //Verify
        assertEquals(200, response.statusCode());
        assertFalse(responseJson.isEmpty());
        assertEquals("AMZN", assetJson);
        testContext.completeNow();
      })).subscribe();
  }

  @Test
  void shouldReturnQuoteNotFound(Vertx vertx, VertxTestContext testContext) {
    //Given
    String noneExistingAsset = "UNKNOWN";

    //When
    client.get("/api/quotes/" + noneExistingAsset)
      .send()
      .doOnSuccess(response -> testContext.verify(() -> {
        //Then
        JsonObject responseJson = response.bodyAsJsonObject();
        String messageJson = responseJson.getString("message");

        //Verify
        assertEquals(404, response.statusCode());
        assertFalse(responseJson.isEmpty());
        String expectedMessage = String.format("Quote with asset %s not found", noneExistingAsset);
        assertEquals(expectedMessage, messageJson);
        testContext.completeNow();
      })).subscribe();
  }
}
