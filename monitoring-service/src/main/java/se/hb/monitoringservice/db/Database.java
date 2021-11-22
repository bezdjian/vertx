package se.hb.monitoringservice.db;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class Database {

  public static final String LOCALHOST = "localhost";
  public static final String LOCALHOST_USER = "root";

  public MySQLPool createConnection(Vertx vertx) {

    String databaseHost = getDatabaseHost();
    String databaseUser = getDatabaseUser();

    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setCharset("utf8")
      .setPort(3306)
      .setHost(databaseHost)
      .setDatabase("dev")
      .setUser(databaseUser)
      .setPassword("secret");

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    return MySQLPool.pool(vertx, connectOptions, poolOptions);
  }

  private String getDatabaseHost() {
    return Optional.ofNullable(System.getenv("DATABASE_HOST"))
      .orElse(LOCALHOST);
  }

  private String getDatabaseUser() {
    return Optional.ofNullable(System.getenv("DATABASE_USER"))
      .orElse(LOCALHOST_USER);
  }
}
