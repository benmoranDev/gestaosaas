package com.gestaosaas.gestaosaas.service;


import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AuthService authService;

    public ClienteService(ClienteRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    //Lista todos os clientes da empresa do usuário logado.

    public List<Cliente> listarTodos() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.findByEmpresa(empresa);
    }

    //Salva um cliente vinculado à empresa do usuário logado.

    public Cliente salvar(Cliente cliente) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        cliente.setEmpresa(empresa);
        return repository.save(cliente);
    }

    //Busca um cliente pelo id, garantindo que ele pertença à empresa logada.

    public Cliente buscarPorId(Long id) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.findByIdAndEmpresa(id, empresa)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    //Exclui um cliente da empresa logada.

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
    }

    //Retorna a quantidade de clientes da empresa logada.

    public long contarClientesDaEmpresa() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.countByEmpresa(empresa);
    }

    //Obtém a empresa vinculada ao usuário autenticado.
    private Empresa obterEmpresaDoUsuarioLogado() {
        return authService.usuarioLogado().getEmpresa();
    }
}