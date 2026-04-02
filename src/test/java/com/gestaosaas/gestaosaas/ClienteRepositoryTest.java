package com.gestaosaas.gestaosaas;


import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void deveSalvarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");

        Cliente salvo = clienteRepository.save(cliente);

        assertNotNull(salvo.getId());
        assertEquals("João", salvo.getNome());
    }
}