package br.com.mvc.dao;

import br.com.mvc.config.ConnectionFactory;
import br.com.mvc.model.Filme;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilmeDAO {
    public int inserir(Filme filme) throws SQLException {
        String sql = "INSERT INTO filmes (titulo, ano_lancamento) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, filme.getTitulo());
            ps.setInt(2, filme.getAnoLancamento());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /** Base das consultas: cada filme com a média e o total de avaliações (todas as categorias). */
    private static final String SELECT_COM_MEDIA = "SELECT f.id, f.titulo, f.ano_lancamento, AVG(a.nota) AS media, COUNT(a.id) AS total " +
            "FROM filmes f LEFT JOIN avaliacoes a ON a.filme_id = f.id ";

    public List<Filme> listar() throws SQLException {
        List<Filme> lista = new ArrayList<>();
        String sql = SELECT_COM_MEDIA + "GROUP BY f.id, f.titulo, f.ano_lancamento ORDER BY f.titulo";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Optional<Filme> buscarPorId(int id) throws SQLException {
        String sql = SELECT_COM_MEDIA + "WHERE f.id = ? GROUP BY f.id, f.titulo, f.ano_lancamento";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public boolean atualizar(Filme filme) throws SQLException {
        String sql = "UPDATE filmes SET titulo = ?, ano_lancamento = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, filme.getTitulo());
            ps.setInt(2, filme.getAnoLancamento());
            ps.setInt(3, filme.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM filmes WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Filme> sugerirPorCategoria(int categoriaId) throws SQLException {
        List<Filme> lista = new ArrayList<>();
        String sql = "SELECT f.id, f.titulo, f.ano_lancamento, AVG(a.nota) AS media, COUNT(a.id) AS total " +
                "FROM filmes f JOIN avaliacoes a ON a.filme_id = f.id " +
                "WHERE a.categoria_id = ? GROUP BY f.id, f.titulo, f.ano_lancamento ORDER BY media DESC, f.titulo";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Filme mapear(ResultSet rs) throws SQLException {
        Filme filme = new Filme(rs.getInt("id"), rs.getString("titulo"), rs.getInt("ano_lancamento"));
        double media = rs.getDouble("media");
        filme.setMedia(rs.wasNull() ? null : Math.round(media * 10) / 10.0);
        filme.setTotalAvaliacoes(rs.getInt("total"));
        return filme;
    }
}
