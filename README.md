# Sistema de Sugestão de Filmes — API RESTful (MVC Java)

Repositório: **sistema-sugestao-filmes-mvc**
https://github.com/Raphaelacristiane667/sistema-sugestao-filmes-mvc

Trabalho T2 de Aplicações para Internet: continuação do sistema de sugestão de filmes,
agora com uma **API RESTful completa** (JSON), CRUD das três entidades e separação em
camadas **Controller → Service → DAO**, usando **MySQL**.

## Tema

Sistema para sugerir filmes com base em categorias escolhidas pelos usuários. Os usuários
avaliam as sugestões com notas de 0 a 10 e o ranking de cada categoria usa a média das notas.
Tabelas: `filmes`, `categorias` e `avaliacoes`.

## Tecnologias

- Java 17+, Jakarta Servlet 6, Tomcat 10.1
- JSON com Gson
- JDBC + MySQL 8.4 (Docker) ou H2 (execução local sem Docker e testes)
- JSP + JSTL para as telas web
- JUnit 4 para os testes da camada Service
- OpenAPI 3 / Swagger UI e coleção do Postman para a documentação

## Arquitetura (MVC em camadas)

```
Requisição HTTP
   │
   ▼
Controller   br.com.mvc.controller.api   recebe a requisição, lê/escreve JSON, devolve o status HTTP
   │         br.com.mvc.controller       telas web (JSP) que usam os mesmos services
   ▼
Service      br.com.mvc.service          regras de negócio e validações
   │
   ▼
DAO          br.com.mvc.dao              SQL com PreparedStatement (JDBC)
   │
   ▼
MySQL
```

| Pacote | Conteúdo |
|---|---|
| `br.com.mvc.model` | Entidades `Filme`, `Categoria`, `Avaliacao` |
| `br.com.mvc.dao` | `FilmeDAO`, `CategoriaDAO`, `AvaliacaoDAO` (CRUD + consultas) |
| `br.com.mvc.service` | `FilmeService`, `CategoriaService`, `AvaliacaoService` |
| `br.com.mvc.controller.api` | `FilmeApiController`, `CategoriaApiController`, `AvaliacaoApiController` e a base `ApiServlet` |
| `br.com.mvc.controller` | Servlets das telas JSP |
| `br.com.mvc.exception` | `ValidacaoException` (400), `RecursoNaoEncontradoException` (404), `ConflitoException` (409) |
| `br.com.mvc.config` | `ConnectionFactory` (conexão JDBC) e `DatabaseBootstrap` (cria tabelas e dados de exemplo) |
| `br.com.mvc.filter` | `EncodingFilter` (UTF-8) |
| `src/main/webapp` | JSPs, CSS e a documentação Swagger (`/docs`) |

### Regras de negócio (camada Service)

- **Filme:** título obrigatório (até 150 caracteres); ano de lançamento entre 1888 e o ano atual + 5.
- **Categoria:** nome obrigatório (até 100 caracteres) e único, sem diferenciar maiúsculas de minúsculas.
- **Avaliação:** nota de 0 a 10; o filme e a categoria informados precisam existir.
- Não é possível excluir um filme ou uma categoria que tenha avaliações (responde 409).
- **Sugestões:** filmes avaliados na categoria, ordenados pela média das notas (maior primeiro).

## Endpoints

Base: `http://localhost:8080/mvc/api`. Corpo e respostas em JSON.

A documentação interativa (Swagger UI) fica em **http://localhost:8080/mvc/docs/** e a
especificação em [`src/main/webapp/docs/openapi.yaml`](src/main/webapp/docs/openapi.yaml).
Para o Postman, importe [`docs/sugestao-filmes.postman_collection.json`](docs/sugestao-filmes.postman_collection.json).

### Filmes

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/filmes` | Lista todos os filmes | 200 |
| GET | `/filmes/{id}` | Busca um filme | 200 |
| POST | `/filmes` | Cadastra um filme | 201 |
| PUT | `/filmes/{id}` | Atualiza um filme | 200 |
| DELETE | `/filmes/{id}` | Exclui um filme (sem avaliações) | 204 |

Corpo de POST/PUT:

```json
{ "titulo": "Duna", "anoLancamento": 2021 }
```

### Categorias

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/categorias` | Lista todas as categorias | 200 |
| GET | `/categorias/{id}` | Busca uma categoria | 200 |
| GET | `/categorias/{id}/sugestoes` | Filmes da categoria ordenados pela média | 200 |
| POST | `/categorias` | Cadastra uma categoria | 201 |
| PUT | `/categorias/{id}` | Atualiza uma categoria | 200 |
| DELETE | `/categorias/{id}` | Exclui uma categoria (sem avaliações) | 204 |

