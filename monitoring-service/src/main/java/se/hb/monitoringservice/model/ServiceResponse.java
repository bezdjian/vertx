package se.hb.monitoringservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceResponse {

  @JsonProperty
  private long id;
  @JsonProperty
  private String name;
  @JsonProperty
  private String url;
  @JsonProperty
  private String created;
  @JsonProperty
  private String status;

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
