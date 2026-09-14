<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <title>Sistema de Sugestão de Filmes</title>
</head>
<body>
<%@ include file="/WEB-INF/views/includes/cabecalho.jspf" %>
<main id="conteudo" class="home">
  <div>
    <h1>Sugestão de filmes pela média da categoria</h1>
    <p>Cadastre títulos, categorias e notas de 0 a 10. O ranking usa a média das avaliações.</p>
  </div>
  <div class="atalhos">
    <a class="atalho principal" href="${pageContext.request.contextPath}/filmes">
      <strong>Filmes e sugestões</strong>
      <span>Cadastre um título e veja o ranking por categoria.</span>
    </a>
    <a class="atalho" href="${pageContext.request.contextPath}/categorias">
      <strong>Categorias</strong>
      <span>Organize o catálogo.</span>
    </a>
    <a class="atalho" href="${pageContext.request.contextPath}/avaliacoes">
      <strong>Avaliações</strong>
      <span>Registre notas de 0 a 10.</span>
    </a>
  </div>
</main>
</body>
</html>
