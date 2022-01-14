package se.hb.vertx;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/vertx")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class VertxResource {

  private final WebClient client;

  public VertxResource(Vertx vertx) {
    this.client = WebClient.create(vertx,
        new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8080));
  }

  @GET
  public Uni<JsonArray> get() {
    log.info("getAllUsers Vertx...");
    JsonArray item = new JsonArray()
        .add(new JsonObject().put("name", "Bob"))
        .add(new JsonObject().put("name", "Charlie"));

    return Uni.createFrom().item(item);
  }

  @GET
  @Path("/users")
  public Uni<JsonArray> getUsersFromUserResource() {
    return client.get("/users")
        .send()
        .onItem()
        .transform(HttpResponse::bodyAsJsonArray);
  }
}
