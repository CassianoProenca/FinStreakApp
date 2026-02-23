# 🚀 Future Features & Technical Fixes - FinStreak

Este documento lista as funcionalidades ausentes, melhorias de UX e correções técnicas identificadas para as próximas iterações.

---

## 🛑 Prioridade Alta: Gaps de Funcionalidade (Melhoria Imediata)

### 1. Gestão Dinâmica de Categorias
*   **Problema:** Atualmente as categorias são enviadas como `String` livre. Isso causa inconsistência nos dados (ex: "Saúde" vs "Saude").
*   **Solução:** 
    *   Implementar `CategoryController` com `GET /api/categories`.
    *   Validar no backend se a categoria enviada no `CreateTransactionRequest` existe.
    *   Permitir que o usuário defina cores e ícones para cada categoria.

### 2. Extrato Mensal Inteligente (Data-Only)
*   **Conceito:** Como o foco é App Mobile, não geraremos arquivos (PDF/CSV) no servidor. O Backend deve fornecer os dados puros e o Frontend faz a formatação visual ("Juicy UI").
*   **Solução:**
    *   Criar endpoint `GET /api/transactions/statement?month=X&year=Y`.
    *   **Performance:** Forçar filtro por mês para evitar sobrecarga no banco de dados e payloads gigantes.
    *   **Dados:** Retornar lista de transações + Resumo Mensal (Entradas, Saídas, Balanço e Saldo Inicial/Final).

### 3. Gestão de Parcelamentos e Recorrência (Upcoming)
*   **Problema:** O usuário precisa ver o impacto de compras parceladas (ex: celular em 12x) antes delas serem efetivadas no saldo atual.
*   **Solução:** 
    *   **Projeção:** Endpoint `GET /api/transactions/upcoming` para listar o que está por vir nos próximos meses.
    *   **Parcelamento Automático:** Permitir criar uma transação com `installments: 12`. O sistema deve projetar essas 12 ocorrências.
    *   **Cancelamento:** Possibilidade de remover uma recorrência futura caso o usuário devolva o produto ou pare de pagar um serviço, garantindo que o "Saldo Projetado" seja corrigido.

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

### ✅ Concluído (Recentemente Implementado)
*   Integridade de Saldo (Disponível vs Patrimônio).
*   Funcionalidade de Resgate de Metas (Withdrawal).
*   Vínculo Automático Transação ↔ Meta via `GOAL_ALLOCATION`.
