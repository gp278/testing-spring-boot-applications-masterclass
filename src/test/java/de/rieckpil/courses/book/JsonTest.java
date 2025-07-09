package de.rieckpil.courses.book;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonTest {

  @Test
  void testWithJSONAssert() throws JSONException {
    String result = "{\"name\": \"duke\", \"age\":\"42\", \"hobbies\": [\"soccer\", \"java\"]}";
    JSONAssert.assertEquals("{'name': 'duke'}", result, JSONCompareMode.LENIENT);
    JSONAssert.assertEquals("{'hobbies': ['java', 'soccer']}", result, JSONCompareMode.LENIENT);
  }

  @Test
  void testWithJsonPath() {
    String result =
        "{\"age\":\"42\", \"name\": \"duke\", \"tags\":[\"java\", \"jdk\"], \"orders\": [42, 42, 16]}";
    Assertions.assertEquals(2, JsonPath.parse(result).read("$.tags.length()", Long.class));
    Assertions.assertEquals("duke", JsonPath.parse(result).read("$.name", String.class));
    Assertions.assertEquals(100, JsonPath.parse(result).read("$.orders.sum()", Long.class));
  }
}
