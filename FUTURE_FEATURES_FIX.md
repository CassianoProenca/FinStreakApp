# 🚀 Future Features & Technical Fixes - FinStreak

Este documento lista as funcionalidades ausentes, melhorias de UX e correções técnicas identificadas para as próximas iterações.

---

## 📈 Prioridade Média: Dashboards e Insights

### 4. Histórico de Evolução Patrimonial
*   **Problema:** O dashboard atual mostra apenas o "mês atual". Não há dados para plotar gráficos de linha de evolução de longo prazo.
*   **Solução:**
    *   Endpoint `GET /api/dashboard/history?months=6` que retorne o saldo disponível e patrimônio total de cada um dos últimos meses.

---

## 🎨 Prioridade Baixa: UX e Gamificação

### 5. Sistema de "Proteção de Streak" (Streak Freeze)
*   **Problema:** Se o usuário esquecer um dia, o streak volta a zero, o que causa desmotivação.
*   **Solução:**
    *   Implementar o "Streak Freeze" (item que protege a ofensiva por 24h sem atividade). Pode ser conquistado via XP ou por marcos de economia.

### 6. "Juicy UI" e Feedback Sensorial (Dopamina)
*   **Conceito:** Sugestões de UX (nível BigTech) para aumentar o engajamento através de micro-interações e estímulos sensoriais.
*   **Divisão de Responsabilidades:**
    *   **Frontend (Execução Sensorial):**
        *   Reproduzir arquivos de som (`.mp3`/`.wav`) de fanfarra ou moedas.
        *   Acionar a vibração do celular (*Haptic Feedback*) via APIs nativas.
        *   Renderizar animações de confete (Canvas/Lottie) no momento da conquista.
        *   Gerenciar o player de música de fundo relaxante.
    *   **Backend (Gatilhamento e Inteligência):**
        *   Enviar **Metadados** nas respostas da API (ex: campo `"goalReached": true` ou `"levelUp": true`).
        *   Calcular e retornar o XP exato ganho em cada ação para animações de `+XP`.
        *   Enviar Notificações em tempo real via WebSockets/Push para eventos de background que geram dopamina (ex: "Seu Streak de 7 dias foi atingido!").

---

## 📅 Planejado Futuramente

### Gestão Dinâmica de Categorias
*   **Problema:** Atualmente as categorias são um enum fixo no backend. O frontend não tem como listá-las dinamicamente, o que torna difícil exibir opções ao usuário sem hardcode no app.
*   **Solução:**
    *   Implementar `CategoryController` com `GET /api/categories`.
    *   Retornar as categorias disponíveis com nome, cor e ícone sugerido.
    *   Permitir que o usuário defina cores e ícones customizados por categoria.

---

### ✅ Concluído (Recentemente Implementado)
*   Integridade de Saldo (Disponível vs Patrimônio).
*   Funcionalidade de Resgate de Metas (Withdrawal).
*   Vínculo Automático Transação ↔ Meta via `GOAL_ALLOCATION`.
*   Documentação Swagger completa: `@Schema` em todos os DTOs de request/response, `@Parameter` em todos os path variables e query params, `@ApiResponse` com códigos 400/401/403/404 em todos os endpoints, exemplos de request body em todos os controllers.
*   Concorrência no CI/CD: adicionado `concurrency: cancel-in-progress` no workflow de deploy para evitar conflitos de deploy paralelo.
*   **Extrato Mensal:** `GET /api/transactions/statement?month=X&year=Y` — retorna saldo inicial, totais por tipo, gastos por categoria e lista de transações do mês.
*   **Parcelamentos e Upcoming:** campo `installments` na criação de transações gera parcelas filhas automaticamente; `GET /api/transactions/upcoming` lista parcelas futuras e projeções de recorrentes nos próximos 3 meses.

---

## ✅ 30 Gaps Resolvidos (2026-02-26)

### 🔴 Críticos

| # | Área | Fix aplicado |
|---|------|-------------|
| 1 | Transações | `DeleteTransactionService` agora chama `deleteByParentId(id)` antes de deletar o pai — elimina violação de FK em parcelamentos. Novo método `deleteByParentTransactionId` adicionado ao repositório com `@Modifying`. |
| 2 | Metas | `DeleteGoalService` agora chama `goalHistoryPort.deleteByGoalId(id)` antes de deletar a meta — elimina violação de FK em `fin_goal_history`. Novo método `deleteByGoalId` adicionado ao repositório. |
| 3 | Auth | `UpdateUserProfileService` (PUT `/api/users/me`) não permite mais alterar senha. Troca de senha exige `POST /api/auth/change-password` que valida a senha atual. |

### 🟠 Altos

