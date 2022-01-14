package se.hb.vertxstockbroker.repository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.api.quotes.model.AssetQuote;
import se.hb.vertxstockbroker.api.watchlist.model.WatchList;
import se.hb.vertxstockbroker.db.Queries;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ServiceRepository {

  private final PgPool pgPool;

  public Future<RowSet<Row>> getAssets() {
    log.info("*** Querying assets...");
    return pgPool.query("SELECT a.id, a.name FROM broker.assets a")
      .execute();
  }

  public Future<RowSet<Row>> getQuotes() {
    log.info("*** Querying quotes...");
    return pgPool.query(Queries.SELECT_QUOTES)
      .execute();
  }

  public Future<Optional<Asset>> getAssetOptional(int id) {
    log.info("*** Querying asset with id {}...", id);
    final Map<String, Object> param = new HashMap<>();
    param.put("id", id);
    return SqlTemplate.forQuery(pgPool, Queries.GET_ASSET_BY_ID)
      .mapTo(Asset.class)
      .execute(param)
      .map(this::mapAsset);
  }

  public Future<RowSet<AssetQuote>> getAssetQuotes(int assetId) {
    HashMap<String, Object> params = new HashMap<>();
    params.put("assetId", assetId);
    return SqlTemplate.forQuery(pgPool, Queries.SELECT_ASSET_QUOTES)
      .mapTo(AssetQuote.class)
      .execute(params);
  }

  public Future<RowSet<JsonObject>> getWatchListByAccount(String accountId) {
    Map<String, Object> params = Collections.singletonMap("accountId", accountId);

    return SqlTemplate.forQuery(pgPool, Queries.SELECT_WATCH_LISTS)
      .mapTo(Row::toJson)
      .execute(params)
      .onFailure(err -> log.info("Failed while fetching watchlist for accountId {}: {}", accountId, err.getMessage()));
  }

  public Future<SqlResult<Void>> saveWatchList(String accountId, JsonObject body) {
    var parameters = createParameters(accountId, body);
    return SqlTemplate.forUpdate(pgPool, Queries.INSERT_WATCHLIST)
      .executeBatch(parameters);
  }

  public Future<SqlResult<Void>> deleteWatchList(String accountId) {
    return SqlTemplate.forUpdate(pgPool, Queries.DELETE_WATCHLIST_BY_ACCOUNT)
      .execute(Collections.singletonMap("accountId", accountId));
  }

  private List<Map<String, Object>> createParameters(String accountId, JsonObject body) {
    return body.mapTo(WatchList.class).getAssets()
      .stream()
      .map(asset -> mapParams(accountId, asset))
      .collect(Collectors.toList());
  }

  private Map<String, Object> mapParams(String accountId, Asset asset) {
    Map<String, Object> params = new HashMap<>();
    params.put("account_id", accountId);
    params.put("asset", asset.getId());
    return params;
  }

  private Optional<Asset> mapAsset(RowSet<Asset> assetRow) {
    if (assetRow.size() == 0) {
      return Optional.empty();
    } else {
      return mapAssetFromRow(assetRow);
    }
  }

  private Optional<Asset> mapAssetFromRow(RowSet<Asset> assetRow) {
    final var asset = new Asset();
    assetRow.forEach(row -> map(asset, row));
    return Optional.of(asset);
  }

  private void map(Asset asset, Asset row) {
    log.info("Mapping asset {}", row.toJsonObject());
    asset.setId(row.getId());
    asset.setName(row.getName());
  }

  private BigDecimal generateRandomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
