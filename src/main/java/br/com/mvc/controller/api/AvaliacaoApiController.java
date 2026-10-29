package br.com.mvc.controller.api;

import br.com.mvc.model.Avaliacao;
import br.com.mvc.service.AvaliacaoService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET    /api/avaliacoes[?filmeId=&categoriaId=]   lista (filtros opcionais)
 * GET    /api/avaliacoes/{id}                      busca
 * POST   /api/avaliacoes                           cria
 * PUT    /api/avaliacoes/{id}                      atualiza
 * DELETE /api/avaliacoes/{id}                      exclui
 */
@WebServlet("/api/avaliacoes/*")
public class AvaliacaoApiController extends ApiServlet {
    private final AvaliacaoService service = new AvaliacaoService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length == 0) json(resp, 200, service.listar(filtroInteiro(req, "filmeId"), filtroInteiro(req, "categoriaId")));
        else if (caminho.length == 1) json(resp, 200, service.buscar(id(caminho[0])));
        else super.get(req, resp, caminho);
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 0) { super.post(req, resp, caminho); return; }
        Avaliacao criada = service.criar(lerAvaliacao(req));
        criado(req, resp, criada.getId(), criada);
    }

    @Override
    protected void put(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.put(req, resp, caminho); return; }
        json(resp, 200, service.atualizar(id(caminho[0]), lerAvaliacao(req)));
    }

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.delete(req, resp, caminho); return; }
        service.excluir(id(caminho[0]));
        semConteudo(resp);
    }

    private Avaliacao lerAvaliacao(HttpServletRequest req) throws Exception {
        JsonObject obj = corpo(req);
        Avaliacao a = new Avaliacao();
        a.setFilmeId(inteiro(obj, "filmeId"));
        a.setCategoriaId(inteiro(obj, "categoriaId"));
        a.setNota(inteiro(obj, "nota"));
        return a;
    }
}
