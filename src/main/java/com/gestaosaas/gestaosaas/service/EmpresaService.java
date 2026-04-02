package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.dto.EmpresaForm;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelas operações da empresa logada.
 */
@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final AuthService authService;

    public EmpresaService(EmpresaRepository empresaRepository,
                          AuthService authService) {
        this.empresaRepository = empresaRepository;
        this.authService = authService;
    }

    /**
     * Retorna a empresa do usuário autenticado.
     */
    public Empresa obterEmpresaAtual() {
        return authService.usuarioLogado().getEmpresa();
    }

    /**
     * Converte a empresa atual para um formulário de edição.
     */
    public EmpresaForm obterFormularioEmpresaAtual() {
        Empresa empresa = obterEmpresaAtual();

        EmpresaForm form = new EmpresaForm();
        form.setNome(empresa.getNome());
        form.setSlug(empresa.getSlug());

        return form;
    }

    /**
     * Atualiza os dados da empresa atual.
     */
    @Transactional
    public void atualizarEmpresaAtual(EmpresaForm form) {
        Empresa empresa = obterEmpresaAtual();

        empresa.setNome(form.getNome());
        empresa.setSlug(form.getSlug());

        empresaRepository.save(empresa);
    }
}