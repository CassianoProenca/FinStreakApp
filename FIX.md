# 🛠️ Correções de Integridade Financeira (FIX)

Este documento detalha falhas na lógica de saldo e movimentação de metas que precisam de correção para garantir a precisão dos dados do usuário e desenvolvedores.

---

## 🛑 Bug: Saldo Disponível Incorreto (Inconsistência Home vs. Metas)

### **Descrição**
Atualmente, o saldo exibido no Dashboard (`balance`) não subtrai os valores alocados em Metas (`Goals`). 
*   **Cenário Atual:** Se o usuário tem R$ 1.000,00 de saldo e deposita R$ 200,00 na meta "Reserva", a Home continua mostrando R$ 1.000,00.
*   **Impacto:** O usuário pode gastar o mesmo dinheiro duas vezes, pois o sistema não separa o "Saldo Líquido" do "Saldo Alocado".

### **Ação Necessária**
1.  **Alterar `GetAllTimeBalanceService` e `GetDashboardSummaryService`:**
    *   O cálculo do saldo deve ser: `(Total Receitas - Total Despesas) - Total de Depósitos em Metas`.
2.  **Novo Conceito:** Introduzir o termo **"Saldo Disponível"** (Líquido) vs. **"Patrimônio Total"** (Saldo + Metas).

---

## ⚠️ Feature Faltante: Resgate de Valores (Withdrawal)

### **Descrição**
Não há como o usuário retirar dinheiro de uma meta. Se ele guardou R$ 500,00 para uma viagem e teve uma emergência, o dinheiro fica "preso" virtualmente na meta.

### **Ação Necessária**
1.  **Criar Endpoint:** `POST /api/goals/{id}/withdraw`.
2.  **Lógica de Negócio:**
    *   Verificar se a meta possui saldo suficiente.
    *   Subtrair o valor do `currentAmount` da meta.
    *   Adicionar um registro de "Resgate" no histórico da meta.
    *   **Importante:** Ao resgatar, o valor deve "voltar" para o saldo disponível na Home.

---

## 📉 Registro de Histórico de Saldo

### **Descrição**
A tabela `goal_history` registra depósitos, mas não há um vínculo claro que mostre que aquele dinheiro saiu da conta corrente.

### **Ação Necessária**
*   Ao realizar um depósito em meta, criar automaticamente uma transação do tipo `GOAL_ALLOCATION` (novo tipo) para que o extrato mensal do usuário mostre para onde o dinheiro foi.
