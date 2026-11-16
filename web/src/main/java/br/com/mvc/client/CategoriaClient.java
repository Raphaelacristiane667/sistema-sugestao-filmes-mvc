package br.com.mvc.client;

import br.com.mvc.model.Categoria;
import br.com.mvc.model.Filme;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** Acesso aos endpoints /categorias da API. */
public class CategoriaClient {
    private static final Type LISTA = new TypeToken<List<Categoria>>() {}.getType();
    private static final Type LISTA_FILMES = new TypeToken<List<Filme>>() {}.getType();

    private final ApiClient api;

    public CategoriaClient() { this(new ApiClient()); }
    public CategoriaClient(ApiClient api) { this.api = api; }

    public List<Categoria> listar() throws ApiException {
        return api.get("/categorias", LISTA);
    }

    public Categoria buscar(int id) throws ApiException {
        return api.get("/categorias/" + id, Categoria.class);
    }

    /** Filmes avaliados na categoria, ordenados pela média das notas. */
    public List<Filme> sugerir(int categoriaId) throws ApiException {
        return api.get("/categorias/" + categoriaId + "/sugestoes", LISTA_FILMES);
    }

    public Categoria criar(String nome) throws ApiException {
        return api.post("/categorias", Map.of("nome", nome), Categoria.class);
    }

    public Categoria atualizar(int id, String nome) throws ApiException {
        return api.put("/categorias/" + id, Map.of("nome", nome), Categoria.class);
    }

    public void excluir(int id) throws ApiException {
        api.delete("/categorias/" + id);
    }
}
