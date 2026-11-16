package br.com.mvc.controller;

import br.com.mvc.client.ApiException;
import br.com.mvc.client.CategoriaClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/categorias")
public class CategoriaServlet extends TelaServlet {
    private final CategoriaClient categorias = new CategoriaClient();

    @Override
    protected void carregar(HttpServletRequest req) throws ApiException {
        req.setAttribute("categorias", ouVazia(categorias.listar()));
    }

    @Override
    protected String jsp() { return "/WEB-INF/views/categorias.jsp"; }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        salvar(req, resp, () -> categorias.criar(req.getParameter("nome")));
    }
}
