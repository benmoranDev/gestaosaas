let carrinho = [];

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor || 0);
}

function adicionarProduto(id, nome, preco, estoque, descricao) {
    const itemExistente = carrinho.find(item => item.produtoId === id);

    if (itemExistente) {
        if (itemExistente.quantidade >= estoque) {
            alert('Quantidade máxima em estoque atingida para este produto.');
            return;
        }
        itemExistente.quantidade += 1;
    } else {
        carrinho.push({
            produtoId: id,
            nome: nome,
            preco: Number(preco),
            quantidade: 1,
            estoque: Number(estoque),
            descricao: descricao || ''
        });
    }

    renderizarCarrinho();
}

function alterarQuantidade(produtoId, delta) {
    const item = carrinho.find(i => i.produtoId === produtoId);
    if (!item) return;

    const novaQuantidade = item.quantidade + delta;

    if (novaQuantidade <= 0) {
        carrinho = carrinho.filter(i => i.produtoId !== produtoId);
    } else if (novaQuantidade <= item.estoque) {
        item.quantidade = novaQuantidade;
    } else {
        alert('Estoque insuficiente para aumentar a quantidade.');
    }

    renderizarCarrinho();
}

function removerItem(produtoId) {
    carrinho = carrinho.filter(i => i.produtoId !== produtoId);
    renderizarCarrinho();
}

function limparCarrinho() {
    carrinho = [];
    document.getElementById('valorRecebido').value = '';
    renderizarCarrinho();
}

function renderizarCarrinho() {
    const lista = document.getElementById('listaCarrinho');

    if (carrinho.length === 0) {
        lista.innerHTML = `
            <div class="carrinho-vazio" id="estadoVazio">
                <i class="bi bi-cart3 carrinho-vazio-icone"></i>
                <div>Nenhum item adicionado ao carrinho.</div>
            </div>
        `;
    } else {
        lista.innerHTML = carrinho.map(item => {
            const subtotal = item.preco * item.quantidade;

            return `
                <div class="item-carrinho">
                    <div>
                        <h4>${item.nome}</h4>
                        <p>${formatarMoeda(item.preco)} por unidade</p>

                        <div class="item-acoes">
                            <button type="button" onclick="alterarQuantidade(${item.produtoId}, -1)">-</button>
                            <strong>${item.quantidade}</strong>
                            <button type="button" onclick="alterarQuantidade(${item.produtoId}, 1)">+</button>
                        </div>
                    </div>

                    <div class="item-lado-direito">
                        <strong>${formatarMoeda(subtotal)}</strong>
                        <button type="button" class="item-remove" onclick="removerItem(${item.produtoId})">
                            Remover
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    atualizarResumo();
}

function atualizarResumo() {
    const quantidadeItens = carrinho.reduce((acc, item) => acc + item.quantidade, 0);
    const total = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);

    document.getElementById('badgeItens').textContent = quantidadeItens;
    document.getElementById('resumoQuantidadeItens').textContent = quantidadeItens;
    document.getElementById('valorTotalVenda').textContent = formatarMoeda(total);
    document.getElementById('resumoSubtotal').textContent = formatarMoeda(total);

    calcularTroco();
}

function obterFormaPagamentoSelecionada() {
    const selecionado = document.querySelector('input[name="formaPagamento"]:checked');
    return selecionado ? selecionado.value : null;
}

function atualizarPagamento() {
    const formaPagamento = obterFormaPagamentoSelecionada();
    const bloco = document.getElementById('blocoValorRecebido');
    const inputValorRecebido = document.getElementById('valorRecebido');

    if (formaPagamento === 'DINHEIRO') {
        bloco.style.display = 'block';
    } else {
        bloco.style.display = 'none';
        inputValorRecebido.value = '';
    }

    calcularTroco();
}

function calcularTroco() {
    const formaPagamento = obterFormaPagamentoSelecionada();
    const valorRecebido = parseFloat(document.getElementById('valorRecebido').value || '0');
    const total = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);

    let troco = 0;

    if (formaPagamento === 'DINHEIRO' && valorRecebido > total) {
        troco = valorRecebido - total;
    }

    document.getElementById('trocoVenda').textContent = formatarMoeda(troco);
}

function prepararEnvio() {
    if (carrinho.length === 0) {
        alert('Adicione pelo menos um item ao carrinho.');
        return false;
    }

    const itensParaEnvio = carrinho.map(item => ({
        produtoId: item.produtoId,
        quantidade: item.quantidade
    }));

    document.getElementById('itensJson').value = JSON.stringify(itensParaEnvio);

    const formaPagamento = obterFormaPagamentoSelecionada();
    const valorRecebido = document.getElementById('valorRecebido').value;

    if (formaPagamento === 'DINHEIRO' && (!valorRecebido || Number(valorRecebido) <= 0)) {
        alert('Informe o valor recebido para pagamento em dinheiro.');
        return false;
    }

    return true;
}

function filtrarProdutos() {
    const termo = document.getElementById('buscaProduto').value.toLowerCase();
    const produtos = document.querySelectorAll('.produto-card');

    produtos.forEach(produto => {
        const nome = (produto.dataset.nome || '').toLowerCase();
        const descricao = (produto.dataset.descricao || '').toLowerCase();
        const visivel = nome.includes(termo) || descricao.includes(termo);
        produto.style.display = visivel ? 'flex' : 'none';
    });
}

document.addEventListener("DOMContentLoaded", function () {
    atualizarPagamento();
    renderizarCarrinho();
});