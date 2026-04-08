package com.gestaosaas.gestaosaas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pix")
public class PixProperties {

    private String recebedorNome;
    private String chave;
    private Integer expiracaoSegundos = 900;
    private String webhookToken = "troque-este-token";

    public String getRecebedorNome() {
        return recebedorNome;
    }

    public void setRecebedorNome(String recebedorNome) {
        this.recebedorNome = recebedorNome;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Integer getExpiracaoSegundos() {
        return expiracaoSegundos;
    }

    public void setExpiracaoSegundos(Integer expiracaoSegundos) {
        this.expiracaoSegundos = expiracaoSegundos;
    }

    public String getWebhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
    }
}
