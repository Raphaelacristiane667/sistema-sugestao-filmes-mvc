package br.com.mvc.service;

import static org.junit.Assert.*;

import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Avaliacao;
import org.junit.Test;

public class AvaliacaoServiceTest extends BancoDeTeste {
    private final AvaliacaoService service = new AvaliacaoService();

    private Avaliacao avaliacao(int filmeId, int categoriaId, int nota) {
        Avaliacao a = new Avaliacao();
        a.setFilmeId(filmeId);
        a.setCategoriaId(categoriaId);
        a.setNota(nota);
        return a;
    }

    @Test
    public void criaComTituloENomePreenchidos() throws Exception {
        Avaliacao criada = service.criar(avaliacao(3, 3, 7));
        assertEquals("Toy Story", criada.getFilmeTitulo());
        assertEquals("Animação", criada.getCategoriaNome());
        assertEquals(7, criada.getNota());
    }

    @Test
    public void rejeitaNotaForaDe0a10() {
        assertThrows(ValidacaoException.class, () -> service.criar(avaliacao(1, 1, 11)));
        assertThrows(ValidacaoException.class, () -> service.criar(avaliacao(1, 1, -1)));
    }

    @Test
    public void rejeitaFilmeOuCategoriaInexistente() {
        assertThrows(ValidacaoException.class, () -> service.criar(avaliacao(999, 1, 5)));
        assertThrows(ValidacaoException.class, () -> service.criar(avaliacao(1, 999, 5)));
    }

    @Test
    public void filtraPorFilmeECategoria() throws Exception {
        service.criar(avaliacao(1, 1, 8));
        assertEquals(2, service.listar(1, null).size());
        assertEquals(1, service.listar(1, 1).size());
        assertEquals(0, service.listar(2, 1).size());
    }

    @Test
    public void atualizaEExclui() throws Exception {
        Avaliacao criada = service.criar(avaliacao(2, 1, 4));
        assertEquals(6, service.atualizar(criada.getId(), avaliacao(2, 1, 6)).getNota());
        int antes = service.listar().size();
        service.excluir(criada.getId());
        assertEquals(antes - 1, service.listar().size());
    }
}
