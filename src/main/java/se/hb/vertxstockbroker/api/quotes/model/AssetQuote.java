package se.hb.vertxstockbroker.api.quotes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetQuote {
  private int id;
  @JsonProperty("asset_id")
  private int assetId;
  @JsonProperty("asset_name")
  private String assetName;
  private BigDecimal bid;
  private BigDecimal ask;
  @JsonProperty("last_price")
  private BigDecimal lastPrice;
  private BigDecimal volume;
}
