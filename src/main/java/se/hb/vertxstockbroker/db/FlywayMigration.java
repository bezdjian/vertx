package se.hb.vertxstockbroker.db;

import io.reactivex.rxjava3.core.Maybe;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import se.hb.vertxstockbroker.config.BrokerConfig;
import se.hb.vertxstockbroker.config.DbConfig;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class FlywayMigration {

  public static Maybe<Object> migrate(Vertx vertx, BrokerConfig config) {
    return vertx.rxExecuteBlocking(handler -> {
      //Flyway migration is blocking => uses JDBC
      log.info("*** Executing database migration...");
      execute(config);
      handler.complete();
    });
  }

  private static void execute(BrokerConfig config) {
    var dbConfig = config.getDbConfig();
    log.info("*** DB config loaded: {}", dbConfig.toString());

    final var jdbcUrl = String.format("%s://%s:%d/%s",
      dbConfig.getUrl(), dbConfig.getHost(), dbConfig.getPost(), dbConfig.getName());
    var flyway = configureFlyway(dbConfig, jdbcUrl);

    logFlywayInfo(flyway.info());
    flyway.migrate();
  }

  private static Flyway configureFlyway(DbConfig dbConfig, String jdbcUrl) {
    log.info("*** Configuring Flyway with jdbc url: {}", jdbcUrl);
    return Flyway.configure()
      .failOnMissingLocations(true)
      .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();
  }

  private static void logFlywayInfo(MigrationInfoService info) {
    Optional.ofNullable(info.current())
      .ifPresentOrElse(current -> log.info("** Current Flyway version: {}, description: {}",
          current.getVersion(), current.getDescription()),
        () -> log.info("** No current Flyway info at the moment."));

    Arrays.stream(info.pending())
      .forEach(migrationInfo ->
        log.info("** Migration info -  Version: {}, Description: {}",
          migrationInfo.getVersion(), migrationInfo.getDescription()));
  }
}
