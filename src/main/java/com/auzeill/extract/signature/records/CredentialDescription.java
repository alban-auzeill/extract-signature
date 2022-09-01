package com.auzeill.extract.signature.records;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CredentialDescription(
  String groupId,
  String artifactId,
  String pkg,
  String classType,
  String classSimpleName,
  String className,
  String methodType,
  String returnType,
  String methodName,
  String signature,
  List<String> arguments,
  List<Integer> argumentIndexes) {

  public static List<CredentialDescription> listFrom(JsonArray array) {
    List<CredentialDescription> result = new ArrayList<>();
    for (JsonElement methodDesc : array) {
      result.add(from(methodDesc.getAsJsonArray()));
    }
    return result;
  }

  public static CredentialDescription from(JsonArray descArray) {
    String groupId = descArray.get(0).getAsString();
    String artifactId = descArray.get(1).getAsString();
    String pkg = descArray.get(2).getAsString();
    String classType = descArray.get(3).getAsString();
    String classSimpleName = descArray.get(4).getAsString();
    String className = pkg.isEmpty() ? classSimpleName : (pkg + "." + classSimpleName);
    String methodType = descArray.get(5).getAsString();
    String returnType = descArray.get(6).getAsString();
    String signature = erasure(descArray.get(7).getAsString());
    int openParenthesis = signature.indexOf('(');
    int closeParenthesis = signature.indexOf(')');
    if (openParenthesis == -1 || openParenthesis != signature.lastIndexOf('(') ||
      closeParenthesis == -1 || closeParenthesis != signature.lastIndexOf(')')) {
      throw new IllegalArgumentException("Invalid parentheses in: " + signature);
    }
    String methodName = signature.substring(0, openParenthesis);
    if (!methodName.matches("\\w+")) {
      throw new IllegalArgumentException("Invalid method name in '" + methodName + "' from: " + className + "#" + signature);
    }
    List<String> arguments = Arrays.stream(signature.substring(openParenthesis + 1, closeParenthesis).trim().split(" *+, *+"))
      .map(arg -> arg.indexOf(' ') != -1 ? arg.substring(0, arg.indexOf(' ')) : arg)
      .toList();
    List<Integer> argumentIndexes = Arrays.stream(descArray.get(8).getAsString().split(" *+, *+"))
      .map(Integer::parseInt).toList();
    for (int argumentIndex : argumentIndexes) {
      if (argumentIndex > arguments.size()) {
        throw new IllegalArgumentException("Invalid index " + argumentIndex + " for: " + className + "#" + signature);
      }
    }
    return new CredentialDescription(groupId, artifactId, pkg, classType, classSimpleName,
      className, methodType, returnType, methodName, signature, arguments, argumentIndexes);
  }

  private static String erasure(String signature) {
    var sb = new StringBuilder();
    int depth = 0;
    char[] chars = signature.toCharArray();
    for (char ch : chars) {
      switch (ch) {
        case '<' -> depth++;
        case '>' -> depth--;
        default -> {
          if (depth == 0) {
            sb.append(ch);
          }
        }
      }
    }
    return sb.toString();
  }

}
