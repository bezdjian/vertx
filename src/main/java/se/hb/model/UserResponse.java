package se.hb.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
  String message;
  Object body;
}
