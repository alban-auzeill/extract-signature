package com.auzeill.extract.signature.json;

import com.auzeill.extract.signature.records.MethodSignature;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class JsonUtilsTest {

  @Test
  void to_one_line_json() {
    MethodSignature signature = new MethodSignature("com.google", "Foo", List.of("int", "boolean"));
    assertThat(JsonUtils.toOneLineJson(signature)).isEqualTo("""
      {"cls":"com.google","name":"Foo","args":["int","boolean"]}""");
  }

  @Test
  void to_partially_indented_json_array() {
    List<MethodSignature> signatures = List.of(
      new MethodSignature("com.google", "Foo", List.of("int", "boolean")),
      new MethodSignature("org.apache", "Bar", List.of("long"))
    );
    assertThat(JsonUtils.toPartiallyIndentedJsonArray(signatures)).isEqualTo("""
      [
        {"cls":"com.google","name":"Foo","args":["int","boolean"]},
        {"cls":"org.apache","name":"Bar","args":["long"]}
      ]
      """);
  }

  @Test
  void write_partially_indented_json_array_to_file(@TempDir Path tempDir) throws IOException {
    List<MethodSignature> signatures = List.of(new MethodSignature("com.google", "Foo", List.of("int", "boolean")));
    Path destFile = tempDir.resolve("out.json");
    JsonUtils.writePartiallyIndentedJsonArrayToFile(destFile, signatures);
    assertThat(destFile).hasContent("""
      [
        {"cls":"com.google","name":"Foo","args":["int","boolean"]}
      ]
      """);
  }

  @Test
  void from_json_array(@TempDir Path tempDir) throws IOException {
    Path inputFile = tempDir.resolve("input.json");
    Files.writeString(inputFile, "[1,2,3]", UTF_8);
    JsonArray array = JsonUtils.fromJsonArray(inputFile);
    assertThat(array).hasSize(3);
  }

}
