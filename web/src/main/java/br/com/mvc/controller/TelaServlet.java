package br.com.mvc.controller;

import br.com.mvc.client.ApiException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Base das telas JSP. Os dados vêm da API REST (pacote client); quando a API
 * responde erro, a mensagem dela aparece na tela em vez de uma página de erro.
 */
public abstract class TelaServlet extends HttpServlet {

    /** Carrega os dados da tela (chamando a API) e coloca como atributos da requisição. */
    protected abstract void carregar(HttpServletRequest req) throws ApiException;

    protected abstract String jsp();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            carregar(req);
        } catch (ApiException e) {
            mostrarErro(req, e.getMessage());
        }
        req.getRequestDispatcher(jsp()).forward(req, resp);
    }

    /** Executa um cadastro; se der certo volta para a tela, senão mostra o erro nela. */
    protected void salvar(HttpServletRequest req, HttpServletResponse resp, Acao acao) throws ServletException, IOException {
        try {
            acao.executar();
            resp.sendRedirect(req.getContextPath() + req.getServletPath());
        } catch (ApiException | IllegalArgumentException e) {
            mostrarErro(req, e.getMessage());
            doGet(req, resp);
        }
    }

    protected void mostrarErro(HttpServletRequest req, String mensagem) {
        if (req.getAttribute("erro") == null) req.setAttribute("erro", mensagem);
    }

    /** Lê um número inteiro de um campo do formulário. */
    protected int inteiro(HttpServletRequest req, String campo, String rotulo) {
        String valor = req.getParameter(campo);
        try {
            return Integer.parseInt(valor == null ? "" : valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um número válido em \"" + rotulo + "\".");
        }
    }

    protected static <T> List<T> ouVazia(List<T> lista) {
        return lista == null ? List.of() : lista;
    }

    @FunctionalInterface
    protected interface Acao {
        void executar() throws ApiException;
    }
}
