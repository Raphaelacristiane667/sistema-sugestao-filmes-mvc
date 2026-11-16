package br.com.mvc.dao;

import br.com.mvc.config.ConnectionFactory;
import br.com.mvc.model.Avaliacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvaliacaoDAO {
    private static final String SELECT_BASE = "SELECT a.id, a.filme_id, a.categoria_id, a.nota, f.titulo AS filme_titulo, c.nome AS categoria_nome " +
            "FROM avaliacoes a JOIN filmes f ON f.id = a.filme_id JOIN categorias c ON c.id = a.categoria_id";

    public int inserir(Avaliacao avaliacao) throws SQLException {
        String sql = "INSERT INTO avaliacoes (filme_id, categoria_id, nota) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, avaliacao.getFilmeId());
            ps.setInt(2, avaliacao.getCategoriaId());
            ps.setInt(3, avaliacao.getNota());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<Avaliacao> listar() throws SQLException {
        return listar(null, null);
    }

    /** Lista as avaliações; filmeId e categoriaId são filtros opcionais (null = sem filtro). */
    public List<Avaliacao> listar(Integer filmeId, Integer categoriaId) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1 = 1");
        if (filmeId != null) sql.append(" AND a.filme_id = ?");
        if (categoriaId != null) sql.append(" AND a.categoria_id = ?");
        sql.append(" ORDER BY a.id DESC");
        List<Avaliacao> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            if (filmeId != null) ps.setInt(i++, filmeId);
            if (categoriaId != null) ps.setInt(i, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Optional<Avaliacao> buscarPorId(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE a.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public boolean atualizar(Avaliacao avaliacao) throws SQLException {
        String sql = "UPDATE avaliacoes SET filme_id = ?, categoria_id = ?, nota = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, avaliacao.getFilmeId());
            ps.setInt(2, avaliacao.getCategoriaId());
            ps.setInt(3, avaliacao.getNota());
            ps.setInt(4, avaliacao.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM avaliacoes WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int contarPorFilme(int filmeId) throws SQLException {
        return contar("SELECT COUNT(*) FROM avaliacoes WHERE filme_id = ?", filmeId);
    }

    public int contarPorCategoria(int categoriaId) throws SQLException {
        return contar("SELECT COUNT(*) FROM avaliacoes WHERE categoria_id = ?", categoriaId);
    }

    private int contar(String sql, int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Avaliacao mapear(ResultSet rs) throws SQLException {
        Avaliacao a = new Avaliacao();
        a.setId(rs.getInt("id"));
        a.setFilmeId(rs.getInt("filme_id"));
        a.setCategoriaId(rs.getInt("categoria_id"));
        a.setNota(rs.getInt("nota"));
        a.setFilmeTitulo(rs.getString("filme_titulo"));
        a.setCategoriaNome(rs.getString("categoria_nome"));
        return a;
    }
}
