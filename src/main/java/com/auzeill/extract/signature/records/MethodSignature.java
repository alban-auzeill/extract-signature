package com.auzeill.extract.signature.records;

import java.util.List;

public record MethodSignature(
  String cls,
  String name,
  List<String> args) {
}
