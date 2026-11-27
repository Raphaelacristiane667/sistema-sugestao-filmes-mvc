# Sistema de Sugestão de Filmes — API RESTful + Sistema Web (MVC Java)

Repositório: **sistema-sugestao-filmes-mvc**
https://github.com/Raphaelacristiane667/sistema-sugestao-filmes-mvc

Trabalho T2 de Aplicações para Internet: continuação do sistema de sugestão de filmes,
agora com uma **API RESTful completa** (JSON), CRUD das três entidades e separação em
camadas **Controller → Service → DAO**, usando **MySQL**.

## Tema

Sistema para sugerir filmes com base em categorias escolhidas pelos usuários. Os usuários
avaliam as sugestões com notas de 0 a 10 e o ranking de cada categoria usa a média das notas.
Tabelas: `filmes`, `categorias` e `avaliacoes`.

## Estrutura do repositório

A API e o sistema web são **dois projetos separados**, cada um com seu `pom.xml`, seu WAR e seu Dockerfile.

| Pasta | Projeto | Endereço |
|---|---|---|
| [`api/`](api) | **API RESTful** (JSON) em camadas Controller → Service → DAO → MySQL. Documentação Swagger e coleção do Postman. | http://localhost:8080/api |
| [`web/`](web) | **Sistema web** (JSP). Não acessa o banco: consome a API por HTTP. | http://localhost:8081/mvc/ |

```
Navegador ──► web (JSP + Servlets) ──HTTP/JSON──► api (Controller → Service → DAO) ──► MySQL
```

- Endpoints, regras de negócio e exemplos: [`api/README.md`](api/README.md)
- Swagger: http://localhost:8080/api/docs/ (arquivo [`api/src/main/webapp/docs/openapi.yaml`](api/src/main/webapp/docs/openapi.yaml))
- Postman: importe [`api/docs/sugestao-filmes.postman_collection.json`](api/docs/sugestao-filmes.postman_collection.json)
- Detalhes do sistema web: [`web/README.md`](web/README.md)

## Como executar

### Com Docker: cada projeto no seu container

A API e o MVC têm **cada um o seu `docker-compose.yml`** e sobem em containers separados.
O MVC não enxerga o banco: ele chama a API pela porta 8080, por HTTP.

| Projeto | Containers | Endereço |
|---|---|---|
| `api/` | `cinesugere-mysql` + `cinesugere-api` | http://localhost:8080/api/filmes (Swagger: http://localhost:8080/api/docs/) |
| `web/` (MVC) | `cinesugere-mvc` | http://localhost:8081/mvc/ |

Com o Docker Desktop aberto, suba **primeiro a API** e depois o MVC:

```bash
cd api
docker compose up --build -d

cd ../web
docker compose up --build -d

docker ps        # mostra os 3 containers, em dois projetos (cinesugere-api e cinesugere-mvc)
```

Para parar: `docker compose down` dentro de cada pasta (na `api/`, `docker compose down -v` apaga o banco).

Atalho opcional: `docker compose up --build -d` na raiz sobe os dois projetos de uma vez
(o `docker-compose.yml` da raiz só inclui `api/docker-compose.yml` e `web/docker-compose.yml`).

### Sem Docker (Windows, banco H2 local)

```powershell
powershell -ExecutionPolicy Bypass -File rodar-local.ps1
```

O script baixa JDK, Maven e Tomcat, compila os dois projetos e sobe tudo no mesmo Tomcat:
API em http://localhost:8080/api e telas em http://localhost:8080/mvc/.

## Testes

```bash
cd api && mvn test     # camada Service, com H2 em memória
cd web && mvn test     # cliente HTTP da API, com servidor falso
```
