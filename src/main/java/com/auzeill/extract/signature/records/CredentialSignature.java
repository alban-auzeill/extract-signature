package com.auzeill.extract.signature.records;

import java.util.List;

public record CredentialSignature(
  String cls,
  String name,
  List<String> args,
  List<Integer> indexes) {

  public static CredentialSignature form(MethodSignature methodSignature, List<Integer> argumentIndexes) {
    return new CredentialSignature(methodSignature.cls(), methodSignature.name(), methodSignature.args(), argumentIndexes);
  }
}
