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
  public static final String LOCALHOST_PASSWORD = "root";
  public static final String LOCALHOST_DB_NAME = "dev";

  public MySQLPool createConnection(Vertx vertx) {

    String databaseHost = getDatabaseHost();
    String databaseUser = getDatabaseUser();
    String databasePassword = getDatabasePassword();
    String databaseName = getDatabaseName();

    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setCharset("utf8")
      .setPort(3306)
      .setHost(databaseHost)
      .setDatabase(databaseName)
      .setUser(databaseUser)
      .setPassword(databasePassword);

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

  private String getDatabasePassword() {
    return Optional.ofNullable(System.getenv("DATABASE_PASSWORD"))
      .orElse(LOCALHOST_PASSWORD);
  }

  private String getDatabaseName() {
    return Optional.ofNullable(System.getenv("DATABASE_NAME"))
      .orElse(LOCALHOST_DB_NAME);
  }
}
