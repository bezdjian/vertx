package se.hb.monitoringservice.db;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Queries {

  public final String FIND_ALL_SERVICES = "SELECT * FROM service order by id desc";
  public final String FIND_SERVICE_BY_ID = "SELECT * FROM service where id = ?";
  public final String DELETE_SERVICE_BY_ID = "DELETE FROM service where id = ?";
  public final String UPDATE_SERVICE_STATUS_QUERY = "UPDATE service set status = ? where id = ?";
  public final String INSERT_INTO_SERVICE_QUERY = """
     INSERT INTO service (name, url, status)
     VALUES (?, ?, ?)
    """;
}
