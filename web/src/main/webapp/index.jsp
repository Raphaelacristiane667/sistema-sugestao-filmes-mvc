<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <title>CineSugere - Sistema de Sugestão de Filmes</title>
</head>
<body>
<%@ include file="/WEB-INF/views/includes/cabecalho.jspf" %>
<main id="conteudo" class="home">
  <section class="hero">
    <div>
      <span class="selo">Sugestões pela média das notas</span>
      <h1>Descubra o próximo filme <em>pela categoria</em> que você ama.</h1>
      <p>Escolha uma categoria, veja o ranking dos filmes mais bem avaliados e dê a sua nota de 0 a 10.</p>
      <div class="acoes">
        <a class="btn" href="${pageContext.request.contextPath}/filmes">Ver sugestões</a>
        <a class="btn sec" href="${pageContext.request.contextPath}/avaliacoes">Avaliar um filme</a>
      </div>
      <div class="numeros">
        <div class="numero"><strong id="qtd-filmes">–</strong><span>filmes</span></div>
        <div class="numero"><strong id="qtd-categorias">–</strong><span>categorias</span></div>
        <div class="numero"><strong id="qtd-avaliacoes">–</strong><span>avaliações</span></div>
      </div>
    </div>
    <div class="vitrine" id="vitrine" aria-label="Filmes em destaque"></div>
  </section>

  <section class="secao">
    <div class="secao-cabeca">
      <h2>Mais bem avaliados</h2>
      <a href="${pageContext.request.contextPath}/avaliacoes">Ver todas as avaliações →</a>
    </div>
    <div class="lista" id="top"><p class="vazio">Carregando…</p></div>
  </section>

  <section class="secao">
    <div class="secao-cabeca"><h2>Explore</h2></div>
    <div class="atalhos">
      <a class="atalho" href="${pageContext.request.contextPath}/filmes">
        <span class="ico" aria-hidden="true">🎬</span><strong>Filmes</strong>
        <span>Cadastre títulos e veja o ranking por categoria.</span>
      </a>
      <a class="atalho" href="${pageContext.request.contextPath}/categorias">
        <span class="ico" aria-hidden="true">🏷️</span><strong>Categorias</strong>
        <span>Organize os gêneros usados nas sugestões.</span>
      </a>
      <a class="atalho" href="${pageContext.request.contextPath}/avaliacoes">
        <span class="ico" aria-hidden="true">⭐</span><strong>Avaliações</strong>
        <span>Dê notas de 0 a 10 aos filmes.</span>
      </a>
      <a class="atalho" href="${applicationScope.apiUrl}/docs/">
        <span class="ico" aria-hidden="true">🧩</span><strong>API REST</strong>
        <span>Documentação Swagger dos endpoints JSON.</span>
      </a>
    </div>
  </section>
</main>
<%@ include file="/WEB-INF/views/includes/rodape.jspf" %>
<script>
  (async () => {
    const api = '${applicationScope.apiUrl}/';
    const get = (r) => fetch(api + r).then((res) => res.json());
    const el = (tag, cls, text) => {
      const e = document.createElement(tag);
      if (cls) e.className = cls;
      if (text !== undefined) e.textContent = text;
      return e;
    };
    try {
      const [filmes, categorias, avaliacoes] = await Promise.all([get('filmes'), get('categorias'), get('avaliacoes')]);
      document.getElementById('qtd-filmes').textContent = filmes.length;
      document.getElementById('qtd-categorias').textContent = categorias.length;
      document.getElementById('qtd-avaliacoes').textContent = avaliacoes.length;

      // Média geral de cada filme, juntando todas as categorias
      const notas = {};
      avaliacoes.forEach((a) => (notas[a.filmeId] ||= []).push(a.nota));
      const media = (id) => notas[id] ? notas[id].reduce((s, n) => s + n, 0) / notas[id].length : null;

      const vitrine = document.getElementById('vitrine');
      filmes.slice(0, 6).forEach((f) => {
        const p = el('a', 'poster');
        p.href = '${pageContext.request.contextPath}/filmes';
        p.style.setProperty('--h', (f.id * 67) % 360);
        const arte = el('div', 'poster-arte');
        arte.append(el('span', '', f.titulo.charAt(0)));
        if (media(f.id) !== null) arte.append(el('i', '', '★ ' + media(f.id).toFixed(1)));
        p.append(arte, el('b', '', f.titulo), el('small', '', f.anoLancamento));
        vitrine.append(p);
      });

      const top = document.getElementById('top');
      const ranking = filmes.filter((f) => media(f.id) !== null)
        .sort((a, b) => media(b.id) - media(a.id)).slice(0, 5);
      top.replaceChildren();
      if (!ranking.length) top.append(el('p', 'vazio', 'Nenhuma avaliação ainda.'));
      ranking.forEach((f, i) => {
        const item = el('article', 'item');
        const meio = el('div');
        const barra = el('div', 'barra');
        const fill = el('i');
        fill.style.width = (media(f.id) * 10) + '%';
        barra.append(fill);
        meio.append(el('b', '', f.titulo), el('small', '', ' · ' + f.anoLancamento), barra);
        item.append(el('span', 'pos', i + 1), meio, el('span', 'nota', media(f.id).toFixed(1)));
        top.append(item);
      });
    } catch (e) {
      document.getElementById('top').replaceChildren(el('p', 'vazio', 'Não foi possível carregar os dados da API.'));
    }
  })();
</script>
</body>
</html>
