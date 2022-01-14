package se.hb.vertxstockbroker.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbConfig {

  String url;
  String host;
  int post;
  String name;
  String user;
  String password;

  @Override
  public String toString() {
    return "{" +
      "url='" + host + '\'' +
      ", host='" + host + '\'' +
      ", post=" + post +
      ", name='" + name + '\'' +
      ", user='" + user + '\'' +
      ", password='*****'" +
      '}';
  }
}
