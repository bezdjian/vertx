package se.hb.vertxstockbroker.api.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import se.hb.vertxstockbroker.api.assets.model.Asset;
import se.hb.vertxstockbroker.repository.ServiceRepository;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class AssetsHandlerTest {

  @InjectMocks
  private GetAssetsHandler assetsHandler;
  @Mock
  private RoutingContext ctx;
  @Mock
  private ServiceRepository serviceRepository;

  @BeforeEach
  void setUp() {
    initMocks(this);
  }

  @Test
  void handle() {
    //Given
    final JsonArray expectedJsonResponse = new JsonArray();
    final List<Asset> assetList = List.of(new Asset(1, "AA"),
      new Asset(2, "BB"));
    assetList.forEach(expectedJsonResponse::add);

    when(ctx.normalizedPath()).thenReturn("/api/assets");
    HttpServerResponse mockHttpServerResponse = mock(HttpServerResponse.class);
    when(ctx.response()).thenReturn(mockHttpServerResponse);
    when(ctx.response().setStatusCode(anyInt())).thenReturn(mockHttpServerResponse);
    //when(serviceRepository.getAssets()).thenReturn(assetList);

    //When
    assetsHandler.handle(ctx);

    //Verify
    verify(ctx.response()).end(expectedJsonResponse.encode());
  }
}
