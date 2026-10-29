package br.com.mvc.service;

import br.com.mvc.dao.AvaliacaoDAO;
import br.com.mvc.dao.CategoriaDAO;
import br.com.mvc.dao.FilmeDAO;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Avaliacao;
import java.sql.SQLException;
import java.util.List;

/** Regras de negócio de avaliações: nota de 0 a 10, filme e categoria precisam existir. */
public class AvaliacaoService {
    public static final int NOTA_MINIMA = 0;
    public static final int NOTA_MAXIMA = 10;

    private final AvaliacaoDAO avaliacaoDAO;
    private final FilmeDAO filmeDAO;
    private final CategoriaDAO categoriaDAO;

    public AvaliacaoService() { this(new AvaliacaoDAO(), new FilmeDAO(), new CategoriaDAO()); }

    public AvaliacaoService(AvaliacaoDAO avaliacaoDAO, FilmeDAO filmeDAO, CategoriaDAO categoriaDAO) {
        this.avaliacaoDAO = avaliacaoDAO;
        this.filmeDAO = filmeDAO;
        this.categoriaDAO = categoriaDAO;
    }

    public List<Avaliacao> listar() throws SQLException {
        return avaliacaoDAO.listar();
    }

    public List<Avaliacao> listar(Integer filmeId, Integer categoriaId) throws SQLException {
        return avaliacaoDAO.listar(filmeId, categoriaId);
    }

    public Avaliacao buscar(int id) throws SQLException {
        return avaliacaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação " + id + " não encontrada."));
    }

    public Avaliacao criar(Avaliacao avaliacao) throws SQLException {
        validar(avaliacao);
        return buscar(avaliacaoDAO.inserir(avaliacao));
    }

    public Avaliacao atualizar(int id, Avaliacao avaliacao) throws SQLException {
        buscar(id);
        validar(avaliacao);
        avaliacao.setId(id);
        avaliacaoDAO.atualizar(avaliacao);
        return buscar(id);
    }

    public void excluir(int id) throws SQLException {
        buscar(id);
        avaliacaoDAO.excluir(id);
    }

    private void validar(Avaliacao a) throws SQLException {
        if (a.getNota() < NOTA_MINIMA || a.getNota() > NOTA_MAXIMA) {
            throw new ValidacaoException("A nota deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA + ".");
        }
        if (filmeDAO.buscarPorId(a.getFilmeId()).isEmpty()) {
            throw new ValidacaoException("O filme " + a.getFilmeId() + " não existe.");
        }
        if (categoriaDAO.buscarPorId(a.getCategoriaId()).isEmpty()) {
            throw new ValidacaoException("A categoria " + a.getCategoriaId() + " não existe.");
        }
    }
}
