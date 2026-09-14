package br.com.mvc.dao;

import br.com.mvc.filter.ConnectionFactory;
import br.com.mvc.model.Avaliacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {
    public void inserir(Avaliacao avaliacao) throws SQLException {
        String sql = "INSERT INTO avaliacoes (filme_id, categoria_id, nota) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, avaliacao.getFilmeId());
            ps.setInt(2, avaliacao.getCategoriaId());
            ps.setInt(3, avaliacao.getNota());
            ps.executeUpdate();
        }
    }

    public List<Avaliacao> listar() throws SQLException {
        List<Avaliacao> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.filme_id, a.categoria_id, a.nota, f.titulo AS filme_titulo, c.nome AS categoria_nome " +
                "FROM avaliacoes a JOIN filmes f ON f.id = a.filme_id JOIN categorias c ON c.id = a.categoria_id ORDER BY a.id DESC";
        try (Connection con = ConnectionFactory.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Avaliacao a = new Avaliacao();
                a.setId(rs.getInt("id"));
                a.setFilmeId(rs.getInt("filme_id"));
                a.setCategoriaId(rs.getInt("categoria_id"));
                a.setNota(rs.getInt("nota"));
                a.setFilmeTitulo(rs.getString("filme_titulo"));
                a.setCategoriaNome(rs.getString("categoria_nome"));
                lista.add(a);
            }
        }
        return lista;
    }
}
