package com.auzeill.extract.signature;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

  @Test
  void full_test() throws Exception {
    List<ILoggingEvent> logs = captureLogs();
    Main.main(new String[0]);
    String actual = Files.readString(Path.of("target", "credentials-methods.json"), UTF_8);
    String expected = Files.readString(Path.of("src", "test", "resources", "expected-credentials-methods.json"), UTF_8);
    assertThat(actual).isEqualTo(expected);
    assertThat(asString(logs)).isEqualTo(
      """
        [WARN] ### 4 missing methodName ###
        {"groupId":"com.h2database","artifactId":"h2","pkg":"org.h2.samples","classType":"Classes","classSimpleName":"CreateScriptFile","className":"org.h2.samples.CreateScriptFile","methodType":"Method","returnType":"static java.io.LineNumberReader","methodName":"openScriptReader","signature":"openScriptReader(java.lang.String fileName, java.lang.String compressionAlgorithm, java.lang.String cipher, java.lang.String password, java.lang.String charset)","arguments":["java.lang.String","java.lang.String","java.lang.String","java.lang.String","java.lang.String"],"argumentIndexes":[4]}
        {"groupId":"com.h2database","artifactId":"h2","pkg":"org.h2.samples","classType":"Classes","classSimpleName":"CreateScriptFile","className":"org.h2.samples.CreateScriptFile","methodType":"Method","returnType":"static java.io.PrintWriter","methodName":"openScriptWriter","signature":"openScriptWriter(java.lang.String fileName, java.lang.String compressionAlgorithm, java.lang.String cipher, java.lang.String password, java.lang.String charset)","arguments":["java.lang.String","java.lang.String","java.lang.String","java.lang.String","java.lang.String"],"argumentIndexes":[4]}
        {"groupId":"org.liquibase","artifactId":"liquibase-core","pkg":"liquibase.extension.testing.testsystem.wrapper","classType":"Classes","classSimpleName":"JdbcDatabaseWrapper","className":"liquibase.extension.testing.testsystem.wrapper.JdbcDatabaseWrapper","methodType":"Constructor","returnType":"JdbcDatabaseWrapper","methodName":"JdbcDatabaseWrapper","signature":"JdbcDatabaseWrapper(String url, String username, String password)","arguments":["String","String","String"],"argumentIndexes":[3]}
        {"groupId":"com.datastax.cassandra","artifactId":"cassandra-driver-core","pkg":"com.datastax.driver.core","classType":"Classes","classSimpleName":"Cluster.Builder","className":"com.datastax.driver.core.Cluster.Builder","methodType":"Method","returnType":"Cluster.Builder","methodName":"withCredentials","signature":"withCredentials(String username, String password)","arguments":["String","String"],"argumentIndexes":[2]}
        [WARN] ### 7 missing className ###
        {"groupId":"com.h2database","artifactId":"h2","pkg":"org.h2.samples","classType":"Classes","classSimpleName":"Compact","className":"org.h2.samples.Compact","methodType":"Method","returnType":"static void","methodName":"compact","signature":"compact(java.lang.String dir, java.lang.String dbName, java.lang.String user, java.lang.String password)","arguments":["java.lang.String","java.lang.String","java.lang.String","java.lang.String"],"argumentIndexes":[4]}
        {"groupId":"com.h2database","artifactId":"h2","pkg":"org.h2.samples","classType":"Classes","classSimpleName":"SQLInjection","className":"org.h2.samples.SQLInjection","methodType":"Method","returnType":"static java.lang.String","methodName":"changePassword","signature":"changePassword(java.sql.Connection conn, java.lang.String userName, java.lang.String password)","arguments":["java.sql.Connection","java.lang.String","java.lang.String"],"argumentIndexes":[3]}
        {"groupId":"com.h2database","artifactId":"h2","pkg":"org.h2.samples","classType":"Classes","classSimpleName":"SQLInjection","className":"org.h2.samples.SQLInjection","methodType":"Method","returnType":"static java.sql.ResultSet","methodName":"getUser","signature":"getUser(java.sql.Connection conn, java.lang.String userName, java.lang.String password)","arguments":["java.sql.Connection","java.lang.String","java.lang.String"],"argumentIndexes":[3]}
        {"groupId":"org.liquibase","artifactId":"liquibase-core","pkg":"liquibase.extension.testing.testsystem","classType":"Classes","classSimpleName":"DatabaseTestSystem","className":"liquibase.extension.testing.testsystem.DatabaseTestSystem","methodType":"Method","returnType":"Connection","methodName":"getConnection","signature":"getConnection(String username, String password)","arguments":["String","String"],"argumentIndexes":[2]}
        {"groupId":"org.liquibase","artifactId":"liquibase-core","pkg":"liquibase.extension.testing.testsystem","classType":"Classes","classSimpleName":"DatabaseTestSystem","className":"liquibase.extension.testing.testsystem.DatabaseTestSystem","methodType":"Method","returnType":"protected Connection","methodName":"getConnection","signature":"getConnection(String url, String username, String password)","arguments":["String","String","String"],"argumentIndexes":[3]}
        {"groupId":"com.datastax.cassandra","artifactId":"cassandra-driver-core","pkg":"com.datastax.driver.core","classType":"Classes","classSimpleName":"PlainTextAuthProvider","className":"com.datastax.driver.core.PlainTextAuthProvider","methodType":"Constructor","returnType":"PlainTextAuthProvider","methodName":"PlainTextAuthProvider","signature":"PlainTextAuthProvider(String username, String password)","arguments":["String","String"],"argumentIndexes":[2]}
        {"groupId":"com.datastax.cassandra","artifactId":"cassandra-driver-core","pkg":"com.datastax.driver.core","classType":"Classes","classSimpleName":"PlainTextAuthProvider","className":"com.datastax.driver.core.PlainTextAuthProvider","methodType":"Method","returnType":"void","methodName":"setPassword","signature":"setPassword(String password)","arguments":["String"],"argumentIndexes":[1]}
        [WARN] ### 3 missing arguments ###
        {"groupId":"software.amazon.awssdk","artifactId":"auth","pkg":"software.amazon.awssdk.auth.signer.internal","classType":"Classes","classSimpleName":"AbstractAws4Signer","className":"software.amazon.awssdk.auth.signer.internal.AbstractAws4Signer","methodType":"Method","returnType":"protected abstract void","methodName":"processRequestPayload","signature":"processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, T signerParams)","arguments":["SdkHttpFullRequest.Builder","byte[]","byte[]","Aws4SignerRequestParams","T"],"argumentIndexes":[3]}
        {"groupId":"software.amazon.awssdk","artifactId":"auth","pkg":"software.amazon.awssdk.auth.signer.internal","classType":"Classes","classSimpleName":"AbstractAws4Signer","className":"software.amazon.awssdk.auth.signer.internal.AbstractAws4Signer","methodType":"Method","returnType":"protected abstract void","methodName":"processRequestPayload","signature":"processRequestPayload(SdkHttpFullRequest.Builder mutableRequest, byte[] signature, byte[] signingKey, Aws4SignerRequestParams signerRequestParams, T signerParams, SdkChecksum sdkChecksum)","arguments":["SdkHttpFullRequest.Builder","byte[]","byte[]","Aws4SignerRequestParams","T","SdkChecksum"],"argumentIndexes":[3]}
        {"groupId":"org.postgresql","artifactId":"postgresql","pkg":"org.postgresql.gss","classType":"Classes","classSimpleName":"MakeGSS","className":"org.postgresql.gss.MakeGSS","methodType":"Method","returnType":"static void","methodName":"authenticate","signature":"authenticate(boolean encrypted, PGStream pgStream, String host, String user, char[] password, @Nullable String jaasApplicationName, @Nullable String kerberosServerName, boolean useSpnego, boolean jaasLogin, boolean logServerErrorDetail)","arguments":["boolean","PGStream","String","String","char[]","@Nullable","@Nullable","boolean","boolean","boolean"],"argumentIndexes":[5]}
        [INFO] Converted 605 out of 619
        """);
  }

  private static String asString(List<ILoggingEvent> logs) {
    return logs.stream()
      .map(event -> "[" + event.getLevel() + "] " +
        event.getFormattedMessage().replace(System.lineSeparator(), "\n"))
      .collect(Collectors.joining("\n", "", "\n"));
  }

  private List<ILoggingEvent> captureLogs() {
    Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAndStopAllAppenders();
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender.list;
  }

}
