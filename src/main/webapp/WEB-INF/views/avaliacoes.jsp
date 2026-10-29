<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <title>Avaliações - Sugestão de Filmes</title>
</head>
<body>
<%@ include file="/WEB-INF/views/includes/cabecalho.jspf" %>
<main id="conteudo" class="page">
  <div class="cabeca">
    <h1>Avaliações</h1>
    <p>A nota vai de 0 a 10 e entra na média usada nas sugestões.</p>
  </div>

  <c:if test="${not empty erro}">
    <p class="erro" role="alert"><c:out value="${erro}"/></p>
  </c:if>

  <div class="grade">
    <section class="painel">
      <h2>Registrar avaliação</h2>
      <form method="post" action="${pageContext.request.contextPath}/avaliacoes">
        <div class="campo">
          <label for="filmeId">Filme</label>
          <select id="filmeId" name="filmeId" required>
            <c:forEach var="f" items="${filmes}">
              <option value="${f.id}">${f.titulo}</option>
            </c:forEach>
          </select>
        </div>
        <div class="campo">
          <label for="categoriaId">Categoria</label>
          <select id="categoriaId" name="categoriaId" required>
            <c:forEach var="cat" items="${categorias}">
              <option value="${cat.id}">${cat.nome}</option>
            </c:forEach>
          </select>
        </div>
        <div class="campo">
          <label for="nota">Nota (0 a 10)</label>
          <input id="nota" type="number" name="nota" min="0" max="10" required>
        </div>
        <button type="submit">Registrar avaliação</button>
      </form>
    </section>

    <section class="painel">
      <h2>Lista</h2>
      <c:choose>
        <c:when test="${empty avaliacoes}">
          <p class="vazio">Nenhuma avaliação registrada.</p>
        </c:when>
        <c:otherwise>
          <table class="tabela">
            <tr><th>ID</th><th>Filme</th><th>Categoria</th><th>Nota</th></tr>
            <c:forEach var="a" items="${avaliacoes}">
              <tr>
                <td>${a.id}</td>
                <td>${a.filmeTitulo}</td>
                <td>${a.categoriaNome}</td>
                <td class="nota">${a.nota}</td>
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
