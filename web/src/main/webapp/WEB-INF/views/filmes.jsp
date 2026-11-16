<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
    <p>Escolha uma categoria para ver o ranking pela média das notas, ou cadastre um novo título.</p>
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
          <input id="titulo" type="text" name="titulo" required maxlength="150" placeholder="Ex.: Pulp Fiction">
        </div>
        <div class="campo">
          <label for="anoLancamento">Ano de lançamento</label>
          <input id="anoLancamento" type="number" name="anoLancamento" min="1888" max="2100" required placeholder="Ex.: 1994">
        </div>
        <button type="submit">Cadastrar filme</button>
      </form>
    </section>

    <section class="painel">
      <h2>Sugerir por categoria</h2>
      <nav class="chips" aria-label="Categorias">
        <c:forEach var="cat" items="${categorias}">
          <a class="chip ${param.categoriaId == cat.id ? 'is-on' : ''}" style="--h:${(cat.id * 53 + 20) % 360}"
             href="${pageContext.request.contextPath}/filmes?categoriaId=${cat.id}"><c:out value="${cat.nome}"/></a>
        </c:forEach>
      </nav>

      <c:if test="${not empty sugestoes}">
        <div class="lista">
          <c:forEach var="f" items="${sugestoes}" varStatus="st">
            <article class="item">
              <span class="pos">${st.count}</span>
              <div>
                <b><c:out value="${f.titulo}"/></b> <small>· ${f.anoLancamento}</small>
                <div class="barra"><i style="width:${f.media * 10}%"></i></div>
              </div>
              <span class="nota"><fmt:formatNumber value="${f.media}" minFractionDigits="1" maxFractionDigits="1"/></span>
            </article>
          </c:forEach>
        </div>
      </c:if>
      <c:if test="${empty param.categoriaId}">
        <p class="vazio empilha">Clique numa categoria acima para ver as sugestões.</p>
      </c:if>
      <c:if test="${not empty param.categoriaId and empty sugestoes}">
        <p class="vazio empilha">Nenhum filme avaliado nesta categoria ainda.
          <a href="${pageContext.request.contextPath}/avaliacoes">Avalie um filme</a>.</p>
      </c:if>
    </section>
  </div>

  <section class="painel empilha">
    <h2>Todos os filmes <small>${fn:length(filmes)} no catálogo</small></h2>
    <c:choose>
      <c:when test="${empty filmes}">
        <p class="vazio">Nenhum filme cadastrado.</p>
      </c:when>
      <c:otherwise>
        <div class="posters">
          <c:forEach var="f" items="${filmes}">
            <article class="poster" style="--h:${(f.id * 67) % 360}">
              <div class="poster-arte" aria-hidden="true"><span><c:out value="${fn:substring(f.titulo, 0, 1)}"/></span></div>
              <b title="${fn:escapeXml(f.titulo)}"><c:out value="${f.titulo}"/></b>
              <small>${f.anoLancamento} · #${f.id}</small>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<%@ include file="/WEB-INF/views/includes/rodape.jspf" %>
</body>
</html>
