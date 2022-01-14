package se.hb.websockets;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.ServerWebSocket;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class PriceBroadcast {

  private final Map<String, ServerWebSocket> connectedClients = new HashMap<>();

  public PriceBroadcast(Vertx vertx) {
    periodicUpdate(vertx);
  }

  public void register(ServerWebSocket ws) {
    connectedClients.put(ws.textHandlerID(), ws);
  }

  public void unregister(ServerWebSocket ws) {
    connectedClients.remove(ws.textHandlerID());
  }

  private void periodicUpdate(Vertx vertx) {
    vertx.setPeriodic(Duration.ofSeconds(2).toMillis(),
      id -> updatePricesToClients());
  }

  private void updatePricesToClients() {
    log.info("Push update to {} client(s)", connectedClients.size());
    String updatedPrices = getUpdatedPrices();
    connectedClients.values()
      .forEach(clients -> update(clients, updatedPrices));
  }

  private String getUpdatedPrices() {
    return new JsonObject()
      .put("symbol", "AMZN")
      .put("value", new Random().nextInt())
      .encode();
  }

  private void update(ServerWebSocket clients, String updatedPrices) {
    clients.rxWriteTextMessage(updatedPrices)
      .subscribe();
  }
}
