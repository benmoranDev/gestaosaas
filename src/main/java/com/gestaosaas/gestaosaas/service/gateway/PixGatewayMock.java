package com.gestaosaas.gestaosaas.service.gateway;

import com.gestaosaas.gestaosaas.config.PixProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PixGatewayMock implements PixGateway {

    private final PixProperties properties;

    public PixGatewayMock(PixProperties properties) {
        this.properties = properties;
    }

    // Mock inicial para o projeto subir sem depender de PSP real.
    @Override
    public PixGatewayCobranca criarCobranca(String txid, BigDecimal valor) {
        String copiaECola = "00020126360014BR.GOV.BCB.PIX0114" + properties.getChave()
                + "520400005303986540"
                + valor.toPlainString()
                + "5802BR5913" + properties.getRecebedorNome()
                + "6009SAOPAULO62070503***6304ABCD";

        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=280x280&data=" + copiaECola;

        return new PixGatewayCobranca(
                txid,
                qrCodeUrl,
                copiaECola,
                LocalDateTime.now().plusSeconds(properties.getExpiracaoSegundos())
        );
    }

    // Mock simples: considera pago se o txid terminar com "OK".
    @Override
    public PixGatewayStatus consultarStatus(String txid) {
        boolean pago = txid != null && txid.endsWith("OK");
        return new PixGatewayStatus(txid, pago, pago ? "E2E-MOCK-" + txid : null);
    }

    @Override
    public boolean validarTokenWebhook(String tokenRecebido) {
        return properties.getWebhookToken().equals(tokenRecebido);
    }
}