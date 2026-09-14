<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <title>Categorias - Sugestão de Filmes</title>
</head>
<body>
<%@ include file="/WEB-INF/views/includes/cabecalho.jspf" %>
<main id="conteudo" class="page">
  <div class="cabeca">
    <h1>Categorias</h1>
    <p>Use as categorias para filtrar o ranking de sugestões.</p>
  </div>

  <div class="grade">
    <section class="painel">
      <h2>Cadastrar categoria</h2>
      <form method="post" action="${pageContext.request.contextPath}/categorias">
        <div class="campo">
          <label for="nome">Nome da categoria</label>
          <input id="nome" type="text" name="nome" required maxlength="100">
        </div>
        <button type="submit">Cadastrar categoria</button>
      </form>
    </section>

    <section class="painel">
      <h2>Lista</h2>
      <c:choose>
        <c:when test="${empty categorias}">
          <p class="vazio">Nenhuma categoria cadastrada.</p>
        </c:when>
        <c:otherwise>
          <table class="tabela">
            <tr><th>ID</th><th>Nome</th></tr>
            <c:forEach var="cat" items="${categorias}">
              <tr>
                <td>${cat.id}</td>
                <td>${cat.nome}</td>
              </tr>
            </c:forEach>
          </table>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</main>
</body>
</html>
