package se.hb.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.hb.model.UserRequest;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@Cacheable
@NoArgsConstructor
@AllArgsConstructor
public class Users extends PanacheEntity {

  @Column(unique = true)
  private String name;

  public static Users from(UserRequest request) {
    return new Users(request.getName());
  }
}
