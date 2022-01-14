package se.hb.vertxstockbroker.db;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Queries {
  public final String GET_ASSET_BY_ID = """
    SELECT a.id, a.name FROM broker.assets a where a.id=#{id}
    """;

  public final String SELECT_ASSET_QUOTES = """
    SELECT a.id as asset_id, a.name as asset_name,
    q.id, q.bid, q.ask, q.last_price, q.volume
    from broker.quotes q
    join broker.assets a on a.id = q.asset
    where q.asset=#{assetId}
    group by q.id, a.id
    """;

  public final String SELECT_QUOTES = """
    SELECT q.id, q.bid, q.ask, q.volume, q.asset, q.last_price, a.id as asset_id, a.name as asset_name
    FROM broker.quotes q
    join broker.assets a on a.id = q.asset
    """;

  public final String SELECT_WATCH_LISTS = """
    SELECT w.account_id, w.asset, a.name as assetName
    FROM broker.watchlist w
    join broker.assets a on a.id = w.asset
    where w.account_id =#{accountId}
    """;

  //ON CONFLICT (account_id, asset) DO NOTHING
  public static final String INSERT_WATCHLIST = """
    INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset})
    """;

  public static final String DELETE_WATCHLIST_BY_ACCOUNT = """
    DELETE FROM broker.watchlist where account_id=#{accountId}
    """;
}
