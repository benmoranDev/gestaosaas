document.addEventListener("DOMContentLoaded", function () {
    let carrinho = [];
    let feedbackTimeout;
    let scanBuffer = "";
    let lastScanTime = 0;
    let pixConfirmado = false;

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
    const itensJson = document.getElementById("itensJson");
    const formPdv = document.getElementById("formPdv");
    const btnLimparCarrinho = document.getElementById("btnLimparCarrinho");
    const radiosPagamento = document.querySelectorAll('input[name="formaPagamento"]');
    const pdvFeedback = document.getElementById("pdvFeedback");
    const clienteId = document.getElementById("clienteId");

    const blocoDinheiro = document.getElementById("blocoDinheiro");
    const blocoPix = document.getElementById("blocoPix");
    const blocoCartao = document.getElementById("blocoCartao");
    const blocoVale = document.getElementById("blocoVale");

    const trocoInput = document.getElementById("trocoInput");
    const statusFiscal = document.getElementById("statusFiscal");
    const statusFiscalLabel = document.getElementById("statusFiscalLabel");

    const pixStatus = document.getElementById("pixStatus");
    const btnGerarPix = document.getElementById("btnGerarPix");
    const btnConfirmarPix = document.getElementById("btnConfirmarPix");

    const btnMarcarProcessando = document.getElementById("btnMarcarProcessando");
    const btnMarcarAutorizado = document.getElementById("btnMarcarAutorizado");

    const pixTxId = document.getElementById("pixTxId");
    const pixCopiaCola = document.getElementById("pixCopiaCola");

    const bandeiraCartao = document.getElementById("bandeiraCartao");
    const ultimosDigitosCartao = document.getElementById("ultimosDigitosCartao");
    const operadoraVale = document.getElementById("operadoraVale");

    const clienteFiscalPreview = document.getElementById("clienteFiscalPreview");
    const formaPagamentoPreview = document.getElementById("formaPagamentoPreview");
    const resumoSubtotalFiscal = document.getElementById("resumoSubtotalFiscal");
    const trocoFiscal = document.getElementById("trocoFiscal");
    const valorTotalFiscal = document.getElementById("valorTotalFiscal");
    const itensFiscalPreview = document.getElementById("itensFiscalPreview");
    const numeroNotaPreview = document.getElementById("numeroNotaPreview");
    const chaveNotaPreview = document.getElementById("chaveNotaPreview");

    const permissaoGerencialEl = document.getElementById("permissoesGerenciaisPdv");
    const podeRemoverItens = !!permissaoGerencialEl && permissaoGerencialEl.dataset.podeRemover === "true";

    function formatarMoeda(valor) {
        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL"
        }).format(Number(valor || 0));
    }

    function escaparHtml(texto) {
        return String(texto || "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function obterTextoClienteSelecionado() {
        if (!clienteId) return "Consumidor final";
        const option = clienteId.options[clienteId.selectedIndex];
        return option ? option.text.trim() : "Consumidor final";
    }

    function formatarFormaPagamento(valor) {
        const mapa = {
            DINHEIRO: "Dinheiro",
            PIX: "PIX",
            CARTAO_DEBITO: "Cartão débito",
            CARTAO_CREDITO: "Cartão crédito",
            VALE_ALIMENTACAO: "Vale alimentação"
        };
        return mapa[valor] || (valor || "-");
    }

    function mostrarFeedback(tipo, mensagem) {
        if (!pdvFeedback) return;

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

    function ocultarFeedback() {
        if (!pdvFeedback) return;

        pdvFeedback.classList.remove("is-visible");

        setTimeout(function () {
            pdvFeedback.className = "pdv-toast";
            pdvFeedback.innerHTML = "";
        }, 220);
    }

    async function buscarProdutoPorCodigo(codigo) {
        const response = await fetch(`/pdv/buscar-por-codigo?codigo=${encodeURIComponent(codigo)}`, {
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

        return await response.json();
    }

    async function processarCodigoBarras(codigo) {
        const codigoLimpo = (codigo || "").trim();

        if (!codigoLimpo) return;

        if (!/^[0-9A-Za-z.\-]{4,30}$/.test(codigoLimpo)) {
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

    function obterFormaPagamentoSelecionada() {
        const selecionado = document.querySelector('input[name="formaPagamento"]:checked');
        return selecionado ? selecionado.value : null;
    }

    function obterTotalCarrinho() {
        return carrinho.reduce(function (acc, item) {
            return acc + (item.preco * item.quantidade);
        }, 0);
    }

    function obterQuantidadeItens() {
        return carrinho.reduce(function (acc, item) {
            return acc + item.quantidade;
        }, 0);
    }

    function lerProdutoDoCard(card) {
        return {
            produtoId: Number(card.dataset.id),
            nome: card.dataset.nome || "",
            preco: Number(card.dataset.preco || 0),
            estoque: Number(card.dataset.estoque || 0),
            descricao: card.dataset.descricao || ""
        };
    }

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

        renderizarTudo();
    }

    function alterarQuantidade(produtoId, delta) {
        const item = carrinho.find(function (i) {
            return i.produtoId === produtoId;
        });

        if (!item) return;

        const novaQuantidade = item.quantidade + delta;

        if (novaQuantidade <= 0) {
            if (!podeRemoverItens) {
                mostrarFeedback("error", "Somente gerente ou administrador pode remover itens.");
                return;
            }

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

        renderizarTudo();
    }

    function removerItem(produtoId) {
        if (!podeRemoverItens) {
            mostrarFeedback("error", "Somente gerente ou administrador pode remover itens.");
            return;
        }

        carrinho = carrinho.filter(function (i) {
            return i.produtoId !== produtoId;
        });

        mostrarFeedback("warning", "Item removido do carrinho.");
        renderizarTudo();
    }

    function limparCarrinho() {
        carrinho = [];
        pixConfirmado = false;

        if (valorRecebidoInput) valorRecebidoInput.value = "";
        if (trocoInput) trocoInput.value = "0.00";

        if (pixStatus) {
            pixStatus.textContent = "Status do PIX: aguardando pagamento";
        }

        if (pixTxId) pixTxId.value = "";
        if (pixCopiaCola) pixCopiaCola.value = "";

        mostrarFeedback("warning", "Carrinho limpo com sucesso.");
        renderizarTudo();
    }

    function calcularTroco() {
        const formaPagamento = obterFormaPagamentoSelecionada();
        const valorRecebido = parseFloat(valorRecebidoInput?.value || "0");
        const total = obterTotalCarrinho();

        let troco = 0;

        if (formaPagamento === "DINHEIRO" && valorRecebido > total) {
            troco = valorRecebido - total;
        }

        if (trocoVenda) trocoVenda.textContent = formatarMoeda(troco);
        if (trocoInput) trocoInput.value = troco.toFixed(2);
        if (trocoFiscal) trocoFiscal.textContent = formatarMoeda(troco);

        return troco;
    }

    function atualizarResumo() {
        const quantidadeItens = obterQuantidadeItens();
        const total = obterTotalCarrinho();

        if (badgeItens) badgeItens.textContent = quantidadeItens;
        if (resumoQuantidadeItens) resumoQuantidadeItens.textContent = quantidadeItens;
        if (valorTotalVenda) valorTotalVenda.textContent = formatarMoeda(total);
        if (resumoSubtotal) resumoSubtotal.textContent = formatarMoeda(total);

        calcularTroco();
    }

    function esconderBlocosPagamento() {
        blocoDinheiro?.classList.add("d-none");
        blocoPix?.classList.add("d-none");
        blocoCartao?.classList.add("d-none");
        blocoVale?.classList.add("d-none");
    }

    function atualizarPagamento() {
        const formaPagamento = obterFormaPagamentoSelecionada();

        esconderBlocosPagamento();

        if (formaPagamento === "DINHEIRO") {
            blocoDinheiro?.classList.remove("d-none");
        }

        if (formaPagamento === "PIX") {
            blocoPix?.classList.remove("d-none");
        }

        if (formaPagamento === "CARTAO_DEBITO" || formaPagamento === "CARTAO_CREDITO") {
            blocoCartao?.classList.remove("d-none");
        }

        if (formaPagamento === "VALE_ALIMENTACAO") {
            blocoVale?.classList.remove("d-none");
        }

        if (formaPagamento !== "DINHEIRO" && valorRecebidoInput) {
            valorRecebidoInput.value = "";
        }

        if (formaPagamento !== "PIX") {
            pixConfirmado = false;
            if (pixStatus) {
                pixStatus.textContent = "Status do PIX: aguardando pagamento";
            }
        }

        if (formaPagamentoPreview) {
            formaPagamentoPreview.textContent = formatarFormaPagamento(formaPagamento);
        }

        calcularTroco();
        atualizarPainelFiscal();
    }

    function atualizarStatusFiscal(novoStatus) {
        if (statusFiscal) statusFiscal.value = novoStatus;
        if (statusFiscalLabel) statusFiscalLabel.textContent = novoStatus;
    }

    function atualizarPainelFiscal() {
        const total = obterTotalCarrinho();
        const troco = calcularTroco();
        const formaPagamento = obterFormaPagamentoSelecionada();
        const clienteTexto = obterTextoClienteSelecionado();

        if (clienteFiscalPreview) clienteFiscalPreview.textContent = clienteTexto;
        if (formaPagamentoPreview) formaPagamentoPreview.textContent = formatarFormaPagamento(formaPagamento);
        if (resumoSubtotalFiscal) resumoSubtotalFiscal.textContent = formatarMoeda(total);
        if (valorTotalFiscal) valorTotalFiscal.textContent = formatarMoeda(total);
        if (trocoFiscal) trocoFiscal.textContent = formatarMoeda(troco);

        if (itensFiscalPreview) {
            if (carrinho.length === 0) {
                itensFiscalPreview.innerHTML = "Nenhum item adicionado.";
            } else {
                itensFiscalPreview.innerHTML = carrinho.map(function (item) {
                    const subtotal = item.preco * item.quantidade;
                    return `
                        <div class="thermal-item-row">
                            <span>${item.quantidade}x ${escaparHtml(item.nome)}</span>
                            <strong>${formatarMoeda(subtotal)}</strong>
                        </div>
                    `;
                }).join("");
            }
        }

        if (numeroNotaPreview && !numeroNotaPreview.textContent.trim()) {
            numeroNotaPreview.textContent = "Ainda não emitida";
        }

        if (chaveNotaPreview && !chaveNotaPreview.textContent.trim()) {
            chaveNotaPreview.textContent = "Será gerada após emissão";
        }
    }

    function sincronizarCamposOcultos() {
        const itensParaEnvio = carrinho.map(function (item) {
            return {
                produtoId: item.produtoId,
                quantidade: item.quantidade
            };
        });

        if (itensJson) {
            itensJson.value = JSON.stringify(itensParaEnvio);
        }

        calcularTroco();
    }

    function renderizarEstadoVazio() {
        listaCarrinho.innerHTML = `
            <div class="carrinho-vazio" id="estadoVazio">
                <i class="bi bi-cart3 carrinho-vazio-icon"></i>
                <div>Nenhum item adicionado ao carrinho.</div>
            </div>
        `;
    }

    function renderizarCarrinho() {
        if (!listaCarrinho) return;

        if (carrinho.length === 0) {
            renderizarEstadoVazio();
            return;
        }

        listaCarrinho.innerHTML = carrinho.map(function (item) {
            const subtotal = item.preco * item.quantidade;

            return `
                <div class="item-carrinho">
                    <div>
                        <h4>${escaparHtml(item.nome)}</h4>
                        <p>${formatarMoeda(item.preco)} por unidade</p>

                        <div class="item-acoes">
                            <button type="button" class="js-diminuir-item" data-id="${item.produtoId}">-</button>
                            <strong>${item.quantidade}</strong>
                            <button type="button" class="js-aumentar-item" data-id="${item.produtoId}">+</button>
                        </div>
                    </div>

                    <div class="item-lado-direito">
                        <strong>${formatarMoeda(subtotal)}</strong>
                        ${podeRemoverItens ? `
                            <button type="button" class="item-remove js-remover-item" data-id="${item.produtoId}">
                                Remover
                            </button>
                        ` : ""}
                    </div>
                </div>
            `;
        }).join("");
    }

    function renderizarTudo() {
        renderizarCarrinho();
        atualizarResumo();
        atualizarPainelFiscal();
        sincronizarCamposOcultos();
    }

    function validarCamposPagamento() {
        const formaPagamento = obterFormaPagamentoSelecionada();
        const total = obterTotalCarrinho();
        const valorRecebido = parseFloat(valorRecebidoInput?.value || "0");

        if (formaPagamento === "DINHEIRO") {
            if (!valorRecebidoInput || !valorRecebidoInput.value || Number(valorRecebidoInput.value) <= 0) {
                mostrarFeedback("error", "Informe o valor recebido para pagamento em dinheiro.");
                return false;
            }

            if (valorRecebido < total) {
                mostrarFeedback("error", "O valor recebido não pode ser menor que o total da venda.");
                return false;
            }
        }

        if (formaPagamento === "PIX") {
            if (!pixConfirmado) {
                mostrarFeedback("error", "Confirme o pagamento PIX antes de finalizar.");
                return false;
            }
        }

        if (formaPagamento === "CARTAO_DEBITO" || formaPagamento === "CARTAO_CREDITO") {
            if (bandeiraCartao && !bandeiraCartao.value) {
                mostrarFeedback("error", "Selecione a bandeira do cartão.");
                return false;
            }

            if (ultimosDigitosCartao && ultimosDigitosCartao.value && !/^\d{4}$/.test(ultimosDigitosCartao.value)) {
                mostrarFeedback("error", "Informe os 4 últimos dígitos do cartão corretamente.");
                return false;
            }
        }

        if (formaPagamento === "VALE_ALIMENTACAO") {
            if (operadoraVale && !operadoraVale.value.trim()) {
                mostrarFeedback("error", "Informe a operadora do vale alimentação.");
                return false;
            }
        }

        return true;
    }

    function prepararEnvio(event) {
        if (carrinho.length === 0) {
            event.preventDefault();
            mostrarFeedback("error", "Adicione pelo menos um item ao carrinho.");
            return;
        }

        if (!validarCamposPagamento()) {
            event.preventDefault();
            return;
        }

        sincronizarCamposOcultos();

        if (statusFiscal && (!statusFiscal.value || statusFiscal.value === "PENDENTE")) {
            atualizarStatusFiscal("EM_PROCESSAMENTO");
        }

        ocultarFeedback();
    }

    function filtrarProdutos() {
        if (!buscaProduto || !gridProdutos) return;

        const termo = buscaProduto.value.toLowerCase();
        const produtos = gridProdutos.querySelectorAll(".produto-card");

        produtos.forEach(function (produto) {
            const nome = (produto.dataset.nome || "").toLowerCase();
            const descricao = (produto.dataset.descricao || "").toLowerCase();
            const visivel = nome.includes(termo) || descricao.includes(termo);
            produto.style.display = visivel ? "flex" : "none";
        });
    }

    if (buscaProduto) {
        buscaProduto.addEventListener("input", filtrarProdutos);
    }

    if (codigoBarrasInput) {
        codigoBarrasInput.addEventListener("keydown", lidarEntradaScanner);
    }

    if (clienteId) {
        clienteId.addEventListener("change", atualizarPainelFiscal);
    }

    if (gridProdutos) {
        gridProdutos.addEventListener("click", function (event) {
            const botao = event.target.closest(".js-add-produto");
            const card = event.target.closest(".produto-card");

            if (!card || !botao) return;

            const produto = lerProdutoDoCard(card);
            adicionarProduto(produto);
        });
    }

    if (listaCarrinho) {
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
    }

    radiosPagamento.forEach(function (radio) {
        radio.addEventListener("change", atualizarPagamento);
    });

    if (valorRecebidoInput) {
        valorRecebidoInput.addEventListener("input", function () {
            calcularTroco();
            atualizarPainelFiscal();
        });
    }

    if (btnGerarPix) {
        btnGerarPix.addEventListener("click", function () {
            if (pixTxId && !pixTxId.value.trim()) {
                pixTxId.value = "SB" + Date.now();
            }

            if (pixCopiaCola && !pixCopiaCola.value.trim()) {
                pixCopiaCola.value = "000201PIX-SMART-BUSINESS-" + Date.now();
            }

            if (pixStatus) {
                pixStatus.textContent = "Status do PIX: QR Code gerado, aguardando pagamento";
            }

            mostrarFeedback("success", "QR Code PIX gerado com sucesso.");
        });
    }

    if (btnConfirmarPix) {
        btnConfirmarPix.addEventListener("click", function () {
            pixConfirmado = true;

            if (pixStatus) {
                pixStatus.textContent = "Status do PIX: pagamento confirmado";
            }

            mostrarFeedback("success", "Pagamento PIX confirmado.");
        });
    }

    if (btnMarcarProcessando) {
        btnMarcarProcessando.addEventListener("click", function () {
            atualizarStatusFiscal("EM_PROCESSAMENTO");
            atualizarPainelFiscal();
            mostrarFeedback("warning", "Status fiscal alterado para EM_PROCESSAMENTO.");
        });
    }

    if (btnMarcarAutorizado) {
        btnMarcarAutorizado.addEventListener("click", function () {
            atualizarStatusFiscal("AUTORIZADO");

            if (numeroNotaPreview) {
                numeroNotaPreview.textContent = "NFC-e em autorização";
            }

            if (chaveNotaPreview) {
                chaveNotaPreview.textContent = "Chave será definida pelo backend";
            }

            atualizarPainelFiscal();
            mostrarFeedback("success", "Status fiscal alterado para AUTORIZADO.");
        });
    }

    if (btnLimparCarrinho) {
        btnLimparCarrinho.addEventListener("click", limparCarrinho);
    }

    if (formPdv) {
        formPdv.addEventListener("submit", prepararEnvio);
    }

    atualizarPagamento();
    renderizarTudo();

    if (codigoBarrasInput) {
        codigoBarrasInput.focus();
    }
});
