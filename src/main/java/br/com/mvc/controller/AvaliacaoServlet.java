package br.com.mvc.controller;

import br.com.mvc.dao.AvaliacaoDAO;
import br.com.mvc.dao.CategoriaDAO;
import br.com.mvc.dao.FilmeDAO;
import br.com.mvc.model.Avaliacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/avaliacoes")
public class AvaliacaoServlet extends HttpServlet {
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final FilmeDAO filmeDAO = new FilmeDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("avaliacoes", avaliacaoDAO.listar());
            req.setAttribute("filmes", filmeDAO.listar());
            req.setAttribute("categorias", categoriaDAO.listar());
            req.getRequestDispatcher("/WEB-INF/views/avaliacoes.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar avaliações.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Avaliacao a = new Avaliacao();
            a.setFilmeId(Integer.parseInt(req.getParameter("filmeId")));
            a.setCategoriaId(Integer.parseInt(req.getParameter("categoriaId")));
            a.setNota(Integer.parseInt(req.getParameter("nota")));
            if (a.getNota() < 0 || a.getNota() > 10) throw new IllegalArgumentException("A nota deve estar entre 0 e 10.");
            avaliacaoDAO.inserir(a);
            resp.sendRedirect(req.getContextPath() + "/avaliacoes");
        } catch (Exception e) { throw new ServletException("Erro ao registrar avaliação.", e); }
    }
}
