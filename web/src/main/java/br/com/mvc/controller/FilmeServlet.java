package br.com.mvc.controller;

import br.com.mvc.client.ApiException;
import br.com.mvc.client.CategoriaClient;
import br.com.mvc.client.FilmeClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/filmes")
public class FilmeServlet extends TelaServlet {
    private final FilmeClient filmes = new FilmeClient();
    private final CategoriaClient categorias = new CategoriaClient();

    @Override
    protected void carregar(HttpServletRequest req) throws ApiException {
        req.setAttribute("filmes", ouVazia(filmes.listar()));
        req.setAttribute("categorias", ouVazia(categorias.listar()));
        String cat = req.getParameter("categoriaId");
        if (cat != null && !cat.isBlank()) {
            req.setAttribute("sugestoes", ouVazia(categorias.sugerir(inteiro(req, "categoriaId", "categoria"))));
        }
    }

    @Override
    protected String jsp() { return "/WEB-INF/views/filmes.jsp"; }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        salvar(req, resp, () -> filmes.criar(req.getParameter("titulo"), inteiro(req, "anoLancamento", "Ano de lançamento")));
    }
}
