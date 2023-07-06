package se.hb.monitoringservice.db;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class Database {

  // This Database works with either from
  // test container -> TestApplication.java
  // localhost by running 'gradle run' -> which send databaseConfig null, hence orElseGet() methods
  // or docker-compose that is set by environment variables.
  public static final String LOCALHOST = "localhost";
  public static final String LOCALHOST_DB_USER = "root";
  public static final String LOCALHOST_DB_PASSWORD = "root";
  public static final String LOCALHOST_DB_NAME = "dev";
  public static final int LOCALHOST_DB_PORT = 3306;

  public MySQLPool createConnection(Vertx vertx, DatabaseConfig databaseConfig) {

    String databaseHost = getDatabaseHost(databaseConfig);
    String databaseUser = getDatabaseUser(databaseConfig);
    String databasePassword = getDatabasePassword(databaseConfig);
    String databaseName = getDatabaseName(databaseConfig);
    int databasePort = getDatabasePort(databaseConfig);

    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setCharset("utf8")
      .setPort(databasePort)
      .setHost(databaseHost)
      .setDatabase(databaseName)
      .setUser(databaseUser)
      .setPassword(databasePassword);

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    return MySQLPool.pool(vertx, connectOptions, poolOptions);
  }

  private static int getDatabasePort(DatabaseConfig databaseConfig) {
    String port = Optional.ofNullable(System.getenv("DATABASE_PORT"))
      .orElseGet(() -> String.valueOf(databaseConfig != null ? databaseConfig.dbPort() : LOCALHOST_DB_PORT));
    return Integer.parseInt(port);
  }

  private String getDatabaseHost(DatabaseConfig databaseConfig) {
    return Optional.ofNullable(System.getenv("DATABASE_HOST"))
      .orElseGet(() -> databaseConfig != null ? databaseConfig.dbHost() : LOCALHOST);
  }

  private String getDatabaseUser(DatabaseConfig databaseConfig) {
    return Optional.ofNullable(System.getenv("DATABASE_USER"))
      .orElseGet(() -> databaseConfig != null ? databaseConfig.dbUser() : LOCALHOST_DB_USER);
  }

  private String getDatabasePassword(DatabaseConfig databaseConfig) {
    return Optional.ofNullable(System.getenv("DATABASE_PASSWORD"))
      .orElseGet(() -> databaseConfig != null ? databaseConfig.dbPass() : LOCALHOST_DB_PASSWORD);
  }

  private String getDatabaseName(DatabaseConfig databaseConfig) {
    return Optional.ofNullable(System.getenv("DATABASE_NAME"))
      .orElseGet(() -> databaseConfig != null ? databaseConfig.dbName() : LOCALHOST_DB_NAME);
  }
}
