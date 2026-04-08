package com.gestaosaas.gestaosaas.service.gateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PixGateway {

    PixGatewayCobranca criarCobranca(String txid, BigDecimal valor);

    PixGatewayStatus consultarStatus(String txid);

    boolean validarTokenWebhook(String tokenRecebido);

    class PixGatewayCobranca {
        private String txid;
        private String qrCodeUrl;
        private String copiaECola;
        private LocalDateTime expiracaoEm;

        public PixGatewayCobranca(String txid, String qrCodeUrl, String copiaECola, LocalDateTime expiracaoEm) {
            this.txid = txid;
            this.qrCodeUrl = qrCodeUrl;
            this.copiaECola = copiaECola;
            this.expiracaoEm = expiracaoEm;
        }

        public String getTxid() {
            return txid;
        }

        public String getQrCodeUrl() {
            return qrCodeUrl;
        }

        public String getCopiaECola() {
            return copiaECola;
        }

        public LocalDateTime getExpiracaoEm() {
            return expiracaoEm;
        }
    }

    class PixGatewayStatus {
        private String txid;
        private boolean pago;
        private String endToEndId;

        public PixGatewayStatus(String txid, boolean pago, String endToEndId) {
            this.txid = txid;
            this.pago = pago;
            this.endToEndId = endToEndId;
        }

        public String getTxid() {
            return txid;
        }

        public boolean isPago() {
            return pago;
        }

        public String getEndToEndId() {
            return endToEndId;
        }
    }
}