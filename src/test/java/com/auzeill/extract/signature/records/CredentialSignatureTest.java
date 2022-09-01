package com.auzeill.extract.signature.records;

import com.auzeill.extract.signature.json.JsonUtils;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialSignatureTest {

  @Test
  void form() {
    MethodSignature signature = new MethodSignature("com.google", "Foo", List.of("int", "boolean"));
    CredentialSignature credentialSignature = CredentialSignature.form(signature, List.of(2, 3));
    assertThat(JsonUtils.toOneLineJson(credentialSignature)).isEqualTo("""
      {"cls":"com.google","name":"Foo","args":["int","boolean"],"indexes":[2,3]}""");
  }

}
