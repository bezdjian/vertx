package se.hb.vertxstockbroker.api.quotes.model;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;
import se.hb.vertxstockbroker.api.assets.model.Asset;

import java.math.BigDecimal;

@Value
@Builder
public class Quote {

  int id;
  Asset asset;
  BigDecimal bid;
  BigDecimal ask;
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
