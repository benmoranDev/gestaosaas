package com.gestaosaas.gestaosaas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "documento_fiscal") // 👈 nome explícito da tabela
public class DocumentoFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    private Venda venda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDocumentoFiscal tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusFiscal status = StatusFiscal.PENDENTE;

    @Column(length = 20)
    private String numero;

    @Column(length = 10)
    private String serie;

    @Column(length = 60)
    private String chaveAcesso;

    @Column(length = 60)
    private String protocolo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String xmlEnvio;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String xmlRetorno;

    @Column(length = 1000)
    private String motivoRejeicao;

    private LocalDateTime emitidoEm;

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public TipoDocumentoFiscal getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumentoFiscal tipo) {
        this.tipo = tipo;
    }

    public StatusFiscal getStatus() {
        return status;
    }

    public void setStatus(StatusFiscal status) {
        this.status = status;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getXmlEnvio() {
        return xmlEnvio;
    }

    public void setXmlEnvio(String xmlEnvio) {
        this.xmlEnvio = xmlEnvio;
    }

    public String getXmlRetorno() {
        return xmlRetorno;
    }

    public void setXmlRetorno(String xmlRetorno) {
        this.xmlRetorno = xmlRetorno;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public void setEmitidoEm(LocalDateTime emitidoEm) {
        this.emitidoEm = emitidoEm;
    }
}
