package com.gestaosaas.gestaosaas.dto;

public class PixWebhookRequest {

    private String txid;
    private String endToEndId;
    private String status;
    private String payloadBruto;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayloadBruto() {
        return payloadBruto;
    }

    public void setPayloadBruto(String payloadBruto) {
        this.payloadBruto = payloadBruto;
    }
}