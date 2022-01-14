package se.hb.vertxstockbroker.api.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.client.HttpResponse;
import io.vertx.rxjava3.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import se.hb.vertxstockbroker.MainVerticle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(VertxExtension.class)
class AssetsRestApiTest {

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle())
      .doOnSuccess(id -> testContext.completeNow())
      .subscribe();
  }

  @Test
  void shouldReturnAllAssets(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx,
      new WebClientOptions().setDefaultPort(8080));

    client.get("/api/assets")
      .send()
      .doOnSuccess(response -> testContext.verify(() -> {
        JsonArray responseArray = response.bodyAsJsonArray();
        assertFalse(responseArray.isEmpty());

        assertSimulateSlowResponseError(response, responseArray);

        testContext.completeNow();
      })).subscribe();
  }

  private void assertSimulateSlowResponseError(HttpResponse<Buffer> response, JsonArray responseArray) {
    if (responseArray.encode().contains("error")) {
      assertEquals(500, response.statusCode());
    } else {
      assertEquals(200, response.statusCode());
    }
  }
}
