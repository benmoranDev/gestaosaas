package com.gestaosaas.gestaosaas.dto;

import com.gestaosaas.gestaosaas.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



//DTO usado para edição de usuários da empresa.
public class UsuarioEdicaoForm {

    /**
     * ID do usuário que será editado.
     */
    private Long id;

    /**
     * Nome do usuário.
     */
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    /**
     * Email do usuário.
     */
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String email;

    /**
     * Nova senha do usuário.
     * Se ficar em branco, a senha atual será mantida.
     */
    private String senha;

    /**
     * Perfil do usuário.
     */
    @NotNull(message = "O perfil é obrigatório")
    private Perfil perfil;

    /**
     * Indica se o usuário está ativo.
     */
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}