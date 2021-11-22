package se.hb.monitoringservice.repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.mysqlclient.MySQLPool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowIterator;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import se.hb.monitoringservice.db.Queries;
import se.hb.monitoringservice.entity.Service;
import se.hb.monitoringservice.exception.ServiceNotFoundException;
import se.hb.monitoringservice.model.ServiceResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class ServiceRepository {

  private final MySQLPool connection;

  public Flowable<ServiceResponse> findAll() {
    return connection.query(Queries.FIND_ALL_SERVICES)
      .rxExecute()
      .map(Iterable::spliterator)
      .flattenAsFlowable(this::getServices);
  }

  public Completable save(Service service) {
    Tuple params = createParameters(service);

    return connection.preparedQuery(Queries.INSERT_INTO_SERVICE_QUERY)
      .rxExecute(params)
      .ignoreElement();
  }

  public Completable update(Service service) {
    return connection.preparedQuery(Queries.UPDATE_SERVICE_STATUS_QUERY)
      .rxExecute(Tuple.of(service.getStatus(), service.getId()))
      .ignoreElement();
  }

  public Single<ServiceResponse> findById(String id) {
    return connection.preparedQuery(Queries.FIND_SERVICE_BY_ID)
      .rxExecute(Tuple.of(id))
      .map(RowSet::iterator)
      .flatMap(iterator -> getService(id, iterator));
  }

  public Completable delete(String id) {
    return connection.preparedQuery(Queries.DELETE_SERVICE_BY_ID)
      .rxExecute(Tuple.of(id))
      .ignoreElement();
  }

  private Single<ServiceResponse> getService(String id, RowIterator<Row> iterator) {
    if (iterator.hasNext()) {
      return Single.just(mapServiceResponse(iterator.next()));
    } else {
      return Single.error(new ServiceNotFoundException(
        String.format("Service with id %s not found", id)
      ));
    }
  }

  private List<ServiceResponse> getServices(Spliterator<Row> rows) {
    return StreamSupport.stream(rows, false)
      .map(this::mapServiceResponse)
      .collect(Collectors.toList());
  }

  private ServiceResponse mapServiceResponse(Row row) {
    return ServiceResponse.builder()
      .id(row.getInteger("id"))
      .name(row.getString("name"))
      .url(row.getString("url"))
      .created(getFormattedCreationTime(row))
      .status(row.getString("status"))
      .build();
  }

  private String getFormattedCreationTime(Row row) {
    return row.getLocalDateTime("created")
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  private Tuple createParameters(Service service) {
    return Tuple.of(service.getName(),
      service.getUrl(),
      service.getStatus());
  }
}
