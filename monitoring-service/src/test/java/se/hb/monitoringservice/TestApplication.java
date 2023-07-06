package se.hb.monitoringservice;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@ExtendWith(VertxExtension.class)
class TestApplication {

  @Container
  static JdbcDatabaseContainer mySQLContainer = new MySQLContainer("mysql")
    .withUsername("test")
    .withPassword("test")
    .withDatabaseName("test")
    .withInitScript("db/init.sql");

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new Application().withDatabaseConfig(
        mySQLContainer.getFirstMappedPort(), mySQLContainer.getUsername(),
        mySQLContainer.getPassword(), mySQLContainer.getDatabaseName(),
        mySQLContainer.getHost()),
      testContext.succeedingThenComplete());
  }

  @Test
  void shouldFetchAllServicesSuccessfully(Vertx vertx, VertxTestContext testContext) {
    //When & then
    WebClient.create(vertx)
      .get(8881, "::1", "/api/services")
      .send(response -> testContext.verify(() -> {
        assertEquals(200, response.result().statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void shouldPostServiceSuccessfully(Vertx vertx, VertxTestContext testContext) {
    //Given
    JsonObject jsonBody = new JsonObject()
      .put("name", "My service name")
      .put("url", "https://mock.codes/200");

    //When & then
    WebClient.create(vertx)
      .post(8881, "::1", "/api/services")
      .sendJsonObject(jsonBody, response ->
        testContext.verify(() -> {
          assertEquals(201, response.result().statusCode());
          String body = response.result().bodyAsString();
          assertNull(body);
          testContext.completeNow();
        }));
  }

  @Test
  void shouldReturnErrorWhenInvalidUrlIsSaved(Vertx vertx, VertxTestContext testContext) {
    //Given
    JsonObject jsonBody = new JsonObject()
      .put("name", "My service name")
      .put("url", "someDummyUrl");

    //When & then
    WebClient.create(vertx)
      .post(8881, "::1", "/api/services")
      .sendJsonObject(jsonBody, response -> testContext.verify(() -> {
        assertEquals(500, response.result().statusCode());
        String body = response.result().bodyAsString();
        assertEquals("Internal Server Error", body);
        testContext.completeNow();
      }));
  }
}
