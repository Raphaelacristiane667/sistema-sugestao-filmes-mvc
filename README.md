# Sistema de Sugestão de Filmes (MVC Java)

Repositório: **sistema-sugestao-filmes-mvc**  
https://github.com/Raphaelacristiane667/sistema-sugestao-filmes-mvc

## Enunciado

Sistema para sugerir filmes baseado em categorias escolhidas pelos usuários. Eles podem avaliar as sugestões com notas. Tabelas: Filmes, Categorias, Avaliações.

## O que o sistema faz

1. Cadastra filmes, categorias e avaliações.
2. O usuário escolhe uma categoria e recebe sugestões de filmes, ordenadas pela média das notas.
3. O usuário avalia as sugestões com notas de 0 a 10.

## Tecnologias

- Java 17+, Jakarta Servlet 6, Tomcat 10.1
- JSP + JSTL
- JDBC + MySQL 8.4 (Docker) ou H2 (execução local)
- Arquitetura MVC (Model, DAO, Controller, View)

## Estrutura

`br.com.mvc.model` → Filme, Categoria e Avaliacao  
`br.com.mvc.dao` → acesso ao banco  
`br.com.mvc.controller` → Servlets  
`br.com.mvc.filter.ConnectionFactory` → conexão JDBC  
`src/main/webapp/WEB-INF/views` → JSPs

## Como executar

Na pasta do projeto:

```powershell
powershell -ExecutionPolicy Bypass -File rodar-local.ps1
```

Acesse: `http://localhost:8080/mvc/`

Com Docker Desktop (engine running):

```bash
docker compose up --build -d
```
