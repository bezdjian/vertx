package se.hb.monitoringservice;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.client.WebClient;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.CorsHandler;
import io.vertx.rxjava3.mysqlclient.MySQLPool;
import se.hb.monitoringservice.db.Database;
import se.hb.monitoringservice.handler.PollHandler;
import se.hb.monitoringservice.handler.ServiceHandler;
import se.hb.monitoringservice.repository.ServiceRepository;

public class Application extends AbstractVerticle {

  public static final String BASE_SERVICE_URL = "/api/services";
  public static final String SERVICE_ID_URL = "/api/services/:id";

  private static final int TEN_SECONDS = 10000;

  @Override
  public Completable rxStart() {
    MySQLPool connection = Database.createConnection(vertx);
    WebClient webClient = WebClient.create(vertx);

    ServiceRepository serviceRepository = new ServiceRepository(connection);
    ServiceHandler serviceHandler = new ServiceHandler(webClient, serviceRepository);
    PollHandler pollHandler = new PollHandler(webClient, serviceRepository);

    Router routes = routes(serviceHandler);

    vertx.setPeriodic(TEN_SECONDS, timerId -> pollHandler.pollServices());

    return vertx.createHttpServer()
      .requestHandler(routes)
      .rxListen(8881)
      .ignoreElement();
  }

  private Router routes(ServiceHandler serviceHandler) {
    Router router = Router.router(vertx);

    router.route().handler(createCORSHandler());

    router.get(BASE_SERVICE_URL)
      .handler(serviceHandler::findAll);

    router.get(SERVICE_ID_URL)
      .handler(serviceHandler::findById);

    router.post(BASE_SERVICE_URL)
      .handler(BodyHandler.create())
      .handler(serviceHandler::save);

    router.delete(SERVICE_ID_URL)
      .handler(serviceHandler::delete);

    return router;
  }

  private CorsHandler createCORSHandler() {
    return CorsHandler.create("((http://)|(https://))localhost\\:\\d+")
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.DELETE)
      .allowCredentials(true)
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Content-Type");
  }
}
