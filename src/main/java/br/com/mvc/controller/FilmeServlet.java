package br.com.mvc.controller;

import br.com.mvc.dao.CategoriaDAO;
import br.com.mvc.dao.FilmeDAO;
import br.com.mvc.model.Filme;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/filmes")
public class FilmeServlet extends HttpServlet {
    private final FilmeDAO filmeDAO = new FilmeDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String cat = req.getParameter("categoriaId");
            if (cat != null && !cat.isBlank()) req.setAttribute("sugestoes", filmeDAO.sugerirPorCategoria(Integer.parseInt(cat)));
            req.setAttribute("filmes", filmeDAO.listar());
            req.setAttribute("categorias", categoriaDAO.listar());
            req.getRequestDispatcher("/WEB-INF/views/filmes.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar filmes.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Filme filme = new Filme();
            filme.setTitulo(req.getParameter("titulo"));
            filme.setAnoLancamento(Integer.parseInt(req.getParameter("anoLancamento")));
            filmeDAO.inserir(filme);
            resp.sendRedirect(req.getContextPath() + "/filmes");
        } catch (Exception e) { throw new ServletException("Erro ao cadastrar filme.", e); }
    }
}
