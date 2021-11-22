package se.hb.monitoringservice.exception;

public class ServiceException extends RuntimeException {

  public ServiceException(String message) {
    super(message);
  }
}
