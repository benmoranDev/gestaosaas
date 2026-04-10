package com.gestaosaas.gestaosaas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO usado para receber os dados do formulário
 * de cadastro de uma nova empresa no sistema.
 */
public class CadastroEmpresaForm {

    //Nome da empresa que será criada no sistema.
    @NotBlank(message = "O nome da empresa é obrigatório")
    @Size(min = 2, max = 120, message = "O nome da empresa deve ter entre 2 e 120 caracteres")
    private String nomeEmpresa;

    /**
     * Identificador amigável da empresa.
     * Exemplo: minha-loja, oficina-central, etc.
     */
    @NotBlank(message = "O slug da empresa é obrigatório")
    @Size(min = 3, max = 60, message = "O slug deve ter entre 3 e 60 caracteres")
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "O slug deve conter apenas letras minúsculas, números e hífen"
    )
    private String slugEmpresa;

    /**
     * CNPJ da empresa.
     * Pode ser opcional no primeiro momento, caso você queira
     * simplificar o onboarding inicial.
     */
    private String cnpj;

    /**
     * Nome do usuário administrador da empresa.
     */
    @NotBlank(message = "O nome do administrador é obrigatório")
    @Size(min = 3, max = 120, message = "O nome do administrador deve ter entre 3 e 120 caracteres")
    private String nomeAdministrador;

    /**
     * Email do usuário administrador.
     */
    @NotBlank(message = "O email do administrador é obrigatório")
    @Email(message = "Informe um email válido")
    private String emailAdministrador;

    /**
     * Senha inicial do administrador.
     */
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    /**
     * Confirmação da senha digitada no formulário.
     */
    @NotBlank(message = "A confirmação de senha é obrigatória")
    private String confirmacaoSenha;

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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }

    /**
     * Verifica se a senha e a confirmação são iguais.
     */
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmacaoSenha);
    }
}