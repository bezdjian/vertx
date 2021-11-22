package se.hb.monitoringservice.util;

import io.vertx.core.json.Json;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.experimental.UtilityClass;
import se.hb.monitoringservice.model.ErrorResponse;

@UtilityClass
public class ResponseUtil {

  private final String CONTENT_TYPE_JSON = "application/json";

  public void sendSuccessResponse(RoutingContext rc, int statusCode) {
    rc.response()
      .setStatusCode(statusCode)
      .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
      .end();
  }

  public void sendSuccessResponse(RoutingContext rc, int statusCode, Object data) {
    rc.response()
      .setStatusCode(statusCode)
      .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
      .end(Json.encode(data));
  }

  public void sendErrorResponse(RoutingContext rc, int statusCode, Throwable error) {
    rc.response()
      .setStatusCode(statusCode)
      .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
      .end(buildErrorResponse(statusCode, error));
  }

  private String buildErrorResponse(int status, Throwable error) {
    return ErrorResponse.builder()
      .status(status)
      .message(error.getMessage())
      .build()
      .toString();
  }
}
