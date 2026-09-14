package br.com.mvc.dao;

import br.com.mvc.filter.ConnectionFactory;
import br.com.mvc.model.Filme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmeDAO {
    public void inserir(Filme filme) throws SQLException {
        String sql = "INSERT INTO filmes (titulo, ano_lancamento) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, filme.getTitulo());
            ps.setInt(2, filme.getAnoLancamento());
            ps.executeUpdate();
        }
    }

    public List<Filme> listar() throws SQLException {
        List<Filme> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, ano_lancamento FROM filmes ORDER BY titulo";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Filme(rs.getInt("id"), rs.getString("titulo"), rs.getInt("ano_lancamento")));
        }
        return lista;
    }

    public List<Filme> sugerirPorCategoria(int categoriaId) throws SQLException {
        List<Filme> lista = new ArrayList<>();
        String sql = "SELECT f.id, f.titulo, f.ano_lancamento, AVG(a.nota) AS media " +
                "FROM filmes f JOIN avaliacoes a ON a.filme_id = f.id " +
                "WHERE a.categoria_id = ? GROUP BY f.id, f.titulo, f.ano_lancamento ORDER BY media DESC, f.titulo";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Filme filme = new Filme(rs.getInt("id"), rs.getString("titulo"), rs.getInt("ano_lancamento"));
                    filme.setMedia(rs.getDouble("media"));
                    lista.add(filme);
                }
            }
        }
        return lista;
    }
}
