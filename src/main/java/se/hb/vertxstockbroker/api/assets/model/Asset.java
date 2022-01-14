package se.hb.vertxstockbroker.api.assets.model;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
  private int id;
  private String name;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
