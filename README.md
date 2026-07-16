# 📦 API de Gestão de Estoque - Produção Audiovisual (Backend)

Uma API RESTful desenvolvida em **Java** com **Spring Boot** para gerenciar o inventário físico, status e logística de equipamentos de eventos (Painéis de LED, Áudio, Iluminação, Estrutura e Energia).

## 🎯 O Problema que Resolve
No setor de produção de eventos, o controle de entrada e saída de equipamentos frequentemente sofre com perdas, quebras não reportadas e planilhas desatualizadas. Este sistema fornece um motor de backend robusto para:
- Rastreamento individual de equipamentos via **QR Code**.
- Herança de tabelas no banco de dados para tratar atributos específicos (ex: metragem de cabos vs. voltagem de iluminação) sem poluir o esquema.
- Bloqueio de dados inconsistentes na entrada.
- Prevenção de falhas humanas na montagem de cases e caminhões.

## 🚀 Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **PostgreSQL** (Banco de dados relacional)
- **Spring Security** (Autenticação e Autorização RBAC)
- **Springdoc OpenAPI / Swagger UI** (Documentação Interativa)
- **Maven** (Gerenciamento de dependências)

## 🏗️ Arquitetura e Padrões Aplicados
- **Arquitetura em Camadas:** Divisão clara entre `Controller`, `Service` e `Repository`.
- **Orientação a Objetos Avançada:** Uso de classes abstratas e `@Inheritance(strategy = InheritanceType.JOINED)` no JPA para modelagem eficiente de diferentes tipos de equipamentos.
- **Tratamento Global de Exceções:** Implementação de `@RestControllerAdvice` para interceptar erros. O sistema devolve respostas JSON padronizadas (ex: 404 Customizado, 400 Bad Request para erros de validação e quebra de regras de negócio).
- **Validação de Dados:** Uso do `spring-boot-starter-validation` (ex: `@NotBlank`, `@Positive`) garantindo a integridade dos dados na entrada.

## 🔐 Segurança e Controle de Acesso (RBAC)
A API possui um filtro de segurança (`SecurityFilterChain`) que bloqueia rotas não autorizadas e define patentes de acesso para a equipe:
- **Role `TECNICO`:** Permissão estrita para consultas de leitura (método `GET`) para bipar peças e conferir o estoque no galpão.
- **Role `GERENTE`:** Acesso total para criar eventos, despachar para manutenção e cadastrar/excluir itens (métodos `POST`, `PUT`, `DELETE`, `PATCH`).

## 🚚 Módulo de Logística e Eventos
* **Ordem de Serviço (Eventos):** Implementação de um "Carrinho de Compras" utilizando o relacionamento `@ManyToMany` do Hibernate. O sistema gerencia a saída e o retorno de equipamentos para shows específicos.
* **Automação de Status:** Inteligência de negócio no motor da API. Ao alocar um equipamento em uma OS, o status transita automaticamente de `DISPONIVEL` para `EM_USO`.
* **Controle de Avarias:** Rota específica para devolução de peças estragadas. O sistema aceita parâmetro dinâmico via URL (`?statusRetorno=MANUTENCAO`) para isolar peças que precisam de reparo na bancada.
* **Travas de Segurança (Anti-Bug):** Validações nativas no `Service` que impedem a alocação de equipamentos danificados em novos eventos e bloqueiam ações não permitidas.

## 🔌 Endpoints e Documentação (Swagger)
A API dispensa o uso exclusivo do Postman para testes. Toda a documentação e mapeamento de rotas é gerada automaticamente pelo **Swagger**.
Para testar a API com uma interface gráfica interativa, rode o projeto e acesse:
`http://localhost:8080/swagger-ui/index.html`

**Principais Rotas:**
- `GET /api/equipamentos` - Lista o inventário completo.
- `GET /api/equipamentos/{idQrCode}` - Busca a ficha completa de um equipamento.
- `DELETE /api/equipamentos/{idQrCode}` - Dá baixa (exclui) um equipamento.
- `PATCH /api/equipamentos/{idQrCode}/status` - Atualização rápida de status.
- `POST /api/equipamentos/painel-led` (Separado por domínio: cabo, estrutura, iluminacao, audio).
- `POST /api/eventos` - Criação de Ordem de Serviço.
- `POST /api/eventos/{eventoId}/equipamentos/{idQrCode}` - Aloca a peça no caminhão do evento.