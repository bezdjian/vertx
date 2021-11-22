package se.hb.monitoringservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {

  @JsonProperty
  int status;
  @JsonProperty
  String message;

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
