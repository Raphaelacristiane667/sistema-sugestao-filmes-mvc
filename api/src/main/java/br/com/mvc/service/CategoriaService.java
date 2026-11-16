package br.com.mvc.service;

import br.com.mvc.dao.AvaliacaoDAO;
import br.com.mvc.dao.CategoriaDAO;
import br.com.mvc.dao.FilmeDAO;
import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Categoria;
import br.com.mvc.model.Filme;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Regras de negócio de categorias e das sugestões por categoria. */
public class CategoriaService {
    private final CategoriaDAO categoriaDAO;
    private final AvaliacaoDAO avaliacaoDAO;
    private final FilmeDAO filmeDAO;

    public CategoriaService() { this(new CategoriaDAO(), new AvaliacaoDAO(), new FilmeDAO()); }

    public CategoriaService(CategoriaDAO categoriaDAO, AvaliacaoDAO avaliacaoDAO, FilmeDAO filmeDAO) {
        this.categoriaDAO = categoriaDAO;
        this.avaliacaoDAO = avaliacaoDAO;
        this.filmeDAO = filmeDAO;
    }

    public List<Categoria> listar() throws SQLException {
        return categoriaDAO.listar();
    }

    public Categoria buscar(int id) throws SQLException {
        return categoriaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria " + id + " não encontrada."));
    }

    public Categoria criar(Categoria categoria) throws SQLException {
        validar(categoria, null);
        return buscar(categoriaDAO.inserir(categoria));
    }

    public Categoria atualizar(int id, Categoria categoria) throws SQLException {
        buscar(id);
        validar(categoria, id);
        categoria.setId(id);
        categoriaDAO.atualizar(categoria);
        return buscar(id);
    }

    public void excluir(int id) throws SQLException {
        buscar(id);
        int avaliacoes = avaliacaoDAO.contarPorCategoria(id);
        if (avaliacoes > 0) {
            throw new ConflitoException("A categoria " + id + " tem " + avaliacoes + " avaliação(ões). Exclua as avaliações antes.");
        }
        categoriaDAO.excluir(id);
    }

    /** Filmes avaliados na categoria, do maior para o menor pela média das notas. */
    public List<Filme> sugerir(int categoriaId) throws SQLException {
        buscar(categoriaId);
        return filmeDAO.sugerirPorCategoria(categoriaId);
    }

    private void validar(Categoria categoria, Integer idAtual) throws SQLException {
        String nome = categoria.getNome() == null ? "" : categoria.getNome().trim();
        if (nome.isEmpty()) throw new ValidacaoException("O nome da categoria é obrigatório.");
        if (nome.length() > 100) throw new ValidacaoException("O nome deve ter no máximo 100 caracteres.");
        Optional<Categoria> existente = categoriaDAO.buscarPorNome(nome);
        if (existente.isPresent() && (idAtual == null || existente.get().getId() != idAtual)) {
            throw new ConflitoException("Já existe uma categoria chamada \"" + nome + "\".");
        }
        categoria.setNome(nome);
    }
}
