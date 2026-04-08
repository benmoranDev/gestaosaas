# 🏢 Smart Business — Sistema de Gestão Empresarial SaaS

> Plataforma SaaS multi - gestão de negócios, com módulo de caixa (PDV),
> controle de vendas, clientes, produtos, financeiro e usuários por empresa.

O sistema foi construído com foco em operações do dia a dia de pequenas e médias empresas,
com uma interface limpa e um módulo de caixa (PDV) acessível diretamente pelo navegador
em nova aba, sem interferir no painel administrativo.

✅ Funcionalidades

Painel Administrativo.

- [x] Autenticação com Spring Security (login/logout por empresa)
- [x] Cadastro e gerenciamento de clientes
- [x] Cadastro e gerenciamento de produtos com controle de estoque
- [x] Registro e histórico de vendas
- [x] Módulo financeiro (receitas e despesas)
- [x] Configuração da empresa (dados, logo, etc.)
- [x] Gerenciamento de usuários com perfis de acesso
- [x] Dashboard com indicadores: vendas, clientes, produtos e receita

PDV — Frente de Caixa.

- [x] Abertura em nova aba separada do painel administrativo
- [x] Busca de produtos por código de barras ou nome
- [x] Carrinho de compras com adição/remoção de itens
- [x] Suporte a múltiplas formas de pagamento (dinheiro, cartão, PIX)
- [x] Cálculo automático de troco
- [x] Seleção de cliente opcional na venda
- [x] Integração com o domínio de Venda já existente

🛠 Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Segurança | Spring Security |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL 16 |
| Template engine | Thymeleaf 3 + Thymeleaf Security Extras |
| Frontend | Bootstrap 4 + JavaScript |
| Build | Maven |
| Servidor | Apache Tomcat (embedded) |
