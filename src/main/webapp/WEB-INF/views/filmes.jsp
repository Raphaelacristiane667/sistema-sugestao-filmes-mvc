<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <title>Filmes - Sugestão de Filmes</title>
</head>
<body>
<%@ include file="/WEB-INF/views/includes/cabecalho.jspf" %>
<main id="conteudo" class="page">
  <div class="cabeca">
    <h1>Filmes e sugestões</h1>
    <p>Cadastre um título ou escolha uma categoria para ver o ranking pela média das notas.</p>
  </div>

  <c:if test="${not empty erro}">
    <p class="erro" role="alert"><c:out value="${erro}"/></p>
  </c:if>

  <div class="grade">
    <section class="painel">
      <h2>Cadastrar filme</h2>
      <form method="post" action="${pageContext.request.contextPath}/filmes">
        <div class="campo">
          <label for="titulo">Título</label>
          <input id="titulo" type="text" name="titulo" required maxlength="150">
        </div>
        <div class="campo">
          <label for="anoLancamento">Ano de lançamento</label>
          <input id="anoLancamento" type="number" name="anoLancamento" min="1888" max="2100" required>
        </div>
        <button type="submit">Cadastrar filme</button>
      </form>
    </section>

    <section class="painel">
      <h2>Sugerir por categoria</h2>
      <form method="get" action="${pageContext.request.contextPath}/filmes">
        <div class="campo">
          <label for="categoriaId">Categoria</label>
          <select id="categoriaId" name="categoriaId" required>
            <option value="">Escolha uma categoria</option>
            <c:forEach var="cat" items="${categorias}">
              <option value="${cat.id}" ${param.categoriaId == cat.id ? 'selected' : ''}>${cat.nome}</option>
            </c:forEach>
          </select>
        </div>
        <button type="submit">Mostrar sugestões</button>
      </form>

      <c:if test="${not empty sugestoes}">
        <div class="lista empilha">
          <c:forEach var="f" items="${sugestoes}" varStatus="st">
            <article class="item">
              <span class="pos">${st.count}</span>
              <div>
                <b>${f.titulo}</b><br>
                <small>${f.anoLancamento}</small>
              </div>
              <span class="nota"><fmt:formatNumber value="${f.media}" minFractionDigits="1" maxFractionDigits="1"/></span>
            </article>
          </c:forEach>
        </div>
      </c:if>
      <c:if test="${not empty param.categoriaId and empty sugestoes}">
        <p class="vazio">Nenhum filme avaliado nesta categoria ainda.</p>
      </c:if>
    </section>
  </div>

  <section class="painel empilha">
    <h2>Todos os filmes</h2>
    <c:choose>
      <c:when test="${empty filmes}">
        <p class="vazio">Nenhum filme cadastrado.</p>
      </c:when>
      <c:otherwise>
        <table class="tabela">
          <tr><th>ID</th><th>Título</th><th>Ano</th></tr>
          <c:forEach var="f" items="${filmes}">
            <tr>
              <td>${f.id}</td>
              <td>${f.titulo}</td>
              <td>${f.anoLancamento}</td>
            </tr>
          </c:forEach>
        </table>
      </c:otherwise>
    </c:choose>
  </section>
</main>
</body>
</html>
