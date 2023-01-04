package se.hb.monitoringservice.handler;

import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.client.HttpResponse;
import io.vertx.rxjava3.ext.web.client.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.hb.monitoringservice.entity.Service;
import se.hb.monitoringservice.model.ServiceResponse;
import se.hb.monitoringservice.repository.ServiceRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PollHandler {

  public static final List<String> STATUS_OK_MESSAGES = List.of("OK", "Created", "No Content", "Accepted");
  public static final String STATUS_OK = "OK";
  public static final String STATUS_FAIL = "FAIL";

  private final WebClient webClient;
  private final ServiceRepository serviceRepository;

  public void pollServices() {
    serviceRepository.findAll()
      .map(this::mapToEntity)
      .subscribe(service -> webClient.getAbs(service.getUrl())
          .rxSend()
          .flatMap(this::mapStatus)
          .subscribe(newStatus -> checkStatusAndUpdate(service, newStatus)),
        error -> log.error("Error while checking url status: {}", error.getMessage()));
  }

  private void checkStatusAndUpdate(Service service, String newStatus) {
    if (hasDifferentStatus(service.getStatus(), newStatus)) {
      updateServiceWithNewStatus(service, newStatus);
    }
  }

  private void updateServiceWithNewStatus(Service service, String newStatus) {
    log.info("Updating service '{}' status from '{}' to '{}'",
      service.getName(), service.getStatus(), newStatus);
    service.setStatus(newStatus);
    serviceRepository.update(service).subscribe();
  }

  private boolean hasDifferentStatus(String currentStatus, String newStatus) {
    return !currentStatus.equals(newStatus);
  }

  private Single<String> mapStatus(HttpResponse<Buffer> response) {
    return STATUS_OK_MESSAGES.contains(response.statusMessage()) ?
      Single.just(STATUS_OK) : Single.just(STATUS_FAIL);
  }

  private Service mapToEntity(ServiceResponse service) {
    return Service.builder()
      .id(service.getId())
      .name(service.getName())
      .url(service.getUrl())
      .created(service.getCreated())
      .status(service.getStatus())
      .build();
  }
}
