package br.com.mvc.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Cria as tabelas (se não existirem) e popula dados de exemplo quando a aplicação sobe. */
@WebListener
public class DatabaseBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SQLException ultimo = null;
        for (int tentativa = 1; tentativa <= 20; tentativa++) {
            try {
                preparar();
                return;
            } catch (SQLException e) {
                ultimo = e;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Falha ao preparar o banco.", e);
                }
            }
        }
        throw new IllegalStateException("Falha ao preparar o banco.", ultimo);
    }

    public static void preparar() throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS filmes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "titulo VARCHAR(150) NOT NULL, " +
                    "ano_lancamento INT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS categorias (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS avaliacoes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "filme_id INT NOT NULL, " +
                    "categoria_id INT NOT NULL, " +
                    "nota INT NOT NULL, " +
                    "CONSTRAINT fk_avaliacao_filme FOREIGN KEY (filme_id) REFERENCES filmes(id), " +
                    "CONSTRAINT fk_avaliacao_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id))");
            popularSeVazio(st);
        }
    }

    private static void popularSeVazio(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM filmes")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        st.executeUpdate("INSERT INTO filmes (titulo, ano_lancamento) VALUES " +
                "('Matrix', 1999), ('O Senhor dos Anéis', 2001), ('Toy Story', 1995), " +
                "('Cidade de Deus', 2002), ('Interestelar', 2014)");
        st.executeUpdate("INSERT INTO categorias (nome) VALUES " +
                "('Ação'), ('Aventura'), ('Animação'), ('Drama'), ('Ficção Científica')");
        st.executeUpdate("INSERT INTO avaliacoes (filme_id, categoria_id, nota) VALUES " +
                "(1,5,9), (2,2,10), (3,3,8), (4,4,9), (5,5,10)");
    }
}
