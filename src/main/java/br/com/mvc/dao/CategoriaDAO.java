package br.com.mvc.dao;

import br.com.mvc.config.ConnectionFactory;
import br.com.mvc.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAO {
    public int inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNome());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM categorias ORDER BY nome";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Categoria(rs.getInt("id"), rs.getString("nome")));
        }
        return lista;
    }

    public Optional<Categoria> buscarPorId(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT id, nome FROM categorias WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Categoria(rs.getInt("id"), rs.getString("nome")));
            }
        }
    }

    /** Procura uma categoria pelo nome, sem diferenciar maiúsculas de minúsculas. */
    public Optional<Categoria> buscarPorNome(String nome) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT id, nome FROM categorias WHERE LOWER(nome) = LOWER(?)")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Categoria(rs.getInt("id"), rs.getString("nome")));
            }
        }
    }

    public boolean atualizar(Categoria categoria) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE categorias SET nome = ? WHERE id = ?")) {
            ps.setString(1, categoria.getNome());
            ps.setInt(2, categoria.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM categorias WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
