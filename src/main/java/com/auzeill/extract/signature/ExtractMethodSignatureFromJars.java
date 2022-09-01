package com.auzeill.extract.signature;

import com.auzeill.extract.signature.records.CredentialDescription;
import com.auzeill.extract.signature.records.MethodSignature;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExtractMethodSignatureFromJars {

  private static final Logger LOG = LoggerFactory.getLogger(ExtractMethodSignatureFromJars.class);

  private ExtractMethodSignatureFromJars() {
    // utility class
  }

  public static Map<String, List<MethodSignature>> extractSignaturesFromDirectory(Path baseDirectory) {
    Map<String, List<MethodSignature>> methodsByName = new HashMap<>();
    forAllJars(baseDirectory, jarPath -> forAllClassFile(jarPath,
      (classPath, inputStream) -> addClassNode(methodsByName, parseClassFile(inputStream))));
    return methodsByName;
  }

  public static void loadJDKClasses(Map<String, List<MethodSignature>> methodsByName, List<CredentialDescription> credentialDescriptions) {
    Set<String> jdkClasses = new HashSet<>(
      credentialDescriptions.stream()
        .filter(method -> method.groupId().equals("_"))
        .map(CredentialDescription::className)
        .toList());
    for (String jdkClass : jdkClasses) {
      Class<?> loadedClass = loadClass(jdkClass);
      if (loadedClass != null) {
        String classPath = loadedClass.getName().replace('.', '/') + ".class";
        try (InputStream input = ClassLoader.getSystemResourceAsStream(classPath)) {
          addClassNode(methodsByName, parseClassFile(input));
        } catch (IOException ex) {
          onException(ex);
        }
      } else {
        LOG.warn("ClassNotFoundException: {}", jdkClass);
      }
    }
  }

  private static Class<?> loadClass(String className) {
    String nameCandidate = className;
    while (nameCandidate != null) {
      try {
        return Class.forName(nameCandidate);
      } catch (ClassNotFoundException ex) {
        // ignore
      }
      int sep = nameCandidate.lastIndexOf('.');
      if (sep != -1) {
        nameCandidate = nameCandidate.substring(0, sep) + "$" + nameCandidate.substring(sep + 1);
      } else {
        nameCandidate = null;
      }
    }
    return null;
  }

  private static void forAllJars(Path base, Consumer<Path> consumer) {
    try (Stream<Path> walker = Files.walk(base)) {
      walker.filter(path -> path.getFileName().toString().endsWith(".jar")).forEach(consumer);
    } catch (IOException ex) {
      onException(ex);
    }
  }

  private static void forAllClassFile(Path jarPath, BiConsumer<String, InputStream> consumer) {
    try (InputStream inputStream = Files.newInputStream(jarPath);
      ZipInputStream zip = new ZipInputStream(inputStream)) {
      for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
          consumer.accept(entry.getName(), zip);
        }
      }
    } catch (IOException ex) {
      onException(ex);
    }
  }

  private static void addClassNode(Map<String, List<MethodSignature>> methodsByName, ClassNode classNode) {
    String className = classNode.name.replace('/', '.');
    for (MethodNode method : classNode.methods) {
      if ((method.access & Opcodes.ACC_SYNTHETIC) != 0) {
        continue;
      }
      List<String> arguments = Arrays.stream(Type.getArgumentTypes(method.desc)).map(Type::getClassName).toList();
      boolean constructor = method.name.equals("<init>");
      String methodName = constructor ? classSimpleName(className) : method.name;
      MethodSignature methodSignature = new MethodSignature(className, methodName, arguments);
      List<MethodSignature> methodEntries = methodsByName.computeIfAbsent(methodSignature.name(), key -> new ArrayList<>());
      if (!methodEntries.contains(methodSignature)) {
        methodEntries.add(methodSignature);
      }
    }
  }

  private static String classSimpleName(String fullName) {
    int sep = Math.max(Math.max(fullName.lastIndexOf('.'), fullName.lastIndexOf('$')), fullName.lastIndexOf('/'));
    if (sep == -1) {
      return fullName;
    }
    return fullName.substring(sep + 1);
  }

  private static ClassNode parseClassFile(InputStream inputStream) {
    ClassNode classNode = new ClassNode();
    try {
      ClassReader reader = new ClassReader(inputStream);
      reader.accept(classNode, ClassReader.EXPAND_FRAMES);
    } catch (IOException ex) {
      onException(ex);
    }
    return classNode;
  }

  private static void onException(Throwable e) {
    LOG.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    System.exit(1);
  }

}
