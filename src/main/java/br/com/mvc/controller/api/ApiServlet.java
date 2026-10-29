package br.com.mvc.controller.api;

import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.exception.ValidacaoException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base dos controllers REST: lê e escreve JSON, separa o caminho da URL em partes
 * e converte as exceções da camada Service em respostas HTTP com corpo JSON.
 */
public abstract class ApiServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ApiServlet.class.getName());
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        String[] caminho = caminho(req);
        try {
            switch (req.getMethod()) {
                case "GET" -> get(req, resp, caminho);
                case "POST" -> post(req, resp, caminho);
                case "PUT" -> put(req, resp, caminho);
                case "DELETE" -> delete(req, resp, caminho);
                case "OPTIONS" -> resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                default -> throw new MetodoNaoPermitidoException();
            }
        } catch (ValidacaoException | JsonParseException e) {
            erro(resp, HttpServletResponse.SC_BAD_REQUEST, e instanceof JsonParseException ? "JSON inválido." : e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            erro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ConflitoException e) {
            erro(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (MetodoNaoPermitidoException e) {
            erro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método " + req.getMethod() + " não suportado em " + req.getRequestURI() + ".");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao processar " + req.getMethod() + " " + req.getRequestURI(), e);
            erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    protected void get(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        throw new MetodoNaoPermitidoException();
    }

    protected void post(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        throw new MetodoNaoPermitidoException();
    }

    protected void put(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        throw new MetodoNaoPermitidoException();
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp, String[] caminho) throws Exception {
        throw new MetodoNaoPermitidoException();
    }

    // ---- helpers de resposta ----

    protected void json(HttpServletResponse resp, int status, Object corpo) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(GSON.toJson(corpo));
    }

    protected void criado(HttpServletRequest req, HttpServletResponse resp, int id, Object corpo) throws IOException {
        resp.setHeader("Location", req.getRequestURL().toString().replaceAll("/+$", "") + "/" + id);
        json(resp, HttpServletResponse.SC_CREATED, corpo);
    }

    protected void semConteudo(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", status);
        corpo.put("erro", mensagem);
        json(resp, status, corpo);
    }

    // ---- helpers de requisição ----

    /** "/5/sugestoes" vira ["5", "sugestoes"]; sem caminho vira []. */
    private String[] caminho(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null) return new String[0];
        return Arrays.stream(info.split("/")).filter(s -> !s.isBlank()).toArray(String[]::new);
    }

    protected int id(String valor) {
        try {
            int id = Integer.parseInt(valor);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) {
            // cai na exceção abaixo
        }
        throw new ValidacaoException("Id inválido: " + valor + ".");
    }

    protected Integer filtroInteiro(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        if (valor == null || valor.isBlank()) return null;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("O parâmetro " + nome + " deve ser um número inteiro.");
        }
    }

    protected JsonObject corpo(HttpServletRequest req) throws IOException {
        JsonElement el = JsonParser.parseReader(req.getReader());
        if (el == null || !el.isJsonObject()) throw new ValidacaoException("O corpo da requisição deve ser um objeto JSON.");
        return el.getAsJsonObject();
    }

    protected String texto(JsonObject obj, String campo) {
        JsonElement el = obj.get(campo);
        if (el == null || el.isJsonNull()) throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        if (!el.isJsonPrimitive() || !el.getAsJsonPrimitive().isString()) {
            throw new ValidacaoException("O campo \"" + campo + "\" deve ser um texto.");
        }
        return el.getAsString();
    }

    protected int inteiro(JsonObject obj, String campo) {
        JsonElement el = obj.get(campo);
        if (el == null || el.isJsonNull()) throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        try {
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
                double valor = el.getAsDouble();
                if (valor == Math.rint(valor)) return (int) valor;
            }
        } catch (NumberFormatException ignored) {
            // cai na exceção abaixo
        }
        throw new ValidacaoException("O campo \"" + campo + "\" deve ser um número inteiro.");
    }

    private static class MetodoNaoPermitidoException extends RuntimeException {}
}
