package br.com.mvc.controller.api;

import br.com.mvc.model.Categoria;
import br.com.mvc.service.CategoriaService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET    /api/categorias                  lista
 * GET    /api/categorias/{id}             busca
 * GET    /api/categorias/{id}/sugestoes   filmes da categoria ordenados pela média
 * POST   /api/categorias                  cria
 * PUT    /api/categorias/{id}             atualiza
 * DELETE /api/categorias/{id}             exclui
 */
@WebServlet("/api/categorias/*")
public class CategoriaApiController extends ApiServlet {
    private final CategoriaService service = new CategoriaService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length == 0) json(resp, 200, service.listar());
        else if (caminho.length == 1) json(resp, 200, service.buscar(id(caminho[0])));
        else if (caminho.length == 2 && "sugestoes".equals(caminho[1])) json(resp, 200, service.sugerir(id(caminho[0])));
        else super.get(req, resp, caminho);
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 0) { super.post(req, resp, caminho); return; }
        Categoria criada = service.criar(lerCategoria(req));
        criado(req, resp, criada.getId(), criada);
    }

    @Override
    protected void put(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.put(req, resp, caminho); return; }
        json(resp, 200, service.atualizar(id(caminho[0]), lerCategoria(req)));
    }

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        if (caminho.length != 1) { super.delete(req, resp, caminho); return; }
        service.excluir(id(caminho[0]));
        semConteudo(resp);
    }

    private Categoria lerCategoria(HttpServletRequest req) throws Exception {
        JsonObject obj = corpo(req);
        Categoria categoria = new Categoria();
        categoria.setNome(texto(obj, "nome"));
        return categoria;
    }
}
