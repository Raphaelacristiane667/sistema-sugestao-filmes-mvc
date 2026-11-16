package br.com.mvc.service;

import static org.junit.Assert.*;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Filme;
import org.junit.Test;

public class FilmeServiceTest extends BancoDeTeste {
    private final FilmeService service = new FilmeService();

    @Test
    public void criaBuscaAtualizaEExclui() throws Exception {
        Filme novo = new Filme();
        novo.setTitulo("  Duna  ");
        novo.setAnoLancamento(2021);
        Filme criado = service.criar(novo);
        assertTrue(criado.getId() > 0);
        assertEquals("Duna", criado.getTitulo());

        Filme alteracao = new Filme();
        alteracao.setTitulo("Duna: Parte Um");
        alteracao.setAnoLancamento(2021);
        assertEquals("Duna: Parte Um", service.atualizar(criado.getId(), alteracao).getTitulo());

        service.excluir(criado.getId());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(criado.getId()));
    }

    @Test
    public void rejeitaTituloVazioEAnoForaDoIntervalo() {
        Filme semTitulo = new Filme(0, " ", 2000);
        assertThrows(ValidacaoException.class, () -> service.criar(semTitulo));
        Filme anoAntigo = new Filme(0, "Antigo", 1500);
        assertThrows(ValidacaoException.class, () -> service.criar(anoAntigo));
    }

    @Test
    public void naoExcluiFilmeComAvaliacoes() {
        assertThrows(ConflitoException.class, () -> service.excluir(1));
    }

    @Test
    public void atualizarFilmeInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(999, new Filme(0, "X", 2000)));
    }
}
