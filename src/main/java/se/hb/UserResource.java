package se.hb;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import se.hb.entity.Users;
import se.hb.model.UserRequest;
import se.hb.model.UserResponse;
import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Slf4j
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

  @GET
  public Uni<List<Users>> getAllUsers() {
    log.info("getAllUsers...");
    return Users.listAll(Sort.by("id").descending());
  }

  @GET
  @Path("/{id}")
  public RestResponse<UserResponse> getById(@PathParam("id") Long id) {
    //RestResponse.notFound()
    log.info("GetById {}", id);
    return Users.<Users>findById(id)
        .await()
        .asOptional()
        .indefinitely()
        .map(this::returnUser)
        .orElseGet(() -> returnNotFound(id));
  }

  @POST
  public Uni<Response> save(UserRequest request) {
    log.info("Creating user {}", request.toString());
    var user = Users.from(request);
    return Panache.<Users>withTransaction(user::persist)
        .onFailure()
        .call(this::returnError)
        .onItem()
        .transform(this::returnCreated);
  }

  private Uni<Response> returnError(Throwable err) {
    log.error("Error while saving user {}", err.getMessage());
    return Uni.createFrom().item(Response.serverError().build());
  }

  private Response returnCreated(Users insertedUser) {
    return Response
        .created(URI.create("/users/" + insertedUser.id))
        .build();
  }

  private RestResponse<UserResponse> returnUser(Users user) {
    log.info("Getting user {}", user.toString());
    var response = UserResponse.builder()
        .body(user)
        .build();
    return RestResponse.ok(response);
  }

  private RestResponse<UserResponse> returnNotFound(Long id) {
    var response = UserResponse.builder()
        .message("User with id " + id + " not found")
        .build();
    return RestResponse.status(Response.Status.NOT_FOUND, response);
  }
}
