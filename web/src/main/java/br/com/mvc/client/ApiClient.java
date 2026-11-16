package br.com.mvc.client;

import br.com.mvc.config.ApiConfig;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP genérico da API REST. Envia e recebe JSON e transforma respostas
 * de erro (4xx/5xx) em {@link ApiException} com a mensagem que a API devolveu.
 */
public class ApiClient {
    private static final Gson GSON = new Gson();

    private final String baseUrl;
    private final HttpClient http;

    public ApiClient() { this(ApiConfig.URL_INTERNA); }

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public <T> T get(String caminho, Type tipo) throws ApiException {
        return enviar(requisicao(caminho).GET().build(), tipo);
    }

    public <T> T post(String caminho, Object corpo, Type tipo) throws ApiException {
        return enviar(requisicao(caminho).POST(json(corpo)).build(), tipo);
    }

    public <T> T put(String caminho, Object corpo, Type tipo) throws ApiException {
        return enviar(requisicao(caminho).PUT(json(corpo)).build(), tipo);
    }

    public void delete(String caminho) throws ApiException {
        enviar(requisicao(caminho).DELETE().build(), null);
    }

    private HttpRequest.Builder requisicao(String caminho) {
        return HttpRequest.newBuilder(URI.create(baseUrl + caminho))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; charset=UTF-8");
    }

    private HttpRequest.BodyPublisher json(Object corpo) {
        return HttpRequest.BodyPublishers.ofString(GSON.toJson(corpo), StandardCharsets.UTF_8);
    }

    private <T> T enviar(HttpRequest req, Type tipo) throws ApiException {
        HttpResponse<String> resp;
        try {
            resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ApiException("Não foi possível conectar à API em " + baseUrl + ". Ela está rodando?", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A chamada à API foi interrompida.", e);
        }
        if (resp.statusCode() >= 400) {
            throw new ApiException(resp.statusCode(), mensagemDeErro(resp));
        }
        if (tipo == null || resp.body() == null || resp.body().isBlank()) return null;
        return GSON.fromJson(resp.body(), tipo);
    }

    /** A API responde erros como {"status": 400, "erro": "mensagem"}. */
    private static String mensagemDeErro(HttpResponse<String> resp) {
        try {
            JsonElement el = JsonParser.parseString(resp.body());
            if (el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("erro") && !obj.get("erro").isJsonNull()) return obj.get("erro").getAsString();
            }
        } catch (RuntimeException ignored) {
            // corpo não é JSON: usa a mensagem genérica abaixo
        }
        return "A API respondeu com o status " + resp.statusCode() + ".";
    }
}
