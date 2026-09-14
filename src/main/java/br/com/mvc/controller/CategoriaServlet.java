package br.com.mvc.controller;

import br.com.mvc.dao.CategoriaDAO;
import br.com.mvc.model.Categoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {
    private final CategoriaDAO dao = new CategoriaDAO();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("categorias", dao.listar());
            req.getRequestDispatcher("/WEB-INF/views/categorias.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException("Erro ao carregar categorias.", e); }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Categoria c = new Categoria();
            c.setNome(req.getParameter("nome"));
            dao.inserir(c);
            resp.sendRedirect(req.getContextPath() + "/categorias");
        } catch (Exception e) { throw new ServletException("Erro ao cadastrar categoria.", e); }
    }
}
