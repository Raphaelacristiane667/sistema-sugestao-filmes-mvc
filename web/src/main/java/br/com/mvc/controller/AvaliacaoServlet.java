package br.com.mvc.controller;

import br.com.mvc.client.ApiException;
import br.com.mvc.client.AvaliacaoClient;
import br.com.mvc.client.CategoriaClient;
import br.com.mvc.client.FilmeClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/avaliacoes")
public class AvaliacaoServlet extends TelaServlet {
    private final AvaliacaoClient avaliacoes = new AvaliacaoClient();
    private final FilmeClient filmes = new FilmeClient();
    private final CategoriaClient categorias = new CategoriaClient();

    @Override
    protected void carregar(HttpServletRequest req) throws ApiException {
        req.setAttribute("avaliacoes", ouVazia(avaliacoes.listar()));
        req.setAttribute("filmes", ouVazia(filmes.listar()));
        req.setAttribute("categorias", ouVazia(categorias.listar()));
    }

    @Override
    protected String jsp() { return "/WEB-INF/views/avaliacoes.jsp"; }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        salvar(req, resp, () -> avaliacoes.criar(
                inteiro(req, "filmeId", "Filme"),
                inteiro(req, "categoriaId", "Categoria"),
                inteiro(req, "nota", "Nota")));
    }
}
