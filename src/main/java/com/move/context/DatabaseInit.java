package com.move.context;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;


public class DatabaseInit {

  private final Connection connection = AppContext.getInstance().getConnection();
  private static final String CREATE_TABLE = """
          CREATE TABLE IF NOT EXISTS migrations (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          migration_name VARCHAR(255) NOT NULL
          );
          """;

  private static final String SELECT_MIGRATION = "SELECT * FROM migrations WHERE migration_name = (?)";

  @SneakyThrows
  public void initDatabase() {
    createMigrationsTableIfNotExists();
    File[] files = getFilesFromDBDir();
    if (files != null) {
      Arrays.stream(files)
              .filter(f -> f.getName().startsWith("V") && f.getName().endsWith(".sql"))
              .sorted()
              .forEach(f -> {
                try {
                  String migrationName = f.getName();
                  if (!isMigrationExecuted(migrationName)) {
                    applyMigration(f, migrationName);
                    markMigrationAsExecuted(migrationName);
                  }
                } catch (Exception e) {
                  throw new RuntimeException("Can't apply migration: " + f.getName(), e);
                }
              });
    }
  }

  private boolean isMigrationExecuted(String migrationName) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SELECT_MIGRATION)) {
      ps.setString(1, migrationName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  private void applyMigration(File migrationFile, String migrationName) throws Exception {
    String migrationPath = "./db/" + migrationFile.getName();
    byte[] bytes = this.getClass().getClassLoader().getResourceAsStream(migrationPath).readAllBytes();
    String migrationScript = new String(bytes, StandardCharsets.UTF_8);

    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(migrationScript);
      connection.commit();
      System.out.println("Applied migration: " + migrationName);
    } catch (SQLException e) {
      connection.rollback();
      throw new RuntimeException("Error executing migration " + migrationName, e);
    }
  }

  private void markMigrationAsExecuted(String migrationName) throws SQLException {
    String insertSql = "INSERT INTO migrations (migration_name) VALUES (?)";
    try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
      stmt.setString(1, migrationName);
      stmt.executeUpdate();
      connection.commit();
    }
  }

  private File[] getFilesFromDBDir() throws URISyntaxException {
    URI uri = this.getClass().getClassLoader().getResource("db").toURI();
    File dbDirectory = new File(uri);
    return dbDirectory.listFiles();
  }

  private void createMigrationsTableIfNotExists() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(CREATE_TABLE);
    }
  }
}
