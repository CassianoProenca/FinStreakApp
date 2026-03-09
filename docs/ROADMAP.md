# Roadmap e Funcionalidades: FinStreak

O FinStreak é focado em transformar a gestão financeira em um hábito gratificante. Abaixo estão as funcionalidades implementadas e o planejamento futuro.

---

## 🚀 Funcionalidades Atuais

### 1. Gestão Financeira
- **Transações:** Cadastro de receitas/despesas, saldo disponível e histórico.
- **Recorrência:** Processamento automático de lançamentos mensais/semanais.
- **Metas (Goals):** Criação de objetivos de economia com barra de progresso.
- **Orçamentos (Budgets):** Limites mensais por categoria com alertas de estouro.
- **Extrato Mensal:** Endpoint `GET /api/transactions/statement` com saldo de abertura, totais por tipo, gastos por categoria e lista de transações do mês.

### 2. Gamificação
- **Streaks (Ofensivas):** Contador de dias seguidos de atividade.
- **Níveis e XP:** Acúmulo de experiência para subir de nível.
- **Medalhas:** Recompensas permanentes (Primeiros Passos, Poupador Elite, etc).
- **Missões Diárias:** Objetivos que resetam diariamente para ganhar XP extra.

### 3. Segurança e Acesso
- **Auth:** Login seguro via JWT.
- **Recuperação:** Fluxo de "Esqueci minha senha" integrado com Resend.

---

## 🛠️ Melhorias Técnicas e Correções (FIX)

### Integridade de Saldo
- [x] Renomear `balance` para `availableBalance` (Líquido).
- [x] Adicionar `totalEquity` (Saldo + Valor alocado em Metas).
- [x] Vínculo automático `GOAL_ALLOCATION` ao depositar em metas.
- [x] Criar funcionalidade de Resgate de Metas (`Withdrawal`).

---

## 📅 Futuro do Projeto (Próximas Iterações)

### Alta Prioridade
- [ ] **Categorias Dinâmicas:** API para gerenciar nomes, cores e ícones de categorias.
- [ ] **Parcelamentos:** Projeção de compras parceladas (ex: 12x) no saldo futuro.

### Médio Prazo
- [ ] **Histórico Patrimonial:** Gráfico de evolução do saldo nos últimos 6-12 meses.
- [ ] **Proteção de Streak:** Item "Streak Freeze" para proteger a ofensiva por 24h.

### Gamificação Avançada
- [ ] **Feedback Sensorial:** Metadados na API para o App disparar sons, vibração e animações (confete) no momento do "Level Up".
