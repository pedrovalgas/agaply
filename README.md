# Agaply API - Sistema de Gestão para Mercados (PDV) 🛒

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

A **Agaply API** é um backend robusto e escalável desenvolvido para gerenciar as operações diárias de pequenos mercados e comércios. O projeto foca em **arquitetura limpa**, **segurança** e **confiabilidade**, utilizando as práticas mais modernas do ecossistema Java.

---

## 🚀 Tecnologias & Arquitetura

O projeto foi construído visando um cenário de produção real:

- **Core:** Java 17, Spring Boot 3, Maven.
- **Banco de Dados:** PostgreSQL.
- **Segurança:** Spring Security 6 com autenticação **Stateless via JWT**. Controle de acesso (RBAC) e criptografia de senhas (BCrypt).
- **Qualidade & Testes:**
    - **Testcontainers:** Testes de integração rodando contra um banco PostgreSQL real em contêineres Docker.
    - **JUnit 5 & Mockito:** Testes unitários para regras de negócio complexas.
- **DevOps:**
    - **Docker & Docker Compose:** Containerização completa da aplicação.
    - **CI/CD (GitHub Actions):** Pipeline automatizado que testa, compila e publica a imagem no Docker Hub a cada push.
- **Ferramentas:** MapStruct (DTOs), Lombok, Bean Validation.
- **Documentação:** Swagger UI (OpenAPI 3) com suporte a autenticação Bearer.

---

## ⚙️ Funcionalidades Principais

- **Controle de Vendas Transacional:** Processamento atômico de vendas. Se o estoque falhar ou o produto não existir, a venda é revertida (Rollback automático).
- **Gestão Inteligente de Estoque:**
    - Baixa automática de estoque ao realizar venda.
    - Devolução automática de estoque em caso de cancelamento.
    - Validação de estoque insuficiente e estoque mínimo.
- **Catálogo de Produtos:** Vínculo com Fornecedores e Categorias.
- **Soft Delete:** Exclusão lógica para manter histórico de Fornecedores e Produtos.
- **Tratamento de Erros:** `GlobalExceptionHandler` para padronizar respostas HTTP (400, 404, 409) com mensagens claras para o frontend.

---

## 🛠️ Como Rodar o Projeto

### Opção 1: Via Docker (Recomendado)
Se você tem o Docker instalado, não precisa configurar Java ou Postgres na sua máquina.

1. Clone o repositório:
```bash
git clone https://github.com/pedrovalgas/agaply.git
cd agaply
```

Suba o ambiente completo:

```bash
docker-compose up -d
```

A API estará rodando em: http://localhost:8080

---

### Opção 2: Rodar Localmente
Certifique-se de ter o Java 17 e o Maven instalados.

Tenha um banco PostgreSQL rodando localmente (ou ajuste o application.properties para apontar para o seu banco).

Execute:

```bash
mvn spring-boot:run
```

### 📚 Documentação da API (Swagger)
Com a aplicação rodando, acesse a documentação interativa. Você pode testar todos os endpoints diretamente pelo navegador.

👉 Acesse: http://localhost:8080/swagger-ui.html

Nota: A maioria dos endpoints é protegida. Você precisará criar um usuário (ou usar o admin padrão), fazer login na rota /auth/login, copiar o Token JWT e clicar no cadeado "Authorize" no topo do Swagger.

---

### 🧪 Testes Automatizados
Este projeto contém testes das classes e métodos mais importantes. Foi utilizado Testcontainers para garantir que os testes de integração rodem em um ambiente idêntico ao de produção.

Para rodar a suíte de testes:

```bash
mvn test
```

Isso irá subir automaticamente contêineres Docker do PostgreSQL, rodar os testes e derrubá-los ao final.

---

### 📦 CI/CD e Docker Hub
Este repositório conta com um pipeline de CI/CD configurado via GitHub Actions. A cada atualização na branch main:

O código é baixado na nuvem.

O ambiente Java é configurado.

Os testes (com Testcontainers) são executados.

Se aprovado, a imagem Docker é construída e publicada automaticamente no Docker Hub.

#### 🐳 Imagem Oficial: docker pull pedrovalgas/agaply-api:latest

---

### Autor
Desenvolvido por Pedro Lucas Portes Valgas
