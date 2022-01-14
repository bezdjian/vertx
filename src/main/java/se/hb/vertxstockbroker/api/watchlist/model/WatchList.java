package se.hb.vertxstockbroker.api.watchlist.model;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.hb.vertxstockbroker.api.assets.model.Asset;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {
  private List<Asset> assets;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
