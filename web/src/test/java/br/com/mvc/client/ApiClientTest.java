package br.com.mvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import br.com.mvc.model.Filme;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Testa o cliente contra um servidor HTTP falso que imita as respostas da API. */
public class ApiClientTest {
    private HttpServer servidor;
    private String url;

    @Before
    public void subirServidorFalso() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/api/filmes", troca -> {
            String corpo;
            int status;
            if ("POST".equals(troca.getRequestMethod())) {
                status = 400;
                corpo = "{\"status\":400,\"erro\":\"O título é obrigatório.\"}";
            } else {
                status = 200;
                corpo = "[{\"id\":1,\"titulo\":\"Matrix\",\"anoLancamento\":1999}]";
            }
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            troca.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            troca.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = troca.getResponseBody()) { out.write(bytes); }
        });
        servidor.start();
        url = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/api";
    }

    @After
    public void pararServidor() {
        servidor.stop(0);
    }

    @Test
    public void listaFilmesLendoOJsonDaApi() throws Exception {
        List<Filme> filmes = new FilmeClient(new ApiClient(url)).listar();
        assertEquals(1, filmes.size());
        assertEquals("Matrix", filmes.get(0).getTitulo());
        assertEquals(1999, filmes.get(0).getAnoLancamento());
    }

    @Test
    public void erroDaApiViraApiExceptionComAMensagemDela() {
        try {
            new FilmeClient(new ApiClient(url)).criar("", 2000);
            fail("Deveria lançar ApiException");
        } catch (ApiException e) {
            assertEquals(400, e.getStatus());
            assertEquals("O título é obrigatório.", e.getMessage());
            assertTrue(e.isErroDoUsuario());
        }
    }

    @Test
    public void apiForaDoArAvisaQueNaoConseguiuConectar() {
        servidor.stop(0);
        try {
            new FilmeClient(new ApiClient(url)).listar();
            fail("Deveria lançar ApiException");
        } catch (ApiException e) {
            assertEquals(ApiException.API_INDISPONIVEL, e.getStatus());
            assertTrue(e.getMessage().startsWith("Não foi possível conectar à API"));
        }
    }
}
