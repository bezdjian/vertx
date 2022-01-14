package se.hb.websockets;

import io.vertx.core.Handler;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.http.ServerWebSocket;
import io.vertx.rxjava3.core.http.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandler implements Handler<ServerWebSocket> {

  public static final String PATH = "/ws/prices";
  private final PriceBroadcast priceBroadcast;

  public WebSocketHandler(Vertx vertx) {
    this.priceBroadcast = new PriceBroadcast(vertx);
  }

  @Override
  public void handle(ServerWebSocket ws) {
    if (!ws.path().equalsIgnoreCase(PATH)) {
      var message = String.format("Wrong path %s, connection not established", ws.path());
      log.warn(message);
      ws.rxWriteFinalTextFrame(message);
      ws.close((short) 1000, message);
      return;
    }

    ws.accept();
    ws.frameHandler(event -> handleCommands(ws, event));

    priceBroadcast.register(ws);
    ws.exceptionHandler(err -> log.info("Error while trying to connect! {}", err.getMessage()))
      .endHandler(onClose -> endConnection(ws))
      .rxWriteTextMessage("Connected!")
      .doOnSubscribe(s -> log.info("Websocket connection accepted: {}, {}", ws.path(), ws.textHandlerID()))
      .doOnError(e -> log.error("Error while connecting to web socket {}, {}", ws.textHandlerID(), e.getMessage()))
      .subscribe();
  }

  private void handleCommands(ServerWebSocket ws, WebSocketFrame event) {
    var textData = event.textData();
    if (event.isClose() || textData.equals("disconnect")) {
      closeConnection(ws);
      return;
    }

    log.info("Received text from client {}, ID: {}", textData, ws.textHandlerID());
    ws.rxWriteTextMessage("Not supported => (" + textData + ")")
      .subscribe();
  }

  private void closeConnection(ServerWebSocket ws) {
    ws.close((short) 1000, "Client close request");
    priceBroadcast.unregister(ws);
  }

  private void endConnection(ServerWebSocket ws) {
    log.info("Closed connection {}", ws.textHandlerID());
    priceBroadcast.unregister(ws);
  }
}
