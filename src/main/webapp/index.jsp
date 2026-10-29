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
    <h1>Sugestão de filmes por categoria</h1>
    <p>Escolha uma categoria, veja as sugestões e avalie com notas de 0 a 10.</p>
  </div>
  <div class="atalhos">
    <a class="atalho principal" href="${pageContext.request.contextPath}/filmes">
      <strong>Filmes e sugestões</strong>
      <span>Escolha a categoria e veja o ranking pela média.</span>
    </a>
    <a class="atalho" href="${pageContext.request.contextPath}/categorias">
      <strong>Categorias</strong>
      <span>Cadastre as categorias usadas nas sugestões.</span>
    </a>
    <a class="atalho" href="${pageContext.request.contextPath}/avaliacoes">
      <strong>Avaliações</strong>
      <span>Dê nota às sugestões, de 0 a 10.</span>
    </a>
    <a class="atalho" href="${pageContext.request.contextPath}/docs/">
      <strong>API REST</strong>
      <span>Documentação Swagger dos endpoints JSON.</span>
    </a>
  </div>
</main>
</body>
</html>
