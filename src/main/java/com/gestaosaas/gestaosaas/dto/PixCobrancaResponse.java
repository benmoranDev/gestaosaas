package com.gestaosaas.gestaosaas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PixCobrancaResponse {

    private String txid;
    private BigDecimal valor;
    private String qrCodeUrl;
    private String pixCopiaECola;
    private LocalDateTime expiracaoEm;
    private String status;

    public PixCobrancaResponse() {
    }

    public PixCobrancaResponse(String txid, BigDecimal valor, String qrCodeUrl,
                               String pixCopiaECola, LocalDateTime expiracaoEm, String status) {
        this.txid = txid;
        this.valor = valor;
        this.qrCodeUrl = qrCodeUrl;
        this.pixCopiaECola = pixCopiaECola;
        this.expiracaoEm = expiracaoEm;
        this.status = status;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getPixCopiaECola() {
        return pixCopiaECola;
    }

    public void setPixCopiaECola(String pixCopiaECola) {
        this.pixCopiaECola = pixCopiaECola;
    }

    public LocalDateTime getExpiracaoEm() {
        return expiracaoEm;
    }

    public void setExpiracaoEm(LocalDateTime expiracaoEm) {
        this.expiracaoEm = expiracaoEm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}