package br.com.mvc.controller;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Avaliacao;
import br.com.mvc.service.AvaliacaoService;
import br.com.mvc.service.CategoriaService;
import br.com.mvc.service.FilmeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/avaliacoes")
public class AvaliacaoServlet extends HttpServlet {
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final FilmeService filmeService = new FilmeService();
    private final CategoriaService categoriaService = new CategoriaService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("avaliacoes", avaliacaoService.listar());
            req.setAttribute("filmes", filmeService.listar());
            req.setAttribute("categorias", categoriaService.listar());
            req.getRequestDispatcher("/WEB-INF/views/avaliacoes.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar avaliações.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Avaliacao a = new Avaliacao();
            a.setFilmeId(Integer.parseInt(req.getParameter("filmeId")));
            a.setCategoriaId(Integer.parseInt(req.getParameter("categoriaId")));
            a.setNota(Integer.parseInt(req.getParameter("nota")));
            avaliacaoService.criar(a);
            resp.sendRedirect(req.getContextPath() + "/avaliacoes");
        } catch (ValidacaoException | ConflitoException e) {
            req.setAttribute("erro", e.getMessage());
            doGet(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao registrar avaliação.", e); }
    }
}
