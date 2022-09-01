package com.auzeill.extract.signature;

import com.auzeill.extract.signature.json.JsonUtils;
import com.auzeill.extract.signature.records.CredentialDescription;
import com.auzeill.extract.signature.records.CredentialSignature;
import com.auzeill.extract.signature.records.MethodSignature;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.auzeill.extract.signature.json.JsonUtils.toOneLineJson;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static final Path JAR_DIRECTORY = Path.of("maven-local-repository");

  public static final Path INPUT_DIRECTORY = Path.of("data");
  public static final Path RAW_JSON_INPUT_FILE = INPUT_DIRECTORY.resolve("credentials-methods.json");

  public static final Path OUTPUT_DIRECTORY = Path.of("target");
  public static final Path ALL_METHOD_SIGNATURES_FILE = OUTPUT_DIRECTORY.resolve("all-jars-methods.json");
  public static final Path FORMATTED_RAW_JSON_FILE = OUTPUT_DIRECTORY.resolve("credentials-methods-formatted-input.json");
  public static final Path JSON_OUTPUT_FILE = OUTPUT_DIRECTORY.resolve("credentials-methods.json");

  public static void main(String[] args) throws Exception {
    Map<String, List<MethodSignature>> methodsByName = ExtractMethodSignatureFromJars.extractSignaturesFromDirectory(JAR_DIRECTORY);
    writeMethodSignatures(ALL_METHOD_SIGNATURES_FILE, methodsByName);

    JsonArray rawMethodArray = JsonUtils.fromJsonArray(RAW_JSON_INPUT_FILE);
    JsonUtils.writePartiallyIndentedJsonArrayToFile(FORMATTED_RAW_JSON_FILE, rawMethodArray);

    List<CredentialDescription> credentialDescriptions = CredentialDescription.listFrom(rawMethodArray);
    ExtractMethodSignatureFromJars.loadJDKClasses(methodsByName, credentialDescriptions);
    List<CredentialSignature> credentialSignatureMethods = validateCredentialMethods(credentialDescriptions, methodsByName);
    JsonUtils.writePartiallyIndentedJsonArrayToFile(JSON_OUTPUT_FILE, credentialSignatureMethods);

    LOG.info("Converted {} out of {}", credentialSignatureMethods.size(), credentialDescriptions.size());
  }

  private static void writeMethodSignatures(Path path, Map<String, List<MethodSignature>> methodsByName) throws IOException {
    List<String> sortedList = methodsByName.values().stream().flatMap(Collection::stream)
      .map(JsonUtils::toOneLineJson)
      .sorted()
      .toList();
    JsonUtils.writePartiallyIndentedJsonArrayToFile(path, sortedList);
  }

  private static List<CredentialSignature> validateCredentialMethods(
    List<CredentialDescription> credentialDescriptions,
    Map<String, List<MethodSignature>> methodsByName) {
    List<CredentialSignature> credentialSignatureMethods = new ArrayList<>();
    List<String> missingMethodByName = new ArrayList<>();
    List<String> missingMethodByClass = new ArrayList<>();
    List<String> missingMethodByArguments = new ArrayList<>();
    List<String> ambiguousMethodByArguments = new ArrayList<>();
    for (CredentialDescription credentialDescription : credentialDescriptions) {
      List<MethodSignature> methodEntries = methodsByName.get(credentialDescription.methodName());
      if (methodEntries == null) {
        missingMethodByName.add(toOneLineJson(credentialDescription));
      } else {
        List<MethodSignature> subEntries = methodEntries.stream()
          .filter(method -> classNamesMatch(method.cls(), credentialDescription.className()))
          .toList();
        if (subEntries.isEmpty()) {
          missingMethodByClass.add(toOneLineJson(credentialDescription));
        } else {
          subEntries = subEntries.stream()
            .filter(method -> argumentsMatches(credentialDescription, method))
            .toList();
          if (subEntries.isEmpty()) {
            missingMethodByArguments.add(toOneLineJson(credentialDescription));
          } else if (subEntries.size() > 1) {
            ambiguousMethodByArguments.add(credentialDescription +
              subEntries.stream().map(JsonUtils::toOneLineJson).collect(Collectors.joining("\n  ", "\n  ", "\n")));
          } else {
            credentialSignatureMethods.add(CredentialSignature.form(subEntries.get(0), credentialDescription.argumentIndexes()));
          }
        }
      }
    }
    logWarningIfNotEmtpy(missingMethodByName, "missing methodName");
    logWarningIfNotEmtpy(missingMethodByClass, "missing className");
    logWarningIfNotEmtpy(missingMethodByArguments, "missing arguments");
    logWarningIfNotEmtpy(ambiguousMethodByArguments, "ambiguous arguments");

    credentialSignatureMethods.sort(Comparator.comparing(JsonUtils::toOneLineJson));
    return credentialSignatureMethods;
  }

  private static void logWarningIfNotEmtpy(List<String> list, String listName) {
    if (!list.isEmpty() && LOG.isWarnEnabled()) {
      LOG.warn("### {} {} ###{}{}", list.size(), listName,
        System.lineSeparator(), String.join("\n", list));
    }
  }

  private static boolean classNamesMatch(String first, String second) {
    return first.replace('$', '.').equals(second.replace('$', '.'));
  }

  private static boolean argumentsMatches(CredentialDescription credentialDescription, MethodSignature method) {
    int size = method.args().size();
    if (credentialDescription.arguments().size() != size) {
      return false;
    }
    for (int i = 0; i < size; i++) {
      String credentialArg = credentialDescription.arguments().get(i).replace('$', '.');
      String candidateArg = method.args().get(i).replace('$', '.');
      if (!candidateArg.endsWith(credentialArg)) {
        return false;
      }
    }
    return true;
  }

}
