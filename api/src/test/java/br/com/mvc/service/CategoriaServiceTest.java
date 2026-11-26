package br.com.mvc.service;

import static org.junit.Assert.*;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.model.Avaliacao;
import br.com.mvc.model.Categoria;
import br.com.mvc.model.Filme;
import java.util.List;
import org.junit.Test;

public class CategoriaServiceTest extends BancoDeTeste {
    private final CategoriaService service = new CategoriaService();

    @Test
    public void naoPermiteNomeDuplicado() {
        assertThrows(ConflitoException.class, () -> service.criar(new Categoria(0, "drama")));
    }

    @Test
    public void podeRenomearMantendoOMesmoNome() throws Exception {
        Categoria drama = service.listar().stream().filter(c -> c.getNome().equals("Drama")).findFirst().orElseThrow();
        assertEquals("Drama", service.atualizar(drama.getId(), new Categoria(0, "Drama")).getNome());
    }

    @Test
    public void excluiCategoriaSemAvaliacoesEBloqueiaComAvaliacoes() throws Exception {
        Categoria nova = service.criar(new Categoria(0, "Terror"));
        service.excluir(nova.getId());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscar(nova.getId()));
        assertThrows(ConflitoException.class, () -> service.excluir(5));
    }

    @Test
    public void sugestoesVemOrdenadasPelaMedia() throws Exception {
        AvaliacaoService avaliacoes = new AvaliacaoService();
        // Ficção Científica (id 5): Matrix tem 9 e Interestelar 10. Uma nota 2 para Interestelar
        // derruba a média dele para 6, e Matrix passa para o topo.
        Avaliacao a = new Avaliacao();
        a.setFilmeId(5);
        a.setCategoriaId(5);
        a.setNota(2);
        avaliacoes.criar(a);

        List<Filme> sugestoes = service.sugerir(5);
        assertEquals("Matrix", sugestoes.get(0).getTitulo());
        assertEquals(9.0, sugestoes.get(0).getMedia(), 0.001);
        assertEquals("Interestelar", sugestoes.get(1).getTitulo());
        assertEquals(6.0, sugestoes.get(1).getMedia(), 0.001);
    }

    @Test
    public void sugestoesDeCategoriaInexistenteDa404() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.sugerir(999));
    }
}
