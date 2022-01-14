package se.hb.vertxstockbroker.config;

import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConfigLoader {

  public static final String SERVER_PORT = "server.port";
  private static final List<String> ENVIRONMENT_VARIABLES = List.of(SERVER_PORT);

  public static Single<BrokerConfig> load(Vertx vertx) {
    ConfigStoreOptions envStore = getEnvStore();

    var configFileStore = getConfigFileStore();
    log.info("configFileStore " + configFileStore.getConfig().encode());

    var options = new ConfigRetrieverOptions()
      .addStore(envStore)
      .addStore(configFileStore);

    return ConfigRetriever.create(vertx, options)
      .getConfig()
      .map(BrokerConfig::from);
  }

  private static ConfigStoreOptions getConfigFileStore() {
    return new ConfigStoreOptions()
      .setType("file")
      // Defaults to JSON
      .setFormat("yaml")
      .setConfig(new JsonObject()
        .put("path", "application.yaml"));
  }

  private static ConfigStoreOptions getEnvStore() {
    var exposedKeys = new JsonArray();
    ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);

    return new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));
  }
}
