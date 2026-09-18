package br.com.mvc.filter;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private static final String MODE = envOrDefault("DB_MODE", "mysql");
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try {
            if ("h2".equalsIgnoreCase(MODE)) {
                String dir = Path.of(System.getProperty("user.home"), ".mvc-java-tools", "data")
                        .toAbsolutePath()
                        .toString()
                        .replace('\\', '/');
                URL = "jdbc:h2:" + dir + "/mvc_java;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
                USER = "sa";
                PASSWORD = "";
                Class.forName("org.h2.Driver");
            } else {
                String host = envOrDefault("DB_HOST", "mysql");
                URL = "jdbc:mysql://" + host + ":3306/mvc_java?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci";
                USER = "mvc_user";
                PASSWORD = "mvc123";
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver JDBC nao encontrado para o modo " + MODE + ".", e);
        }
    }

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
