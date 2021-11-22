package se.hb.monitoringservice.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceModel {

  long id;
  String name;
  String url;
  String status;
  String created;
}
