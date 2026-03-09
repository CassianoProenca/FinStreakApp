# FinStreak - Gestão Financeira Gamificada 🚀

FinStreak é um aplicativo de finanças pessoais desenvolvido com **Java 21** e **Spring Boot 3.5.9**. O diferencial do projeto é o uso de **gamificação** (streaks, níveis e medalhas) para incentivar o hábito de controle financeiro e economia.

## 🛠️ Stack Tecnológica

- **Backend:** Java 21, Spring Boot 3.5.9
- **Database:** PostgreSQL 15 (Produção), H2 (Testes)
- **Migrações:** Flyway
- **Segurança:** Spring Security + JWT
- **E-mail:** Resend SDK
- **Arquitetura:** Hexagonal (Ports & Adapters)
- **Documentação API:** SpringDoc OpenAPI (Swagger UI)
- **CI/CD:** GitHub Actions + Docker + VPS

---

## 🏗️ Arquitetura do Projeto

O projeto segue a **Arquitetura Hexagonal**, garantindo a separação entre lógica de negócio e infraestrutura:

1. **Domain:** Modelos puros (`User`, `Transaction`, `Goal`, `Budget`).
2. **Application:** Portas de entrada (`UseCase`), saídas (`Port`) e serviços.
3. **Infrastructure:** Adaptadores (`Web`, `Persistence`, `Email`, `Security`).

---

## 📖 Guias de Documentação

Para facilitar o desenvolvimento, a documentação foi dividida por áreas:

- [🚀 **Guia de Endpoints**](./docs/API_GUIDE.md): Passo-a-passo de uso da API (Onboarding -> Gamificação).
- [⚙️ **Configuração de Infra**](./docs/INFRA_CONFIG.md): Guia de Resend, DNS, VPS e Deep Linking para React Native.
- [📋 **Roadmap & Features**](./docs/ROADMAP.md): Funcionalidades atuais, bugs conhecidos e planos futuros.

---

## 💻 Comandos Rápidos

```bash
# Build (pular testes)
./mvnw clean package -DskipTests

# Rodar Localmente
./mvnw spring-boot:run

# Rodar todos os testes
./mvnw test

# Rodar apenas testes unitários
./mvnw test -Dtest="!*IntegrationTest"

# Docker Compose (App + PostgreSQL)
docker-compose up
```

## 🔐 Configuração
O sistema utiliza variáveis de ambiente via arquivo `.env`. Veja o [.env.example](.env.example) para os nomes das chaves necessárias (JWT, DB, Resend, Swagger).

---

## 📡 API e Swagger
A documentação completa dos endpoints e modelos está disponível via Swagger UI no caminho:
`http://localhost:8080/swagger-ui.html`
*(Credenciais padrão: admin/admin)*
