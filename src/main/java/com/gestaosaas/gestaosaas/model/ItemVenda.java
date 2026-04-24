package com.gestaosaas.gestaosaas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull(message = "O produto é obrigatório")
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade;

    @NotNull(message = "O preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço unitário deve ser maior que zero")
    @Column(precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Calcula o subtotal do item.
     */
    public void calcularSubtotal() {
        BigDecimal qtd = BigDecimal.valueOf(quantidade != null ? quantidade : 0);
        BigDecimal preco = precoUnitario != null ? precoUnitario : BigDecimal.ZERO;
        this.subtotal = preco.multiply(qtd);
    }

    private boolean itemValido(ItemVenda itemForm) {
        return itemForm != null
                && itemForm.getProduto() != null
                && itemForm.getProduto().getId() != null
                && itemForm.getQuantidade() != null
                && itemForm.getQuantidade() > 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}