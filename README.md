# 📦 API de Gestão de Estoque - Produção Audiovisual (Backend)

Uma API RESTful desenvolvida em **Java** com **Spring Boot** para gerenciar o inventário físico, status e logística de equipamentos de eventos (Áudio, Iluminação, Estrutura e Energia).

## 🎯 O Problema que Resolve
No setor de produção de eventos, o controle de entrada e saída de equipamentos frequentemente sofre com perdas, quebras não reportadas e planilhas desatualizadas. Este sistema fornece um motor de backend robusto para:
- Rastreamento individual de equipamentos via **QR Code**.
- Herança de tabelas no banco de dados para tratar atributos específicos (ex: metragem de cabos vs. voltagem de iluminação) sem poluir o esquema.
- Bloqueio de dados inconsistentes na entrada.

## 🚀 Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **PostgreSQL** (Banco de dados relacional)
- **Maven** (Gerenciamento de dependências)

## 🏗️ Arquitetura e Padrões Aplicados
- **Arquitetura em Camadas:** Divisão clara entre `Controller`, `Service` e `Repository`.
- **Orientação a Objetos Avançada:** Uso de classes abstratas e `@Inheritance(strategy = InheritanceType.JOINED)` no JPA para modelagem eficiente de diferentes tipos de equipamentos.
- **Tratamento Global de Exceções:** Implementação de `@RestControllerAdvice` para interceptar erros e devolver respostas JSON padronizadas (ex: 404 Customizado, 400 Bad Request com lista de erros).
- **Validação de Dados:** Uso do `spring-boot-starter-validation` (ex: `@NotBlank`, `@Positive`) garantindo a integridade dos dados na entrada.

## 🔌 Endpoints (Guichês da API)

### Equipamentos (Geral)
- `GET /api/equipamentos` - Lista o inventário completo.
- `GET /api/equipamentos/{idQrCode}` - Busca a ficha completa de um equipamento específico.
- `DELETE /api/equipamentos/{idQrCode}` - Dá baixa (exclui) um equipamento do sistema.
- `PATCH /api/equipamentos/{idQrCode}/status` - Atualização rápida de status (DISPONIVEL, MANUTENCAO, EVENTO).

### Cadastros e Atualizações Específicas (POST / PUT)
As rotas de criação e edição completa são separadas por domínio para garantir os atributos corretos:
- `/api/equipamentos/cabo`
- `/api/equipamentos/iluminacao`
- `/api/equipamentos/audio`
- `/api/equipamentos/estrutura`

## 🚀 Novidades e Evolução da Arquitetura

* **Módulo de Logística (Eventos):** Implementação de um "Carrinho de Compras" utilizando o relacionamento `@ManyToMany` do Hibernate. Agora o sistema gerencia a saída e o retorno de equipamentos para shows e eventos específicos.
* **Automação de Status:** O motor do sistema foi aprimorado com inteligência de negócio. Ao alocar um equipamento em uma OS (Ordem de Serviço), o status transita automaticamente de `DISPONIVEL` para `EM_USO`.
* **Controle de Avarias:** Rota específica para devolução de peças estragadas. O sistema aceita o parâmetro dinâmico via URL (`?statusRetorno=MANUTENCAO`) para isolar cabos e painéis de LED que precisam de reparo na bancada.
* **Travas de Segurança (Anti-Bug):** Validações nativas no `Service` que impedem a alocação de equipamentos danificados em novos eventos e bloqueiam a criação de OS duplicadas na agenda.
* **Filtros Avançados:** Habilitada no Controller, permitindo consultar cirurgicamente o banco de dados filtrando itens por status diretamente pela URL.