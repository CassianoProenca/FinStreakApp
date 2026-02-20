# 🚀 Fluxo do Usuário: Guia de Endpoints FinStreak

Este guia segue a sequência lógica de uso da plataforma, desde a criação da conta até a conquista de medalhas e acompanhamento de metas.

---

## 🏗️ Fase 1: Onboarding (Primeiro Acesso)

### 1. Criar Conta
O primeiro passo é registrar o usuário no sistema.
- **Endpoint:** `POST /api/auth/register`
- **JSON Exemplo:**
    ```json
    {
      "name": "Seu Nome",
      "email": "user@email.com",
      "password": "senha123"
    }
    ```

### 2. Autenticação (Login)
Obtenha o **Token JWT** necessário para todas as outras chamadas.
- **Endpoint:** `POST /api/auth/login`
- **JSON Exemplo:**
    ```json
    {
      "email": "user@email.com",
      "password": "senha123"
    }
    ```
- **Importante:** Guarde o token retornado e envie em todas as próximas requisições no Header: `Authorization: Bearer <seu_token>`.

### 3. Configuração Inicial (Onboarding)
Informa ao sistema que o usuário completou as configurações iniciais.
- **Endpoint:** `POST /api/onboarding/complete`
- **JSON Exemplo:** `{ "completed": true }`

---

## 💸 Fase 2: Gestão Financeira Diária

### 4. Criar Transações (O Hábito)
Registre suas receitas e despesas. **Fazer isso diariamente mantém seu Streak (Ofensiva) e XP ativos.**
- **Endpoint:** `POST /api/transactions`
- **JSON Exemplo:** 
    ```json
    {
      "description": "Compra no Mercado",
      "amount": 150.50,
      "type": "EXPENSE", 
      "category": "Alimentação",
      "transactionDate": "2026-02-20T15:00:00",
      "isRecurring": false
    }
    ```
- **Dica:** Use `type: "INCOME"` para ganhos e `type: "EXPENSE"` para gastos.

### 5. Definir Orçamentos (Planejamento)
Limite quanto quer gastar por categoria no mês para evitar desperdícios.
- **Endpoint:** `POST /api/budgets`
- **JSON Exemplo:** 
    ```json
    {
      "category": "Alimentação",
      "limitAmount": 1000.00,
      "month": 2,
      "year": 2026
    }
    ```

---

## 🎯 Fase 3: Conquista de Metas (O Gap de Progresso)

### 6. Criar uma Meta
Defina um objetivo de economia de médio ou longo prazo.
- **Endpoint:** `POST /api/goals`
- **JSON Exemplo:** 
    ```json
    {
      "title": "Reserva de Emergência",
      "targetAmount": 10000.00,
      "deadline": "2026-12-31T23:59:59",
      "iconKey": "shield"
    }
    ```

### 7. Registrar Progresso (Depósito na Meta)
**Este é o comando para "anotar que progrediu".** Ele retira o valor do seu saldo e aloca especificamente para esta meta.
- **Endpoint:** `POST /api/goals/{goalId}/deposit`
- **JSON Exemplo:** 
    ```json
    {
      "amount": 200.00,
      "description": "Economia da semana"
    }
    ```
- **Resultado:** Atualiza a barra de progresso (%), gera XP extra e pode desbloquear medalhas de poupador.

---

## 🏆 Fase 4: Feedback e Recompensa (Gamificação)

### 8. Consultar Dashboard
Veja um resumo consolidado da sua saúde financeira na tela inicial.
- **Endpoint:** `GET /api/dashboard/summary`
- **O que retorna:** Saldo total, total de receitas/despesas do mês, progresso das metas e status do seu Streak.

### 9. Ver seu Nível e Medalhas
Acompanhe sua evolução comportamental.
- **Perfil de Gamificação:** `GET /api/gamification/profile` (Mostra Nível, XP Total e Streak atual em dias).
- **Conquistas:** `GET /api/gamification/achievements` (Lista medalhas ganhas como "Primeiros Passos" ou "Poupador de Elite").

### 10. Central de Notificações
Fique por dentro de alertas de sistema, níveis alcançados ou avisos de orçamento.
- **Listar Notificações:** `GET /api/notifications`
- **Marcar como Lida:** `PATCH /api/notifications/{id}/read`

---

## ⚙️ Fase 5: Manutenção e Ajustes

### 11. Perfil e Preferências
- **Mudar Tema (Light/Dark):** `PUT /api/settings` -> `{"theme": "DARK", "language": "pt-BR"}`.
- **Editar Dados de Perfil:** `PUT /api/user/profile` -> `{"name": "Novo Nome", "avatarUrl": "http://..."}`.

### 12. Recuperação de Acesso
- **Esqueci Senha:** `POST /api/auth/forgot-password` -> `{"email": "user@email.com"}`.
- **Resetar Senha:** `POST /api/auth/reset-password` (Requer o token enviado por e-mail).

---

### 💡 Dica Pro: O Ciclo de Sucesso
Para garantir que o sistema funcione perfeitamente:
1. Faça **Login (2)** para ter o token.
2. Registre uma **Transação (4)** para iniciar seu Streak.
3. Faça um **Depósito (7)** para ver sua meta sair do 0%.
4. Cheque o **Dashboard (8)** para ver o impacto visual do seu progresso.
