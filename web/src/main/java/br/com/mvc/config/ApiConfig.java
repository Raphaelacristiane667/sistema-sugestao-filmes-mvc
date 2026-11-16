package br.com.mvc.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Endereços da API REST que este sistema web consome.
 *
 * API_URL          usado pelo servidor (servlets) para chamar a API. Padrão: http://localhost:8080/api
 * API_URL_PUBLICA  usado pelo navegador (JavaScript da página inicial e link da documentação).
 *                  Padrão: o mesmo valor de API_URL.
 *
 * Cada chave é lida primeiro como propriedade do sistema (-DAPI_URL=...) e depois como variável de ambiente.
 */
@WebListener
public class ApiConfig implements ServletContextListener {
    public static final String URL_INTERNA = semBarraFinal(config("API_URL", "http://localhost:8080/api"));
    public static final String URL_PUBLICA = semBarraFinal(config("API_URL_PUBLICA", URL_INTERNA));

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Disponível nas JSPs como ${applicationScope.apiUrl}
        sce.getServletContext().setAttribute("apiUrl", URL_PUBLICA);
    }

    private static String config(String chave, String padrao) {
        String valor = System.getProperty(chave);
        if (valor == null || valor.isBlank()) valor = System.getenv(chave);
        return (valor == null || valor.isBlank()) ? padrao : valor.trim();
    }

    private static String semBarraFinal(String url) {
        return url.replaceAll("/+$", "");
    }
}
