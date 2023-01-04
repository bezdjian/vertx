package se.hb.monitoringservice.handler;


import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.Json;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.client.HttpResponse;
import io.vertx.rxjava3.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.monitoringservice.entity.Service;
import se.hb.monitoringservice.repository.ServiceRepository;
import se.hb.monitoringservice.util.ResponseUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ServiceHandler {

  public static final List<String> STATUS_OK_MESSAGES = List.of("OK", "Created", "No Content", "Accepted");
  public static final String STATUS_OK = "OK";
  public static final String STATUS_FAIL = "FAIL";

  private final WebClient webClient;
  private final ServiceRepository serviceRepository;

  public void findAll(RoutingContext rc) {
    serviceRepository.findAll()
      .toList()
      .doOnError(error -> ResponseUtil.sendErrorResponse(rc, 500, error))
      .subscribe(services -> ResponseUtil.sendSuccessResponse(rc, 200, services),
        error -> log.error("Error while fetching all services: {}", error.getMessage()));
  }

  public void save(RoutingContext rc) {
    Service service = mapRequest(rc);
    webClient.getAbs(service.getUrl())
      .rxSend()
      .flatMap(this::mapStatus)
      .map(status -> mapServiceStatus(service, status))
      .onErrorReturnItem(service)
      .subscribe(updatedService -> saveService(rc, updatedService),
        error -> log.error("Error while saving service {}: {}", service.getName(), error.getMessage()));
  }

  public void findById(RoutingContext rc) {
    String id = rc.pathParam("id");
    serviceRepository.findById(id)
      .subscribe(service -> ResponseUtil.sendSuccessResponse(rc, 200, service),
        error -> ResponseUtil.sendErrorResponse(rc, 404, error));
  }

  public void delete(RoutingContext rc) {
    String id = rc.pathParam("id");
    serviceRepository.delete(id)
      .doOnError(error -> ResponseUtil.sendErrorResponse(rc, 500, error))
      .subscribe(() -> ResponseUtil.sendSuccessResponse(rc, 200),
        error -> log.error("Error while deleting service with ID {}: {}", id, error.getMessage()));
  }

  private Service mapServiceStatus(Service service, String status) {
    service.setStatus(status);
    return service;
  }

  private void saveService(RoutingContext rc, Service service) {
    log.info("Received {} status from {}", service.getStatus(), service.getUrl());
    serviceRepository.save(service)
      .subscribe(() -> ResponseUtil.sendSuccessResponse(rc, 201),
        error -> log.error("Error while saving service {}: {}", service.getName(), error.getMessage()));
  }

  private Single<String> mapStatus(HttpResponse<Buffer> response) {
    return STATUS_OK_MESSAGES.contains(response.statusMessage()) ?
      Single.just(STATUS_OK) : Single.just(STATUS_FAIL);
  }

  private Service mapRequest(RoutingContext routingContext) {
    String body = routingContext.getBodyAsString();
    return Json.decodeValue(body, Service.class);
  }
}
