# Sistema de Sugestão de Filmes (MVC Java)

Repositório acadêmico: **sistema-sugestao-filmes-mvc**  
Projeto de faculdade em arquitetura MVC (Model, View, Controller) com Java, JSP, JDBC e MySQL.

Projeto corrigido e simplificado para o tema **Sistema de Sugestão de Filmes**, usando:
- Java 17+
- Jakarta Servlet 6 / Tomcat 10.1
- JSP + JSTL
- JDBC + MySQL 8.4
- Docker Compose
- Arquitetura MVC (Model, DAO, Controller e View)

## Estrutura

`br.com.mvc.model` → Filme, Categoria e Avaliacao  
`br.com.mvc.dao` → acesso ao MySQL  
`br.com.mvc.controller` → Servlets  
`br.com.mvc.filter.ConnectionFactory` → conexão JDBC  
`src/main/webapp/WEB-INF/views` → JSPs

## Como executar

Neste PC o caminho mais simples e **sem Docker**:

```powershell
powershell -ExecutionPolicy Bypass -File rodar-local.ps1
```

O script baixa Java 17, Maven e Tomcat na pasta do usuario e sobe o sistema com banco local H2.

Depois acesse:

`http://localhost:8080/mvc/`

Se o Docker Desktop estiver instalado:

```bash
docker compose up --build -d
```

## Funcionalidades

1. Cadastro de filmes.
2. Cadastro de categorias.
3. Cadastro de avaliações de 0 a 10.
4. Sugestão de filmes por categoria, ordenada pela média das avaliações.

O projeto foi limpo para remover as classes antigas de usuários/perfis que estavam misturadas ao sistema de filmes.
