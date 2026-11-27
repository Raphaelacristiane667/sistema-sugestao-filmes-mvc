# Sistema Web de Sugestão de Filmes (cliente da API)

Telas em JSP (MVC com Servlets) que **não acessam o banco**: todos os dados vêm da
[API REST](../api) por HTTP, em JSON.

```
Navegador ──► Servlet (controller) ──► Client (HTTP + JSON) ──► API REST ──► MySQL
                    │
                    ▼
                  JSP (view)
```

| Pacote | Conteúdo |
|---|---|
| `br.com.mvc.controller` | Servlets das telas (`FilmeServlet`, `CategoriaServlet`, `AvaliacaoServlet`) e a base `TelaServlet` |
| `br.com.mvc.client` | `ApiClient` (HTTP genérico) e `FilmeClient`, `CategoriaClient`, `AvaliacaoClient` |
| `br.com.mvc.model` | `Filme`, `Categoria`, `Avaliacao` (o JSON da API vira esses objetos) |
| `br.com.mvc.config` | `ApiConfig`: endereço da API |
| `src/main/webapp` | JSPs e CSS |

Quando a API recusa um cadastro (por exemplo, nota fora de 0 a 10 ou categoria repetida),
a mensagem de erro que ela devolve aparece na própria tela.

## Configuração

| Variável | Padrão | Uso |
|---|---|---|
| `API_URL` | `http://localhost:8080/api` (no Docker: `http://host.docker.internal:8080/api`) | Endereço da API usado pelos servlets |
| `API_URL_PUBLICA` | igual a `API_URL` | Endereço da API usado pelo navegador (página inicial e link da documentação) |

## Executar

### Com Docker (container próprio)

Com a API já rodando (`cd ../api && docker compose up --build -d`), nesta pasta:

```bash
docker compose up --build -d
```

Sobe só o container `cinesugere-mvc`, em http://localhost:8081/mvc/.
Ele não tem banco: chama a API em `http://host.docker.internal:8080/api`
(a porta 8080 publicada pelo container da API). Para apontar para outra API,
defina `API_URL` antes do comando.

### Com Maven

```bash
cd web
mvn package          # gera target/mvc.war
```

Copie `target/mvc.war` para o `webapps` de um Tomcat 10.1 com a API rodando.

## Testes

```bash
cd web
mvn test
```

O `ApiClientTest` usa um servidor HTTP falso, então não precisa da API no ar.
