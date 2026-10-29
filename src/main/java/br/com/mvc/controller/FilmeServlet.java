package br.com.mvc.controller;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Filme;
import br.com.mvc.service.CategoriaService;
import br.com.mvc.service.FilmeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/filmes")
public class FilmeServlet extends HttpServlet {
    private final FilmeService filmeService = new FilmeService();
    private final CategoriaService categoriaService = new CategoriaService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String cat = req.getParameter("categoriaId");
            if (cat != null && !cat.isBlank()) req.setAttribute("sugestoes", categoriaService.sugerir(Integer.parseInt(cat)));
            req.setAttribute("filmes", filmeService.listar());
            req.setAttribute("categorias", categoriaService.listar());
            req.getRequestDispatcher("/WEB-INF/views/filmes.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar filmes.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Filme filme = new Filme();
            filme.setTitulo(req.getParameter("titulo"));
            filme.setAnoLancamento(Integer.parseInt(req.getParameter("anoLancamento")));
            filmeService.criar(filme);
            resp.sendRedirect(req.getContextPath() + "/filmes");
        } catch (ValidacaoException | ConflitoException e) {
            req.setAttribute("erro", e.getMessage());
            doGet(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao cadastrar filme.", e); }
    }
}
