package se.hb.vertxstockbroker.util;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ResponseUtil {

  public void returnResponse(RoutingContext ctx, String message, int statusCode) {
    log.info(message);
    ctx.response()
      .setStatusCode(statusCode)
      .end(new JsonObject()
        .put("message", message)
        .encode());
  }
}
