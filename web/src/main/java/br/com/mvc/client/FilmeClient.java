package br.com.mvc.client;

import br.com.mvc.model.Filme;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** Acesso aos endpoints /filmes da API. */
public class FilmeClient {
    private static final Type LISTA = new TypeToken<List<Filme>>() {}.getType();

    private final ApiClient api;

    public FilmeClient() { this(new ApiClient()); }
    public FilmeClient(ApiClient api) { this.api = api; }

    public List<Filme> listar() throws ApiException {
        return api.get("/filmes", LISTA);
    }

    public Filme buscar(int id) throws ApiException {
        return api.get("/filmes/" + id, Filme.class);
    }

    public Filme criar(String titulo, int anoLancamento) throws ApiException {
        return api.post("/filmes", Map.of("titulo", titulo, "anoLancamento", anoLancamento), Filme.class);
    }

    public Filme atualizar(int id, String titulo, int anoLancamento) throws ApiException {
        return api.put("/filmes/" + id, Map.of("titulo", titulo, "anoLancamento", anoLancamento), Filme.class);
    }

    public void excluir(int id) throws ApiException {
        api.delete("/filmes/" + id);
    }
}
