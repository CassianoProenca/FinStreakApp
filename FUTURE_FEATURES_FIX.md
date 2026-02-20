# 🚀 Future Features & Technical Fixes - FinStreak

Este documento lista as funcionalidades ausentes, melhorias de UX e correções técnicas identificadas durante a análise da arquitetura atual.

---

## 🛑 Prioridade Alta: Gaps de Funcionalidade (Melhoria Imediata)

### 1. Gestão Dinâmica de Categorias
*   **Problema:** Atualmente as categorias são enviadas como `String` livre nos payloads de transações e orçamentos. Isso causa inconsistência nos dados (ex: "Saúde" vs "Saude").
*   **Solução:** 
    *   Implementar `CategoryController` com `GET /api/categories`.
    *   Permitir que o usuário defina cores e ícones para cada categoria.
    *   Validar no backend se a categoria enviada no `CreateTransactionRequest` existe.

### 2. Estorno e Edição de Depósitos em Metas
*   **Problema:** O endpoint `POST /api/goals/{id}/deposit` apenas adiciona valor. Não há como corrigir um erro de digitação ou remover um depósito feito indevidamente.
*   **Solução:** 
    *   Implementar `DELETE /api/goals/deposits/{id}`.
    *   **Lógica de Reversão:** Ao deletar um depósito, o `currentAmount` da meta deve ser subtraído e o status `COMPLETED` deve ser revertido para `ACTIVE` se o novo valor for menor que o `targetAmount`.

### 3. Visibilidade de Transações Futuras (Recorrência)
*   **Problema:** O sistema possui o `ProcessRecurringTransactionsService`, mas o usuário só vê a transação depois que o Job a cria no banco.
*   **Solução:** 
    *   Criar endpoint `GET /api/transactions/upcoming` para listar projeções de gastos fixos (Aluguel, Internet, etc.) baseados no campo `isRecurring`.

---

## 📈 Prioridade Média: Dashboards e Insights

### 4. Histórico de Evolução Patrimonial
*   **Problema:** O dashboard atual mostra apenas o "mês atual". Não há dados para plotar gráficos de linha ou barras de evolução.
*   **Solução:** 
    *   Endpoint `GET /api/dashboard/history?months=6` que retorne o saldo final e balanço (Receita - Despesa) de cada um dos últimos meses.

### 5. Vínculo Automático: Transação ↔ Meta
*   **Problema:** Atualmente o usuário precisa criar uma transação de despesa (ex: "Guardando para Meta") e DEPOIS fazer um depósito na meta. São dois passos manuais.
*   **Solução:** 
    *   Permitir que ao criar uma transação, o usuário envie um `goalId` opcional. O sistema faria o débito no saldo e o aporte na meta em uma única operação atômica.

---

## 🎨 Prioridade Baixa: UX e Gamificação

### 6. Sistema de "Proteção de Streak"
*   **Problema:** Se o usuário esquecer um dia, o streak volta a zero, o que pode ser desmotivador (churn).
*   **Solução:** 
    *   Implementar o "Streak Freeze" (item que o usuário pode ganhar ou "comprar" com XP para proteger a ofensiva por 24h sem atividade).

### 7. Exportação de Dados
*   **Melhoria:** Permitir exportar o histórico de transações em CSV ou PDF para fins de declaração ou controle externo.