Corpo de POST/PUT:

```json
{ "nome": "Terror" }
```

Exemplo de resposta de `GET /categorias/5/sugestoes`:

```json
[
  { "id": 5, "titulo": "Interestelar", "anoLancamento": 2014, "media": 10.0 },
  { "id": 1, "titulo": "Matrix", "anoLancamento": 1999, "media": 9.0 }
]
```

### Avaliações

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/avaliacoes` | Lista as avaliações (filtros opcionais `?filmeId=` e `?categoriaId=`) | 200 |
| GET | `/avaliacoes/{id}` | Busca uma avaliação | 200 |
| POST | `/avaliacoes` | Registra uma avaliação | 201 |
| PUT | `/avaliacoes/{id}` | Atualiza uma avaliação | 200 |
| DELETE | `/avaliacoes/{id}` | Exclui uma avaliação | 204 |

Corpo de POST/PUT:

```json
{ "filmeId": 1, "categoriaId": 5, "nota": 8 }
```

Resposta (o título do filme e o nome da categoria vêm junto):

```json
{ "id": 6, "filmeId": 1, "categoriaId": 5, "nota": 8, "filmeTitulo": "Matrix", "categoriaNome": "Ficção Científica" }
```

### Erros

Todas as respostas de erro têm o mesmo formato:

```json
{ "status": 400, "erro": "A nota deve estar entre 0 e 10." }
```

| Status | Quando |
|---|---|
| 400 | JSON inválido, campo obrigatório ausente, tipo errado ou regra de validação |
| 404 | Id não encontrado |
| 405 | Método HTTP não suportado na rota |
| 409 | Categoria com nome repetido, ou exclusão de filme/categoria que tem avaliações |
| 500 | Erro inesperado no servidor |

O `POST` devolve o cabeçalho `Location` com a URL do recurso criado.

### Testando com curl

```bash
curl http://localhost:8080/mvc/api/filmes
curl -X POST http://localhost:8080/mvc/api/filmes -H "Content-Type: application/json" -d '{"titulo":"Duna","anoLancamento":2021}'
curl -X PUT http://localhost:8080/mvc/api/filmes/6 -H "Content-Type: application/json" -d '{"titulo":"Duna: Parte Um","anoLancamento":2021}'
curl -X DELETE http://localhost:8080/mvc/api/filmes/6
curl http://localhost:8080/mvc/api/categorias/5/sugestoes
```

## Como executar

### Com Docker (MySQL 8.4 + Tomcat)

Com o Docker Desktop aberto, na pasta do projeto:

```bash
docker compose up --build -d
```

- Telas web: http://localhost:8080/mvc/
- API: http://localhost:8080/mvc/api/filmes
- Swagger: http://localhost:8080/mvc/docs/

O MySQL é criado com o `init.sql` (tabelas e dados de exemplo).
Para começar do zero: `docker compose down -v`.

### Sem Docker (Windows, banco H2 local)

```powershell
powershell -ExecutionPolicy Bypass -File rodar-local.ps1
```

O script baixa JDK, Maven e Tomcat, compila o WAR e sobe em http://localhost:8080/mvc/.

### Configuração do banco

A `ConnectionFactory` lê estas variáveis de ambiente (ou propriedades `-D`):

| Variável | Padrão | Uso |
|---|---|---|
| `DB_MODE` | `mysql` | `mysql` ou `h2` |
| `DB_HOST` | `mysql` | Host do MySQL |
| `DB_URL` | — | URL JDBC completa (sobrescreve as anteriores) |
| `DB_USER` / `DB_PASSWORD` | `mvc_user` / `mvc123` | Credenciais |

## Testes

```bash
mvn test
```

Os testes da camada Service rodam num banco H2 em memória, sem precisar do MySQL.
