package com.gestaosaas.gestaosaas.dto;

import java.time.LocalDateTime;

public class PixStatusResponse {

    private String txid;
    private String status;
    private String endToEndId;
    private LocalDateTime pagoEm;

    public PixStatusResponse() {
    }

    public PixStatusResponse(String txid, String status, String endToEndId, LocalDateTime pagoEm) {
        this.txid = txid;
        this.status = status;
        this.endToEndId = endToEndId;
        this.pagoEm = pagoEm;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public LocalDateTime getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(LocalDateTime pagoEm) {
        this.pagoEm = pagoEm;
    }
}
