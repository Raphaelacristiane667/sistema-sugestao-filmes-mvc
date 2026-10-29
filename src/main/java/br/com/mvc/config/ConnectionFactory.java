package br.com.mvc.config;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Cria conexões JDBC. Por padrão usa MySQL; com DB_MODE=h2 usa um banco H2 local
 * (útil para rodar sem Docker). DB_URL, DB_USER e DB_PASSWORD sobrescrevem os padrões.
 * Cada chave é lida primeiro como propriedade do sistema (-DDB_MODE=h2) e depois como
 * variável de ambiente.
 */
public final class ConnectionFactory {
    private static final String MODE = config("DB_MODE", "mysql");
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
                URL = config("DB_URL", "jdbc:h2:" + dir + "/mvc_java;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
                USER = config("DB_USER", "sa");
                PASSWORD = config("DB_PASSWORD", "");
                Class.forName("org.h2.Driver");
            } else {
                String host = config("DB_HOST", "mysql");
                URL = config("DB_URL", "jdbc:mysql://" + host + ":3306/mvc_java?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci");
                USER = config("DB_USER", "mvc_user");
                PASSWORD = config("DB_PASSWORD", "mvc123");
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

    private static String config(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