| # | Área | Fix aplicado |
|---|------|-------------|
| 4 | Transações | `UpdateTransactionService` propaga `amount`, `description`, `type`, `category`, `iconKey` para todas as parcelas filhas quando o pai é editado. Novo método `loadChildInstallments(parentId)` adicionado ao `LoadTransactionPort`. |
| 5 | Transações | `ProcessRecurringTransactionsService` agora tem lógica separada para `WEEKLY` (janela semanal Seg–Dom) vs `MONTHLY` (janela mensal com `repeatDay`). |
| 6 | Metas | `GET /api/goals/{id}/history` agora verifica ownership — lança 404 se a meta não pertencer ao usuário autenticado (IDOR corrigido). |
| 7 | Notificações | `PATCH /api/notifications/{id}/read` agora carrega a notificação, valida `userId` e lança 403 se não pertencer ao usuário (IDOR corrigido). |
| 8 | Notificações | `PersistentNotificationAdapter` verifica `user.preferences.notificationsEnabled` antes de persistir qualquer notificação. |
| 9 | Dashboard | `GetDashboardSummaryService` calcula `openingBalance` (todas as transações antes do mês solicitado) e o inclui no `availableBalance` — alinhado com o extrato mensal. |
| 10 | Onboarding | `CompleteOnboardingService` não cria mais a transação de salário no dia 0. A `monthlyIncome` é armazenada apenas no campo do usuário. |
| 11 | Budget | `BudgetService` dispara `BUDGET_ALERT` quando gasto ≥ 80 % ou ≥ 100 % do limite. `CreateTransactionService` chama o check após cada despesa. |
| 12 | Auth | Sem escopo desta entrega (requer blacklist de JWT / refresh token — ver item futuro abaixo). |

### 🟡 Médios

| # | Área | Fix aplicado |
|---|------|-------------|
| 13 | Transações | Novo endpoint `GET /api/transactions/{id}` — retorna transação individual com validação de ownership. |
| 14 | Metas | `GoalStatus.CANCELLED` adicionado ao enum. |
| 15 | Metas | `DepositInGoalService` lança `BusinessException` se a meta estiver `COMPLETED` ou `CANCELLED`. |
| 16 | Metas | Novo endpoint `GET /api/goals/{id}` — retorna meta individual com validação de ownership. |
| 17 | Gamificação | `FIRST_STEPS`, `STREAK_7`, `STREAK_30`, `GOAL_SETTER` agora concedem XP (200, 500, 1500, 300 respectivamente). |
| 18 | Gamificação | `ELITE_SAVER` é concedido em `CheckStreakService` quando o usuário atinge nível 10 (XP bônus: 2000). |
| 19 | Gamificação | Perfil inicial não é mais salvo antes do `execute()` — é sempre persistido ao final, garantindo que o ID nunca seja `null` na resposta. |
| 20 | Auth | Novo endpoint `GET /api/users/me` — retorna `id`, `name`, `email`, `avatarUrl`, `onboardingCompleted`, `monthlyIncome`. |
| 21 | Auth | TTL do reset de senha corrigido para **60 minutos** (alinhado com Swagger). |
| 22 | Auth | Sem escopo desta entrega (requer refresh token — ver item futuro abaixo). |
| 23 | Notificações | `DepositInGoalService` dispara `GOAL_COMPLETED` quando a meta é concluída. |
| 24 | Dashboard | Campo `achievements` retorna conquistas all-time (comportamento mantido, Swagger atualizado implicitamente pelos comentários do código). |
| 25 | Onboarding | Novo endpoint `PATCH /api/users/me/income` — permite atualizar `monthlyIncome` após onboarding concluído. |

### 🟢 Baixos

| # | Área | Fix aplicado |
|---|------|-------------|
| 26 | Transações | `GET /api/transactions` aceita novos query params: `description` (busca parcial case-insensitive), `sortBy` e `sortDir`. `TransactionQuery` atualizado com esses campos. |
| 27 | Notificações | Novos endpoints: `PATCH /api/notifications/read-all`, `GET /api/notifications/unread-count`, `DELETE /api/notifications/{id}`. |
| 28 | Edu | Fora do escopo desta entrega (tabela `edu_tips` sem model/use-case definidos). |
| 29 | Arquitetura | `OnboardingCommand` não importa mais DTOs de infraestrutura. Criados `OnboardingExpenseItem` e `OnboardingGoalItem` como value objects de domínio. O controller faz o mapeamento DTO → domínio. |
| 30 | Performance | `UserJpaRepository.findAllIds()` usa `@Query("SELECT u.id FROM UserEntity u")` — projeta apenas UUIDs sem carregar entidades completas. |

---

## 🔮 Próximas Entregas

### Refresh Token / Logout (gaps #12 e #22)
*   Implementar blacklist de JWT ou refresh token para invalidar tokens após troca de senha ou logout explícito.

### Tabela `edu_tips` (gap #28)
*   Criar `EduTip` domain model, use-case `GetEduTipsUseCase` e `GET /api/edu-tips` com paginação.

### Histórico de Evolução Patrimonial
*   Endpoint `GET /api/dashboard/history?months=6` que retorne saldo disponível e patrimônio total dos últimos N meses.


