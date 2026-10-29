package br.com.mvc.controller;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.ValidacaoException;
import br.com.mvc.model.Categoria;
import br.com.mvc.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {
    private final CategoriaService service = new CategoriaService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("categorias", service.listar());
            req.getRequestDispatcher("/WEB-INF/views/categorias.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar categorias.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Categoria c = new Categoria();
            c.setNome(req.getParameter("nome"));
            service.criar(c);
            resp.sendRedirect(req.getContextPath() + "/categorias");
        } catch (ValidacaoException | ConflitoException e) {
            req.setAttribute("erro", e.getMessage());
            doGet(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao cadastrar categoria.", e); }
    }
}
