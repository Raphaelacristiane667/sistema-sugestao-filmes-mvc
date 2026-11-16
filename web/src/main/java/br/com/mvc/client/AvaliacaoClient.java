package br.com.mvc.client;

import br.com.mvc.model.Avaliacao;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** Acesso aos endpoints /avaliacoes da API. */
public class AvaliacaoClient {
    private static final Type LISTA = new TypeToken<List<Avaliacao>>() {}.getType();

    private final ApiClient api;

    public AvaliacaoClient() { this(new ApiClient()); }
    public AvaliacaoClient(ApiClient api) { this.api = api; }

    public List<Avaliacao> listar() throws ApiException {
        return api.get("/avaliacoes", LISTA);
    }

    public Avaliacao buscar(int id) throws ApiException {
        return api.get("/avaliacoes/" + id, Avaliacao.class);
    }

    public Avaliacao criar(int filmeId, int categoriaId, int nota) throws ApiException {
        return api.post("/avaliacoes", corpo(filmeId, categoriaId, nota), Avaliacao.class);
    }

    public Avaliacao atualizar(int id, int filmeId, int categoriaId, int nota) throws ApiException {
        return api.put("/avaliacoes/" + id, corpo(filmeId, categoriaId, nota), Avaliacao.class);
    }

    public void excluir(int id) throws ApiException {
        api.delete("/avaliacoes/" + id);
    }

    private static Map<String, Integer> corpo(int filmeId, int categoriaId, int nota) {
        return Map.of("filmeId", filmeId, "categoriaId", categoriaId, "nota", nota);
    }
}
