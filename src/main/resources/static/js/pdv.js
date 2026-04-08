document.addEventListener("DOMContentLoaded", function () {
    // Estado
    let carrinho = [];
    let feedbackTimeout;
    let scanBuffer = "";
    let lastScanTime = 0;

    // Elementos
    const buscaProduto = document.getElementById("buscaProduto");
    const codigoBarrasInput = document.getElementById("codigoBarrasInput");
    const gridProdutos = document.getElementById("gridProdutos");
    const listaCarrinho = document.getElementById("listaCarrinho");
    const badgeItens = document.getElementById("badgeItens");
    const resumoQuantidadeItens = document.getElementById("resumoQuantidadeItens");
    const valorTotalVenda = document.getElementById("valorTotalVenda");
    const resumoSubtotal = document.getElementById("resumoSubtotal");
    const trocoVenda = document.getElementById("trocoVenda");
    const valorRecebidoInput = document.getElementById("valorRecebido");
    const blocoValorRecebido = document.getElementById("blocoValorRecebido");
    const itensJson = document.getElementById("itensJson");
    const formPdv = document.getElementById("formPdv");
    const btnLimparCarrinho = document.getElementById("btnLimparCarrinho");
    const radiosPagamento = document.querySelectorAll('input[name="formaPagamento"]');
    const pdvFeedback = document.getElementById("pdvFeedback");

    // Util
    function formatarMoeda(valor) {
        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL"
        }).format(valor || 0);
    }

    // Toast
    function mostrarFeedback(tipo, mensagem) {
        clearTimeout(feedbackTimeout);

        let icone = "bi-info-circle";

        if (tipo === "success") icone = "bi-check-circle-fill";
        if (tipo === "error") icone = "bi-x-circle-fill";
        if (tipo === "warning") icone = "bi-exclamation-triangle-fill";

        pdvFeedback.className = "pdv-toast";
        pdvFeedback.classList.add(`pdv-toast-${tipo}`);
        pdvFeedback.innerHTML = `
            <i class="bi ${icone}"></i>
            <span>${mensagem}</span>
        `;

        requestAnimationFrame(function () {
            pdvFeedback.classList.add("is-visible");
        });

        feedbackTimeout = setTimeout(function () {
            ocultarFeedback();
        }, 2800);
    }

    // Toast
    function ocultarFeedback() {
        pdvFeedback.classList.remove("is-visible");

        setTimeout(function () {
            pdvFeedback.className = "pdv-toast";
            pdvFeedback.innerHTML = "";
        }, 220);
    }

    // API
    async function buscarProdutoPorCodigo(codigo) {
        const response = await fetch(`/pdv/produtos/codigo/${encodeURIComponent(codigo)}`, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            credentials: "same-origin"
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Produto não encontrado para o código informado.");
            }

            throw new Error("Não foi possível consultar o produto no momento.");
        }

        return response.json();
    }

    // Scanner
    async function processarCodigoBarras(codigo) {
        const codigoLimpo = (codigo || "").trim();

        if (!codigoLimpo) {
            return;
        }

        if (!/^[0-9A-Za-z\\-.]{4,30}$/.test(codigoLimpo)) {
            mostrarFeedback("error", "Código de barras inválido.");
            codigoBarrasInput.value = "";
            return;
        }

        try {
            const produto = await buscarProdutoPorCodigo(codigoLimpo);

            adicionarProduto({
                produtoId: Number(produto.id),
                nome: produto.nome,
                preco: Number(produto.preco),
                estoque: Number(produto.quantidadeEstoque || 0),
                descricao: produto.descricao || ""
            });

            codigoBarrasInput.value = "";
            codigoBarrasInput.focus();
        } catch (error) {
            mostrarFeedback("error", error.message);
            codigoBarrasInput.value = "";
            codigoBarrasInput.focus();
        }
    }

    // Scanner
    function lidarEntradaScanner(event) {
        const agora = Date.now();
        const diferenca = agora - lastScanTime;
        lastScanTime = agora;

        if (diferenca > 120) {
            scanBuffer = "";
        }

        if (event.key === "Enter") {
            event.preventDefault();

            const valorFinal = scanBuffer || codigoBarrasInput.value;
            scanBuffer = "";
            processarCodigoBarras(valorFinal);
            return;
        }

        if (event.key.length === 1) {
            scanBuffer += event.key;
        }

        if (event.key === "Backspace") {
            scanBuffer = codigoBarrasInput.value.slice(0, -1);
        }
    }

    // Pagamento
    function obterFormaPagamentoSelecionada() {
        const selecionado = document.querySelector('input[name="formaPagamento"]:checked');
        return selecionado ? selecionado.value : null;
    }

    // Carrinho
    function obterTotalCarrinho() {
        return carrinho.reduce(function (acc, item) {
            return acc + (item.preco * item.quantidade);
        }, 0);
    }

    // Carrinho
    function obterQuantidadeItens() {
        return carrinho.reduce(function (acc, item) {
            return acc + item.quantidade;
        }, 0);
    }

    // Produto
    function lerProdutoDoCard(card) {
        return {
            produtoId: Number(card.dataset.id),
            nome: card.dataset.nome || "",
            preco: Number(card.dataset.preco || 0),
            estoque: Number(card.dataset.estoque || 0),
            descricao: card.dataset.descricao || ""
        };
    }

    // Carrinho
    function adicionarProduto(produto) {
        const itemExistente = carrinho.find(function (item) {
            return item.produtoId === produto.produtoId;
        });

        if (itemExistente) {
            if (itemExistente.quantidade >= itemExistente.estoque) {
                mostrarFeedback("warning", "Quantidade máxima em estoque atingida para este produto.");
                return;
            }

            itemExistente.quantidade += 1;
            mostrarFeedback("success", `${produto.nome} teve a quantidade atualizada.`);
        } else {
            carrinho.push({
                produtoId: produto.produtoId,
                nome: produto.nome,
                preco: produto.preco,
                quantidade: 1,
                estoque: produto.estoque,
                descricao: produto.descricao
            });

            mostrarFeedback("success", `${produto.nome} foi adicionado ao carrinho.`);
        }

        renderizarCarrinho();
    }

    // Carrinho
    function alterarQuantidade(produtoId, delta) {
        const item = carrinho.find(function (i) {
            return i.produtoId === produtoId;
        });

        if (!item) return;

        const novaQuantidade = item.quantidade + delta;

        if (novaQuantidade <= 0) {
            carrinho = carrinho.filter(function (i) {
                return i.produtoId !== produtoId;
            });

            mostrarFeedback("warning", "Item removido do carrinho.");
        } else if (novaQuantidade <= item.estoque) {
            item.quantidade = novaQuantidade;
            mostrarFeedback("success", "Quantidade atualizada com sucesso.");
        } else {
            mostrarFeedback("error", "Estoque insuficiente para aumentar a quantidade.");
        }

        renderizarCarrinho();
    }

    // Carrinho
    function removerItem(produtoId) {
        carrinho = carrinho.filter(function (i) {
            return i.produtoId !== produtoId;
        });

        mostrarFeedback("warning", "Item removido do carrinho.");
        renderizarCarrinho();
    }

    // Carrinho
    function limparCarrinho() {
        carrinho = [];
        valorRecebidoInput.value = "";
        mostrarFeedback("warning", "Carrinho limpo com sucesso.");
        renderizarCarrinho();
    }

    // Resumo
    function atualizarResumo() {
        const quantidadeItens = obterQuantidadeItens();
        const total = obterTotalCarrinho();

        badgeItens.textContent = quantidadeItens;
        resumoQuantidadeItens.textContent = quantidadeItens;
        valorTotalVenda.textContent = formatarMoeda(total);
        resumoSubtotal.textContent = formatarMoeda(total);

        calcularTroco();
    }

    // Pagamento
    function atualizarPagamento() {
        const formaPagamento = obterFormaPagamentoSelecionada();

        if (formaPagamento === "DINHEIRO") {
            blocoValorRecebido.style.display = "block";
        } else {
            blocoValorRecebido.style.display = "none";
            valorRecebidoInput.value = "";
        }

        calcularTroco();
    }

    // Pagamento
    function calcularTroco() {
        const formaPagamento = obterFormaPagamentoSelecionada();
        const valorRecebido = parseFloat(valorRecebidoInput.value || "0");
        const total = obterTotalCarrinho();

        let troco = 0;

        if (formaPagamento === "DINHEIRO" && valorRecebido > total) {
            troco = valorRecebido - total;
        }

        trocoVenda.textContent = formatarMoeda(troco);
    }

    // Carrinho
    function renderizarEstadoVazio() {
        listaCarrinho.innerHTML = `
            <div class="carrinho-vazio" id="estadoVazio">
                <i class="bi bi-cart3 carrinho-vazio-icon"></i>
                <div>Nenhum item adicionado ao carrinho.</div>
            </div>
        `;
    }

    // Carrinho
    function renderizarCarrinho() {
        if (carrinho.length === 0) {
            renderizarEstadoVazio();
            atualizarResumo();
            return;
        }

        listaCarrinho.innerHTML = carrinho.map(function (item) {
            const subtotal = item.preco * item.quantidade;

            return `
                <div class="item-carrinho">
                    <div>
                        <h4>${item.nome}</h4>
                        <p>${formatarMoeda(item.preco)} por unidade</p>

                        <div class="item-acoes">
                            <button type="button" class="js-diminuir-item" data-id="${item.produtoId}">-</button>
                            <strong>${item.quantidade}</strong>
                            <button type="button" class="js-aumentar-item" data-id="${item.produtoId}">+</button>
                        </div>
                    </div>

                    <div class="item-lado-direito">
                        <strong>${formatarMoeda(subtotal)}</strong>
                        <button type="button" class="item-remove js-remover-item" data-id="${item.produtoId}">
                            Remover
                        </button>
                    </div>
                </div>
            `;
        }).join("");

        atualizarResumo();
    }

    // Envio
    function prepararEnvio(event) {
        if (carrinho.length === 0) {
            event.preventDefault();
            mostrarFeedback("error", "Adicione pelo menos um item ao carrinho.");
            return;
        }

        const itensParaEnvio = carrinho.map(function (item) {
            return {
                produtoId: item.produtoId,
                quantidade: item.quantidade
            };
        });

        itensJson.value = JSON.stringify(itensParaEnvio);

        const formaPagamento = obterFormaPagamentoSelecionada();
        const valorRecebido = valorRecebidoInput.value;

        if (formaPagamento === "DINHEIRO" && (!valorRecebido || Number(valorRecebido) <= 0)) {
            event.preventDefault();
            mostrarFeedback("error", "Informe o valor recebido para pagamento em dinheiro.");
            return;
        }

        ocultarFeedback();
    }

    // Busca
    function filtrarProdutos() {
        const termo = buscaProduto.value.toLowerCase();
        const produtos = gridProdutos.querySelectorAll(".produto-card");

        produtos.forEach(function (produto) {
            const nome = (produto.dataset.nome || "").toLowerCase();
            const descricao = (produto.dataset.descricao || "").toLowerCase();
            const visivel = nome.includes(termo) || descricao.includes(termo);
            produto.style.display = visivel ? "flex" : "none";
        });
    }

    // Eventos
    buscaProduto.addEventListener("input", filtrarProdutos);
    codigoBarrasInput.addEventListener("keydown", lidarEntradaScanner);

    // Eventos
    gridProdutos.addEventListener("click", function (event) {
        const card = event.target.closest(".produto-card");
        if (!card) return;

        const produto = lerProdutoDoCard(card);
        adicionarProduto(produto);
    });

    // Eventos
    listaCarrinho.addEventListener("click", function (event) {
        const btnDiminuir = event.target.closest(".js-diminuir-item");
        const btnAumentar = event.target.closest(".js-aumentar-item");
        const btnRemover = event.target.closest(".js-remover-item");

        if (btnDiminuir) {
            alterarQuantidade(Number(btnDiminuir.dataset.id), -1);
            return;
        }

        if (btnAumentar) {
            alterarQuantidade(Number(btnAumentar.dataset.id), 1);
            return;
        }

        if (btnRemover) {
            removerItem(Number(btnRemover.dataset.id));
        }
    });

    // Eventos
    radiosPagamento.forEach(function (radio) {
        radio.addEventListener("change", atualizarPagamento);
    });

    // Eventos
    valorRecebidoInput.addEventListener("input", calcularTroco);
    btnLimparCarrinho.addEventListener("click", limparCarrinho);
    formPdv.addEventListener("submit", prepararEnvio);

    // Init
    atualizarPagamento();
    renderizarCarrinho();
    codigoBarrasInput.focus();
});