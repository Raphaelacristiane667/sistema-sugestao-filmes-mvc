package br.com.mvc.dao;

import br.com.mvc.filter.ConnectionFactory;
import br.com.mvc.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public void inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.executeUpdate();
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
}
