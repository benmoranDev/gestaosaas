package com.gestaosaas.gestaosaas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO usado para receber os dados do formulário
 * de cadastro de uma nova empresa no sistema.
 */
public class CadastroEmpresaForm {

    /**
     * Nome da empresa que será criada no sistema.
     */
    @NotBlank(message = "O nome da empresa é obrigatório")
    private String nomeEmpresa;

    /**
     * Identificador amigável da empresa.
     * Exemplo: minha-loja, oficina-central, etc.
     */
    @NotBlank(message = "O slug da empresa é obrigatório")
    private String slugEmpresa;

    /**
     * Nome do usuário administrador da empresa.
     */
    @NotBlank(message = "O nome do administrador é obrigatório")
    private String nomeAdministrador;

    /**
     * Email do usuário administrador.
     */
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String emailAdministrador;

    /**
     * Senha inicial do administrador.
     */
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getSlugEmpresa() {
        return slugEmpresa;
    }

    public void setSlugEmpresa(String slugEmpresa) {
        this.slugEmpresa = slugEmpresa;
    }

    public String getNomeAdministrador() {
        return nomeAdministrador;
    }

    public void setNomeAdministrador(String nomeAdministrador) {
        this.nomeAdministrador = nomeAdministrador;
    }

    public String getEmailAdministrador() {
        return emailAdministrador;
    }

    public void setEmailAdministrador(String emailAdministrador) {
        this.emailAdministrador = emailAdministrador;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}