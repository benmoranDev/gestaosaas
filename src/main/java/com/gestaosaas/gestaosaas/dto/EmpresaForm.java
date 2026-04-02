package com.gestaosaas.gestaosaas.dto;

import jakarta.validation.constraints.NotBlank;

//DTO usado para edição dos dados da empresa logada.
public class EmpresaForm {

    //Nome da empresa.
    @NotBlank(message = "O nome da empresa é obrigatório")
    private String nome;

    //Slug da empresa.

    @NotBlank(message = "O slug da empresa é obrigatório")
    private String slug;

    //Indica se a empresa está ativa.
    private Boolean ativa;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
