package se.hb.udemy.vertxstarter;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonObjectTest {

  @Test
  void shouldMapJsonObject() {
    //Given
    String expectedJsonObject = "{\"id\":1,\"name\":\"Vertx\"}";

    JsonObject jsonObject = new JsonObject();
    jsonObject.put("id", 1);
    jsonObject.put("name", "Vertx");

    //When
    String jsonEncode = jsonObject.encode();

    //Then
    assertEquals(expectedJsonObject, jsonEncode);

    //Decode
    JsonObject jsonDecode = new JsonObject(jsonEncode);
    assertEquals(jsonObject, jsonDecode);
  }

  @Test
  void shouldMapJsonArray() {
    //Given
    String expectedJsonArray = "[{\"id\":1,\"name\":\"Vertx\"}]";

    JsonObject jsonObject = new JsonObject();
    jsonObject.put("id", 1);
    jsonObject.put("name", "Vertx");
    JsonArray array = new JsonArray();
    array.add(jsonObject);

    //When
    String encodeJson = array.encode();

    //Then
    assertEquals(expectedJsonArray, encodeJson);
  }

  @Test
  void shouldMapJavaObject() {
    //Given
    String expectedJson = "{\"id\":1,\"name\":\"Person\"}";
    Person person = new Person(1, "Person");

    //When
    JsonObject personJson = JsonObject.mapFrom(person);
    //Then
    assertEquals(expectedJson, personJson.encode());

    //MapTo
    Person person1 = personJson.mapTo(Person.class);
    assertEquals(person1.getName(), "Person");
  }
}
