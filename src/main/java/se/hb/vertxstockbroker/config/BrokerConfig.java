package se.hb.vertxstockbroker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
@ToString
public class BrokerConfig {

  int serverPort;
  String version;
  String applicationName;
  DbConfig dbConfig;

  public static BrokerConfig from(JsonObject config) {
    return BrokerConfig.builder()
      .serverPort(config.getJsonObject("server").getInteger("port"))
      .version(config.getString("version"))
      .version(config.getJsonObject("application").getString("name"))
      .dbConfig(mapDbConfig(config))
      .build();
  }

  private static DbConfig mapDbConfig(JsonObject config) {
    return DbConfig.builder()
      .url(config.getJsonObject("db").getString("url"))
      .host(config.getJsonObject("db").getString("host"))
      .name(config.getJsonObject("db").getString("name"))
      .user(config.getJsonObject("db").getString("user"))
      .password(config.getJsonObject("db").getString("password"))
      .post(config.getJsonObject("db").getInteger("port"))
      .build();
  }
}
