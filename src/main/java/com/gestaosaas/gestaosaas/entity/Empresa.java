package com.gestaosaas.gestaosaas.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Column(unique = true)
    private String slug;

    private boolean ativa = true;

    public Long getId() {
        return id;
    }

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

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public void setCnpj(String cnpj) {

    }
}
