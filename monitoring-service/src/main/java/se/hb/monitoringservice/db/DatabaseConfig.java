package se.hb.monitoringservice.db;

public record DatabaseConfig(int dbPort, String dbUser, String dbPass, String dbName, String dbHost) {
}
