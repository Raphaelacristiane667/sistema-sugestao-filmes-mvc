/*
 * Front do CineSugere. Tudo aqui conversa com a API REST (/mvc/api/...) via fetch:
 * não há acesso direto ao banco. Cada tela é uma função render* chamada pelo roteador.
 */
(() => {
  'use strict';

  const conteudo = document.getElementById('conteudo');

  // ---------------------------------------------------------------- API

  async function api(metodo, caminho, corpo) {
    const opcoes = { method: metodo, headers: {} };
    if (corpo !== undefined) {
      opcoes.headers['Content-Type'] = 'application/json';
      opcoes.body = JSON.stringify(corpo);
    }
    const resp = await fetch('api' + caminho, opcoes);
    if (resp.status === 204) return null;
    const dados = await resp.json().catch(() => null);
    if (!resp.ok) throw new Error((dados && dados.erro) || 'Erro ' + resp.status + ' na API.');
    return dados;
  }

  // ---------------------------------------------------------------- utilitários

  function esc(texto) {
    return String(texto ?? '').replace(/[&<>"']/g, (c) => (
      { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }

  const PALAVRAS_PEQUENAS = new Set(['o', 'a', 'os', 'as', 'de', 'do', 'da', 'dos', 'das', 'e', 'the', 'of']);

  function iniciais(titulo) {
    const palavras = titulo.split(/\s+/).filter((p) => p && !PALAVRAS_PEQUENAS.has(p.toLowerCase()));
    const base = palavras.length ? palavras : titulo.split(/\s+/);
    return base.slice(0, 2).map((p) => p[0].toUpperCase()).join('');
  }

  /** Cor do "pôster" gerada a partir do id, para cada filme ter sempre a mesma. */
  function fundoPoster(id) {
    const h = (id * 67) % 360;
    return `background: linear-gradient(150deg, hsl(${h} 55% 42%), hsl(${(h + 40) % 360} 60% 18%));`;
  }

  function formatarNota(n) {
    return n == null ? '–' : Number(n).toFixed(1).replace('.', ',');
  }

  function estrelas(nota) {
    const cheias = Math.round((nota ?? 0) / 2);
    return `<span class="estrelas" aria-label="${formatarNota(nota)} de 10">${'★'.repeat(cheias)}<span class="vazia">${'★'.repeat(5 - cheias)}</span></span>`;
  }

  function toast(mensagem, tipo = 'ok') {
    const el = document.createElement('div');
    el.className = 'toast' + (tipo === 'erro' ? ' erro' : '');
    el.innerHTML = `${esc(mensagem)}`;
    document.getElementById('toasts').appendChild(el);
    setTimeout(() => el.remove(), 4200);
  }

  function opcoes(lista, valorSelecionado, rotulo, vazio) {
    const inicio = vazio ? `<option value="">${esc(vazio)}</option>` : '';
    return inicio + lista.map((item) =>
      `<option value="${item.id}" ${String(item.id) === String(valorSelecionado) ? 'selected' : ''}>${esc(rotulo(item))}</option>`
    ).join('');
  }

  // ---------------------------------------------------------------- modal

  const modal = document.getElementById('modal');
  const modalForm = document.getElementById('modal-form');
  const modalErro = document.getElementById('modal-erro');
  let aoConfirmar = null;

  function abrirModal({ titulo, corpo, botao = 'Salvar', perigo = false, onSubmit }) {
    document.getElementById('modal-titulo').textContent = titulo;
    document.getElementById('modal-corpo').innerHTML = corpo;
    const ok = document.getElementById('modal-ok');
    ok.textContent = botao;
    ok.className = 'btn' + (perigo ? ' btn-perigo' : '');
    modalErro.hidden = true;
    aoConfirmar = onSubmit;
    modal.showModal();
    const primeiro = modal.querySelector('.modal-corpo input, .modal-corpo select');
    if (primeiro) primeiro.focus();
  }

  modalForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!aoConfirmar) return;
    const ok = document.getElementById('modal-ok');
    ok.disabled = true;
    try {
      await aoConfirmar(new FormData(modalForm));
      modal.close();
    } catch (erro) {
      modalErro.textContent = erro.message;
      modalErro.hidden = false;
    } finally {
      ok.disabled = false;
    }
  });
  modal.querySelectorAll('[data-fechar]').forEach((b) => b.addEventListener('click', () => modal.close()));

  function confirmar(titulo, texto, acao) {
    abrirModal({ titulo, corpo: `<p>${texto}</p>`, botao: 'Excluir', perigo: true, onSubmit: acao });
  }

  function campoNota(valor) {
    return `
      <div class="campo">
        <label for="nota">Nota</label>
        <div class="nota-escolha">
          <input id="nota" name="nota" type="range" min="0" max="10" step="1" value="${valor}"
                 oninput="this.nextElementSibling.value = this.value">
          <output for="nota">${valor}</output>
        </div>
      </div>`;
  }

  // ---------------------------------------------------------------- telas

  async function renderInicio() {
    const [filmes, categorias, avaliacoes] = await Promise.all([
      api('GET', '/filmes'), api('GET', '/categorias'), api('GET', '/avaliacoes')]);

    const mediaGeral = avaliacoes.length
      ? avaliacoes.reduce((s, a) => s + a.nota, 0) / avaliacoes.length : null;
    const top = filmes.filter((f) => f.media != null)
      .sort((a, b) => b.media - a.media || b.totalAvaliacoes - a.totalAvaliacoes).slice(0, 5);
    const melhor = top[0];

    conteudo.innerHTML = `
      <section class="hero">
        <div>
          <h1>Descubra o próximo filme pela <em>nota de quem assistiu</em>.</h1>
          <p>Escolha uma categoria e veja o ranking pela média das avaliações. Avalie de 0 a 10 e ajude a montar as sugestões.</p>
          <div class="hero-acoes">
            <a class="btn" href="#/sugestoes">Ver sugestões</a>
            <a class="btn btn-fantasma" href="#/avaliacoes">Avaliar um filme</a>
          </div>
        </div>
        ${melhor ? `
          <div class="hero-destaque">
            <small>Mais bem avaliado</small>
            ${cartaoFilme(melhor)}
          </div>` : ''}
      </section>

      <section class="numeros" aria-label="Resumo">
        <div class="numero"><span>Filmes</span><strong>${filmes.length}</strong></div>
        <div class="numero"><span>Categorias</span><strong>${categorias.length}</strong></div>
        <div class="numero"><span>Avaliações</span><strong>${avaliacoes.length}</strong></div>
        <div class="numero destaque"><span>Média geral</span><strong>${formatarNota(mediaGeral)}</strong></div>
      </section>

      <div class="grade-2">
        <section class="painel">
          <div class="painel-topo"><h2>Top 5 geral</h2><a class="btn btn-fantasma btn-pequeno" href="#/sugestoes">Por categoria</a></div>
          ${top.length ? `<ol class="ranking">${top.map((f, i) => linhaRanking(f, i)).join('')}</ol>`
                       : '<p class="vazio">Nenhum filme avaliado ainda.</p>'}
        </section>
        <section class="painel">
          <div class="painel-topo"><h2>Avaliações recentes</h2><a class="btn btn-fantasma btn-pequeno" href="#/avaliacoes">Ver todas</a></div>
          ${avaliacoes.length ? `
            <div class="tabela-wrap"><table class="tabela">
              <tbody>${avaliacoes.slice(0, 6).map((a) => `
                <tr>
                  <td><strong>${esc(a.filmeTitulo)}</strong><br><span class="pill">${esc(a.categoriaNome)}</span></td>
                  <td>${estrelas(a.nota)}</td>
                  <td class="nota-pill">${a.nota}</td>
                </tr>`).join('')}
              </tbody>
            </table></div>` : '<p class="vazio">Nenhuma avaliação ainda.</p>'}
        </section>
      </div>`;
  }

  function cartaoFilme(f, comAcoes = false) {
    return `
      <article class="filme">
        <div class="poster" style="${fundoPoster(f.id)}">
          ${iniciais(f.titulo)}
          ${f.media != null
            ? `<span class="selo">★ ${formatarNota(f.media)}</span>`
            : '<span class="selo sem">sem nota</span>'}
        </div>
        ${comAcoes ? `
          <div class="filme-acoes">
            <a class="icone-btn" href="#/avaliacoes?filme=${f.id}" title="Avaliar" aria-label="Avaliar ${esc(f.titulo)}">★</a>
            <button class="icone-btn" data-editar-filme="${f.id}" title="Editar" aria-label="Editar ${esc(f.titulo)}">✎</button>
            <button class="icone-btn perigo" data-excluir-filme="${f.id}" title="Excluir" aria-label="Excluir ${esc(f.titulo)}">🗑</button>
          </div>` : ''}
        <div class="filme-info">
          <strong>${esc(f.titulo)}</strong>
          <small>${f.anoLancamento} · ${f.totalAvaliacoes} ${f.totalAvaliacoes === 1 ? 'avaliação' : 'avaliações'}</small>
        </div>
      </article>`;
  }

  function linhaRanking(f, i) {
    return `
      <li class="rank ${i < 3 ? 'top' + (i + 1) : ''}" style="animation-delay:${i * 60}ms">
        <span class="pos">${i + 1}º</span>
        <span class="mini-poster" style="${fundoPoster(f.id)}">${iniciais(f.titulo)}</span>
        <div class="rank-info">
          <strong>${esc(f.titulo)}</strong>
          <small>${f.anoLancamento} · ${f.totalAvaliacoes} ${f.totalAvaliacoes === 1 ? 'avaliação' : 'avaliações'}</small>
          <div class="barra"><i style="width:${(f.media / 10) * 100}%"></i></div>
        </div>
        <span class="nota-grande">${formatarNota(f.media)}</span>
      </li>`;
  }

  // ---- filmes

  const estadoFilmes = { busca: '', ordem: 'titulo' };

  async function renderFilmes() {
    const filmes = await api('GET', '/filmes');

    conteudo.innerHTML = `
      <div class="cabeca">
        <div>
          <h1>Filmes</h1>
          <p>${filmes.length} títulos no catálogo. Passe o mouse num filme para avaliar, editar ou excluir.</p>
        </div>
        <button class="btn" id="novo-filme">+ Novo filme</button>
      </div>
      <div class="busca" style="margin-bottom:20px">
        <input type="search" id="busca" placeholder="Buscar pelo título…" value="${esc(estadoFilmes.busca)}" aria-label="Buscar filme">
        <select id="ordem" aria-label="Ordenar">
          <option value="titulo">Ordem alfabética</option>
          <option value="media">Maior média</option>
          <option value="ano">Mais recentes</option>
        </select>
      </div>
      <div class="filmes" id="lista-filmes"></div>`;

    const ordem = document.getElementById('ordem');
    ordem.value = estadoFilmes.ordem;

    function desenhar() {
      const termo = estadoFilmes.busca.trim().toLowerCase();
      const lista = filmes.filter((f) => f.titulo.toLowerCase().includes(termo));
      if (estadoFilmes.ordem === 'media') lista.sort((a, b) => (b.media ?? -1) - (a.media ?? -1));
      if (estadoFilmes.ordem === 'ano') lista.sort((a, b) => b.anoLancamento - a.anoLancamento);
      document.getElementById('lista-filmes').innerHTML = lista.length
        ? lista.map((f) => cartaoFilme(f, true)).join('')
        : '<p class="vazio">Nenhum filme encontrado.</p>';
    }
    desenhar();

    document.getElementById('busca').addEventListener('input', (e) => { estadoFilmes.busca = e.target.value; desenhar(); });
    ordem.addEventListener('change', (e) => { estadoFilmes.ordem = e.target.value; desenhar(); });
    document.getElementById('novo-filme').addEventListener('click', () => formularioFilme());

    document.getElementById('lista-filmes').addEventListener('click', (e) => {
      const editar = e.target.closest('[data-editar-filme]');
      const excluir = e.target.closest('[data-excluir-filme]');
      if (editar) formularioFilme(filmes.find((f) => f.id === Number(editar.dataset.editarFilme)));
      if (excluir) {
        const f = filmes.find((x) => x.id === Number(excluir.dataset.excluirFilme));
        confirmar('Excluir filme', `Excluir <strong>${esc(f.titulo)}</strong>? Essa ação não pode ser desfeita.`, async () => {
          await api('DELETE', '/filmes/' + f.id);
          toast(`"${f.titulo}" foi excluído.`);
          renderFilmes();
        });
      }
    });
  }

  function formularioFilme(filme) {
    abrirModal({
      titulo: filme ? 'Editar filme' : 'Novo filme',
      corpo: `
        <div class="campo"><label for="titulo">Título</label>
          <input id="titulo" name="titulo" maxlength="150" required value="${esc(filme?.titulo)}"></div>
        <div class="campo"><label for="ano">Ano de lançamento</label>
          <input id="ano" name="anoLancamento" type="number" min="1888" max="2100" required value="${filme?.anoLancamento ?? ''}"></div>`,
      onSubmit: async (dados) => {
        const corpo = { titulo: dados.get('titulo'), anoLancamento: Number(dados.get('anoLancamento')) || null };
        if (filme) await api('PUT', '/filmes/' + filme.id, corpo);
        else await api('POST', '/filmes', corpo);
        toast(filme ? 'Filme atualizado.' : `"${corpo.titulo}" entrou no catálogo.`);
        renderFilmes();
      },
    });
  }

  // ---- sugestões

  async function renderSugestoes(params) {
    const categorias = await api('GET', '/categorias');
    const atual = Number(params.get('c')) || categorias[0]?.id;

    conteudo.innerHTML = `
      <div class="cabeca">
        <div>
          <h1>Sugestões por categoria</h1>
          <p>Os filmes avaliados na categoria escolhida, do maior para o menor pela média das notas.</p>
        </div>
      </div>
      <div class="chips" role="tablist" aria-label="Categorias">
        ${categorias.map((c) => `<button class="chip ${c.id === atual ? 'is-on' : ''}" role="tab"
            aria-selected="${c.id === atual}" data-categoria="${c.id}">${esc(c.nome)}</button>`).join('')}
      </div>
      <section class="painel" id="painel-ranking"><p class="carregando">Carregando…</p></section>`;

    document.querySelector('.chips').addEventListener('click', (e) => {
      const chip = e.target.closest('[data-categoria]');
      if (chip) location.hash = '#/sugestoes?c=' + chip.dataset.categoria;
    });

    if (!atual) {
      document.getElementById('painel-ranking').innerHTML = '<p class="vazio">Cadastre uma categoria primeiro.</p>';
      return;
    }
    const sugestoes = await api('GET', `/categorias/${atual}/sugestoes`);
    const nome = categorias.find((c) => c.id === atual)?.nome ?? '';
    document.getElementById('painel-ranking').innerHTML = `
      <div class="painel-topo">
        <h2>Ranking · ${esc(nome)}</h2>
        <a class="btn btn-pequeno" href="#/avaliacoes?categoria=${atual}">Avaliar nesta categoria</a>
      </div>
      ${sugestoes.length
        ? `<ol class="ranking">${sugestoes.map((f, i) => linhaRanking(f, i)).join('')}</ol>`
        : '<p class="vazio">Nenhum filme avaliado nesta categoria ainda. Que tal ser a primeira avaliação?</p>'}`;
  }

  // ---- avaliações

  const filtrosAvaliacoes = { filme: '', categoria: '' };

  async function renderAvaliacoes(params) {
    const [filmes, categorias] = await Promise.all([api('GET', '/filmes'), api('GET', '/categorias')]);
    const filmePre = params.get('filme') || '';
    const categoriaPre = params.get('categoria') || '';

    conteudo.innerHTML = `
      <div class="cabeca">
        <div>
          <h1>Avaliações</h1>
          <p>Dê uma nota de 0 a 10. A média de cada categoria define o ranking de sugestões.</p>
        </div>
      </div>
      <div class="grade-2" style="grid-template-columns: 0.8fr 1.2fr">
        <section class="painel">
          <h2>Nova avaliação</h2>
          <form id="form-avaliacao" novalidate>
            <div class="campo"><label for="av-filme">Filme</label>
              <select id="av-filme" name="filmeId" required>${opcoes(filmes, filmePre, (f) => `${f.titulo} (${f.anoLancamento})`, 'Escolha um filme')}</select></div>
            <div class="campo"><label for="av-categoria">Categoria</label>
              <select id="av-categoria" name="categoriaId" required>${opcoes(categorias, categoriaPre, (c) => c.nome, 'Escolha uma categoria')}</select></div>
            ${campoNota(8)}
            <button class="btn" type="submit" style="width:100%">Enviar avaliação</button>
          </form>
        </section>
        <section class="painel">
          <div class="painel-topo"><h2>Todas as avaliações</h2></div>
          <div class="busca" style="margin-bottom:12px">
            <select id="filtro-filme" aria-label="Filtrar por filme">${opcoes(filmes, filtrosAvaliacoes.filme, (f) => f.titulo, 'Todos os filmes')}</select>
            <select id="filtro-categoria" aria-label="Filtrar por categoria">${opcoes(categorias, filtrosAvaliacoes.categoria, (c) => c.nome, 'Todas as categorias')}</select>
          </div>
          <div id="lista-avaliacoes"><p class="carregando">Carregando…</p></div>
        </section>
      </div>`;

    let avaliacoes = [];

    async function carregarLista() {
      const q = new URLSearchParams();
      if (filtrosAvaliacoes.filme) q.set('filmeId', filtrosAvaliacoes.filme);
      if (filtrosAvaliacoes.categoria) q.set('categoriaId', filtrosAvaliacoes.categoria);
      avaliacoes = await api('GET', '/avaliacoes' + (q.toString() ? '?' + q : ''));
      document.getElementById('lista-avaliacoes').innerHTML = avaliacoes.length ? `
        <div class="tabela-wrap"><table class="tabela">
          <thead><tr><th>Filme</th><th>Categoria</th><th>Nota</th><th><span class="sr">Ações</span></th></tr></thead>
          <tbody>${avaliacoes.map((a) => `
            <tr>
              <td><strong>${esc(a.filmeTitulo)}</strong></td>
              <td><span class="pill">${esc(a.categoriaNome)}</span></td>
              <td><span class="nota-pill">${a.nota}</span> ${estrelas(a.nota)}</td>
              <td class="acoes">
                <button class="icone-btn" data-editar="${a.id}" aria-label="Editar avaliação">✎</button>
                <button class="icone-btn perigo" data-excluir="${a.id}" aria-label="Excluir avaliação">🗑</button>
              </td>
            </tr>`).join('')}
          </tbody>
        </table></div>` : '<p class="vazio">Nenhuma avaliação com esses filtros.</p>';
    }
    await carregarLista();

    document.getElementById('form-avaliacao').addEventListener('submit', async (e) => {
      e.preventDefault();
      const dados = new FormData(e.target);
      if (!dados.get('filmeId') || !dados.get('categoriaId')) {
        toast('Escolha o filme e a categoria antes de enviar.', 'erro');
        return;
      }
      try {
        const criada = await api('POST', '/avaliacoes', {
          filmeId: Number(dados.get('filmeId')) || null,
          categoriaId: Number(dados.get('categoriaId')) || null,
          nota: Number(dados.get('nota')),
        });
        toast(`Nota ${criada.nota} para "${criada.filmeTitulo}" em ${criada.categoriaNome}.`);
        await carregarLista();
      } catch (erro) {
        toast(erro.message, 'erro');
      }
    });

    document.getElementById('filtro-filme').addEventListener('change', (e) => { filtrosAvaliacoes.filme = e.target.value; carregarLista(); });
    document.getElementById('filtro-categoria').addEventListener('change', (e) => { filtrosAvaliacoes.categoria = e.target.value; carregarLista(); });

    document.getElementById('lista-avaliacoes').addEventListener('click', (e) => {
      const editar = e.target.closest('[data-editar]');
      const excluir = e.target.closest('[data-excluir]');
      if (editar) {
        const a = avaliacoes.find((x) => x.id === Number(editar.dataset.editar));
        abrirModal({
          titulo: 'Editar avaliação',
          corpo: `
            <div class="campo"><label for="ed-filme">Filme</label>
              <select id="ed-filme" name="filmeId">${opcoes(filmes, a.filmeId, (f) => f.titulo)}</select></div>
            <div class="campo"><label for="ed-categoria">Categoria</label>
              <select id="ed-categoria" name="categoriaId">${opcoes(categorias, a.categoriaId, (c) => c.nome)}</select></div>
            ${campoNota(a.nota)}`,
          onSubmit: async (dados) => {
            await api('PUT', '/avaliacoes/' + a.id, {
              filmeId: Number(dados.get('filmeId')),
              categoriaId: Number(dados.get('categoriaId')),
              nota: Number(dados.get('nota')),
            });
            toast('Avaliação atualizada.');
            await carregarLista();
          },
        });
      }
      if (excluir) {
        const a = avaliacoes.find((x) => x.id === Number(excluir.dataset.excluir));
        confirmar('Excluir avaliação', `Excluir a nota <strong>${a.nota}</strong> de <strong>${esc(a.filmeTitulo)}</strong> em ${esc(a.categoriaNome)}?`, async () => {
          await api('DELETE', '/avaliacoes/' + a.id);
          toast('Avaliação excluída.');
          await carregarLista();
        });
      }
    });
  }

  // ---- categorias

  async function renderCategorias() {
    const [categorias, avaliacoes] = await Promise.all([api('GET', '/categorias'), api('GET', '/avaliacoes')]);
    const contagem = {};
    avaliacoes.forEach((a) => { contagem[a.categoriaId] = (contagem[a.categoriaId] || 0) + 1; });

    conteudo.innerHTML = `
      <div class="cabeca">
        <div>
          <h1>Categorias</h1>
          <p>As categorias organizam as avaliações e o ranking de sugestões.</p>
        </div>
      </div>
      <section class="painel" style="margin-bottom:20px">
        <form id="form-categoria" class="busca" novalidate>
          <input name="nome" maxlength="100" placeholder="Nome da nova categoria (ex.: Terror)" aria-label="Nome da categoria" required>
          <button class="btn" type="submit">+ Adicionar</button>
        </form>
      </section>
      <div class="categorias">
        ${categorias.map((c) => `
          <div class="categoria">
            <div>
              <strong>${esc(c.nome)}</strong>
              <small>${contagem[c.id] || 0} ${(contagem[c.id] || 0) === 1 ? 'avaliação' : 'avaliações'}</small>
            </div>
            <div class="categoria-acoes">
              <a class="icone-btn" href="#/sugestoes?c=${c.id}" title="Ver ranking" aria-label="Ver ranking de ${esc(c.nome)}">🏆</a>
              <button class="icone-btn" data-editar="${c.id}" aria-label="Renomear ${esc(c.nome)}">✎</button>
              <button class="icone-btn perigo" data-excluir="${c.id}" aria-label="Excluir ${esc(c.nome)}">🗑</button>
            </div>
          </div>`).join('') || '<p class="vazio">Nenhuma categoria cadastrada.</p>'}
      </div>`;

    document.getElementById('form-categoria').addEventListener('submit', async (e) => {
      e.preventDefault();
      try {
        const criada = await api('POST', '/categorias', { nome: new FormData(e.target).get('nome') });
        toast(`Categoria "${criada.nome}" criada.`);
        renderCategorias();
      } catch (erro) {
        toast(erro.message, 'erro');
      }
    });

    document.querySelector('.categorias').addEventListener('click', (e) => {
      const editar = e.target.closest('[data-editar]');
      const excluir = e.target.closest('[data-excluir]');
      const id = editar ? editar.dataset.editar : excluir?.dataset.excluir;
      const cat = categorias.find((c) => c.id === Number(id));
      if (editar) {
        abrirModal({
          titulo: 'Renomear categoria',
          corpo: `<div class="campo"><label for="cat-nome">Nome</label><input id="cat-nome" name="nome" maxlength="100" value="${esc(cat.nome)}"></div>`,
          onSubmit: async (dados) => {
            await api('PUT', '/categorias/' + cat.id, { nome: dados.get('nome') });
            toast('Categoria atualizada.');
            renderCategorias();
          },
        });
      }
      if (excluir) {
        confirmar('Excluir categoria', `Excluir a categoria <strong>${esc(cat.nome)}</strong>?`, async () => {
          await api('DELETE', '/categorias/' + cat.id);
          toast(`Categoria "${cat.nome}" excluída.`);
          renderCategorias();
        });
      }
    });
  }

  // ---------------------------------------------------------------- roteador

  const rotas = {
    inicio: renderInicio,
    filmes: renderFilmes,
    sugestoes: renderSugestoes,
    avaliacoes: renderAvaliacoes,
    categorias: renderCategorias,
  };

  async function navegar() {
    const [caminho, query] = location.hash.replace(/^#\/?/, '').split('?');
    const nome = rotas[caminho] ? caminho : 'inicio';
    document.querySelectorAll('.nav [data-rota]').forEach((a) => {
      const ativa = a.dataset.rota === nome;
      a.classList.toggle('is-on', ativa);
      if (ativa) a.setAttribute('aria-current', 'page'); else a.removeAttribute('aria-current');
    });
    try {
      await rotas[nome](new URLSearchParams(query || ''));
    } catch (erro) {
      conteudo.innerHTML = `<section class="painel"><h2>Não foi possível falar com a API</h2>
        <p class="vazio">${esc(erro.message)}</p></section>`;
    }
  }

  window.addEventListener('hashchange', () => { navegar(); window.scrollTo(0, 0); });
  navegar();
})();
