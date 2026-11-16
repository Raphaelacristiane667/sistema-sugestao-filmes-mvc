<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
    <p>Use as categorias para filtrar o ranking de sugestões. Clique numa delas para ver os filmes mais bem avaliados.</p>
  </div>

  <c:if test="${not empty erro}">
    <p class="erro" role="alert"><c:out value="${erro}"/></p>
  </c:if>

  <div class="grade">
    <section class="painel">
      <h2>Cadastrar categoria</h2>
      <form method="post" action="${pageContext.request.contextPath}/categorias">
        <div class="campo">
          <label for="nome">Nome da categoria</label>
          <input id="nome" type="text" name="nome" required maxlength="100" placeholder="Ex.: Terror">
        </div>
        <button type="submit">Cadastrar categoria</button>
      </form>
    </section>

    <section class="painel">
      <h2>Categorias <small>${fn:length(categorias)} cadastradas</small></h2>
      <c:choose>
        <c:when test="${empty categorias}">
          <p class="vazio">Nenhuma categoria cadastrada.</p>
        </c:when>
        <c:otherwise>
          <div class="cards-cat">
            <c:forEach var="cat" items="${categorias}">
              <a class="card-cat" style="--h:${(cat.id * 53 + 20) % 360}"
                 href="${pageContext.request.contextPath}/filmes?categoriaId=${cat.id}">
                <span class="letra" aria-hidden="true"><c:out value="${fn:substring(cat.nome, 0, 1)}"/></span>
                <span><c:out value="${cat.nome}"/><small>Ver sugestões →</small></span>
              </a>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</main>
<%@ include file="/WEB-INF/views/includes/rodape.jspf" %>
</body>
</html>
