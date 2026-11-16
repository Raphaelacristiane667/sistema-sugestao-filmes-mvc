<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
              <option value="${f.id}"><c:out value="${f.titulo}"/></option>
            </c:forEach>
          </select>
        </div>
        <div class="campo">
          <label for="categoriaId">Categoria</label>
          <select id="categoriaId" name="categoriaId" required>
            <c:forEach var="cat" items="${categorias}">
              <option value="${cat.id}"><c:out value="${cat.nome}"/></option>
            </c:forEach>
          </select>
        </div>
        <fieldset class="campo">
          <legend>Nota (0 a 10)</legend>
          <div class="notas">
            <c:forEach var="n" begin="0" end="10">
              <input type="radio" id="nota${n}" name="nota" value="${n}" required ${n == 8 ? 'checked' : ''}>
              <label for="nota${n}">${n}</label>
            </c:forEach>
          </div>
        </fieldset>
        <button type="submit">Registrar avaliação</button>
      </form>
    </section>

    <section class="painel">
      <h2>Últimas avaliações <small>${fn:length(avaliacoes)} no total</small></h2>
      <c:choose>
        <c:when test="${empty avaliacoes}">
          <p class="vazio">Nenhuma avaliação registrada.</p>
        </c:when>
        <c:otherwise>
          <div class="avals">
            <c:forEach var="a" items="${avaliacoes}">
              <article class="aval">
                <span class="mini" style="--h:${(a.filmeId * 67) % 360}" aria-hidden="true"><c:out value="${fn:substring(a.filmeTitulo, 0, 1)}"/></span>
                <div>
                  <b><c:out value="${a.filmeTitulo}"/></b><br>
                  <span class="chip" style="--h:${(a.categoriaId * 53 + 20) % 360}"><c:out value="${a.categoriaNome}"/></span>
                  <div class="barra"><i style="width:${a.nota * 10}%"></i></div>
                </div>
                <span class="nota">${a.nota}</span>
              </article>
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
