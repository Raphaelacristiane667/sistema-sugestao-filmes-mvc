package br.com.mvc.service;

import br.com.mvc.config.ConnectionFactory;
import br.com.mvc.config.DatabaseBootstrap;
import java.sql.Connection;
import java.sql.Statement;
import org.junit.Before;

/** Recria o banco H2 em memória com os dados de exemplo antes de cada teste. */
public abstract class BancoDeTeste {
    @Before
    public void recriarBanco() throws Exception {
        try (Connection con = ConnectionFactory.getConnection(); Statement st = con.createStatement()) {
            st.execute("DROP TABLE IF EXISTS avaliacoes");
            st.execute("DROP TABLE IF EXISTS categorias");
            st.execute("DROP TABLE IF EXISTS filmes");
        }
        DatabaseBootstrap.preparar();
    }
}
