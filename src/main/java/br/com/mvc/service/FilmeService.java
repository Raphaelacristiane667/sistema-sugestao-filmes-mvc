package br.com.mvc.service;

import br.com.mvc.dao.AvaliacaoDAO;
import br.com.mvc.dao.FilmeDAO;
import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Filme;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

/** Regras de negócio de filmes. */
public class FilmeService {
    static final int ANO_MINIMO = 1888;

    private final FilmeDAO filmeDAO;
    private final AvaliacaoDAO avaliacaoDAO;

    public FilmeService() { this(new FilmeDAO(), new AvaliacaoDAO()); }

    public FilmeService(FilmeDAO filmeDAO, AvaliacaoDAO avaliacaoDAO) {
        this.filmeDAO = filmeDAO;
        this.avaliacaoDAO = avaliacaoDAO;
    }

    public List<Filme> listar() throws SQLException {
        return filmeDAO.listar();
    }

    public Filme buscar(int id) throws SQLException {
        return filmeDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme " + id + " não encontrado."));
    }

    public Filme criar(Filme filme) throws SQLException {
        validar(filme);
        return buscar(filmeDAO.inserir(filme));
    }

    public Filme atualizar(int id, Filme filme) throws SQLException {
        buscar(id);
        validar(filme);
        filme.setId(id);
        filmeDAO.atualizar(filme);
        return buscar(id);
    }

    public void excluir(int id) throws SQLException {
        buscar(id);
        int avaliacoes = avaliacaoDAO.contarPorFilme(id);
        if (avaliacoes > 0) {
            throw new ConflitoException("O filme " + id + " tem " + avaliacoes + " avaliação(ões). Exclua as avaliações antes.");
        }
        filmeDAO.excluir(id);
    }

    private void validar(Filme filme) {
        String titulo = filme.getTitulo() == null ? "" : filme.getTitulo().trim();
        if (titulo.isEmpty()) throw new ValidacaoException("O título é obrigatório.");
        if (titulo.length() > 150) throw new ValidacaoException("O título deve ter no máximo 150 caracteres.");
        int anoMaximo = Year.now().getValue() + 5;
        if (filme.getAnoLancamento() < ANO_MINIMO || filme.getAnoLancamento() > anoMaximo) {
            throw new ValidacaoException("O ano de lançamento deve estar entre " + ANO_MINIMO + " e " + anoMaximo + ".");
        }
        filme.setTitulo(titulo);
    }
}
