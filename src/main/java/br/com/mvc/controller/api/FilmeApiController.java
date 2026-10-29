package br.com.mvc.controller.api;

import br.com.mvc.model.Filme;
import br.com.mvc.service.FilmeService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET    /api/filmes        lista
 * GET    /api/filmes/{id}   busca
 * POST   /api/filmes        cria
 * PUT    /api/filmes/{id}   atualiza
 * DELETE /api/filmes/{id}   exclui
 */
@WebServlet("/api/filmes/*")
public class FilmeApiController extends ApiServlet {
    private final FilmeService service = new FilmeService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length == 0) json(resp, 200, service.listar());
        else if (caminho.length == 1) json(resp, 200, service.buscar(id(caminho[0])));
        else super.get(req, resp, caminho);
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 0) { super.post(req, resp, caminho); return; }
        Filme criado = service.criar(lerFilme(req));
        criado(req, resp, criado.getId(), criado);
    }

    @Override
    protected void put(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.put(req, resp, caminho); return; }
        json(resp, 200, service.atualizar(id(caminho[0]), lerFilme(req)));
    }

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.delete(req, resp, caminho); return; }
        service.excluir(id(caminho[0]));
        semConteudo(resp);
    }

    private Filme lerFilme(HttpServletRequest req) throws Exception {
        JsonObject obj = corpo(req);
        Filme filme = new Filme();
        filme.setTitulo(texto(obj, "titulo"));
        filme.setAnoLancamento(inteiro(obj, "anoLancamento"));
        return filme;
    }
}
