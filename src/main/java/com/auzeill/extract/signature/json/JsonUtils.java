package com.auzeill.extract.signature.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class JsonUtils {

  public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

  private JsonUtils() {
    // utility class
  }

  public static String toOneLineJson(Object data) {
    return GSON.toJson(data);
  }

  public static String toPartiallyIndentedJsonArray(Iterable<?> collection) {
    return StreamSupport.stream(collection.spliterator(), false)
      .map(JsonUtils::toOneLineJson)
      .collect(Collectors.joining(",\n  ", "[\n  ", "\n]\n"));
  }

  public static void writePartiallyIndentedJsonArrayToFile(Path path, Iterable<?> collection) throws IOException {
    Files.writeString(path, toPartiallyIndentedJsonArray(collection), UTF_8);
  }

  public static JsonArray fromJsonArray(Path path) throws IOException {
    String jsonText = Files.readString(path, UTF_8);
    return new Gson().fromJson(jsonText, JsonArray.class);
  }

}
