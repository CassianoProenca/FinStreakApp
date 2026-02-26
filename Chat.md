Análise completa. Aqui estão os gaps encontrados, organizados por prioridade e área:

  ---
🔴 Crítico (bugs que causam erro 500)

┌─────┬────────────┬──────────────────────────────────────────────────────────────────────────────────────────────────────┐
│  #  │    Área    │                                               Problema                                               │
├─────┼────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 1   │ Transações │ Deletar a transação-pai de um parcelamento causa violação de FK — os filhos ficam órfãos e o banco   │
│     │            │ rejeita a exclusão                                                                                   │
├─────┼────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 2   │ Metas      │ Deletar uma meta que já teve depósitos causa violação de FK na tabela goal_history                   │
├─────┼────────────┼──────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 3   │ Auth       │ PUT /api/users/me permite trocar senha sem informar a senha atual — quem tiver o token roubado pode  │
│     │            │ bloquear o dono da conta                                                                             │
└─────┴────────────┴──────────────────────────────────────────────────────────────────────────────────────────────────────┘

  ---
🟠 Alto (comportamento incorreto / segurança)

┌─────┬──────────────┬────────────────────────────────────────────────────────────────────────────────────────────────────┐
│  #  │     Área     │                                              Problema                                              │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 4   │ Transações   │ Editar parcelamento atualiza só o pai — as 11 parcelas filhas ficam com valor antigo               │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 5   │ Transações   │ Recorrência WEEKLY nunca é processada corretamente (o job sempre cria apenas 1 instância/mês)      │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 6   │ Metas        │ GET /api/goals/{id}/history não verifica se a meta pertence ao usuário autenticado (IDOR)          │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 7   │ Notificações │ PATCH /api/notifications/{id}/read não verifica ownership (IDOR)                                   │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 8   │ Notificações │ A preferência notificationsEnabled do usuário é ignorada pelo backend                              │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 9   │ Dashboard    │ availableBalance no dashboard não inclui o saldo acumulado de meses anteriores — diverge do        │
│     │              │ extrato mensal                                                                                     │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 10  │ Onboarding   │ Cria uma transação de salário extra no dia 0, distorcendo o saldo histórico do usuário             │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 11  │ Budget       │ BUDGET_ALERT notification nunca é disparada — o tipo existe mas nenhum código o usa                │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ 12  │ Auth         │ Sem logout / invalidação de token — após troca de senha o token antigo continua válido por 24h     │
└─────┴──────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────┘

  ---
🟡 Médio (funcionalidade ausente)

┌─────┬──────────────┬──────────────────────────────────────────────────────────────────────────┐
│  #  │     Área     │                                 Problema                                 │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 13  │ Transações   │ Sem GET /api/transactions/{id}                                           │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 14  │ Metas        │ GoalStatus.CANCELLED documentado no Swagger mas não existe no enum       │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 15  │ Metas        │ Depósito permitido em meta já COMPLETED (pode ultrapassar 100%)          │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 16  │ Metas        │ Sem GET /api/goals/{id}                                                  │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 17  │ Gamification │ GOAL_SETTER, ELITE_SAVER, BUDGET_MASTER não concedem XP                  │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 18  │ Gamification │ ELITE_SAVER deveria ser concedido ao atingir nível 10, mas nunca é       │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 19  │ Gamification │ Profile de gamificação retorna id = null antes da primeira transação     │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 20  │ Auth         │ Sem GET /api/users/me (único modo de obter dados atualizados do perfil)  │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 21  │ Auth         │ TTL do reset de senha: 15 min no código vs. 1 hora na doc Swagger        │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 22  │ Auth         │ Sem refresh token (forçar re-login após 24h)                             │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 23  │ Notificações │ GOAL_COMPLETED é enum morto — nunca disparado                            │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 24  │ Dashboard    │ Campo achievements retorna conquistas all-time, mas Swagger diz "do mês" │
├─────┼──────────────┼──────────────────────────────────────────────────────────────────────────┤
│ 25  │ Onboarding   │ Sem como atualizar monthlyIncome após onboarding concluído               │
└─────┴──────────────┴──────────────────────────────────────────────────────────────────────────┘

  ---
🟢 Baixo (melhoria de UX / arquitetura)

┌─────┬──────────────┬────────────────────────────────────────────────────────────────────────────────┐
│  #  │     Área     │                                    Problema                                    │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 26  │ Transações   │ Sem filtro por texto/descrição e sem ordenação configurável                    │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 27  │ Notificações │ Sem mark-all-as-read, sem contagem de não lidas, sem delete                    │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 28  │ Edu          │ Tabela edu_tips existe no banco mas zero código implementado                   │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 29  │ Arquitetura  │ OnboardingCommand importa DTOs de infraestrutura (viola arquitetura hexagonal) │
├─────┼──────────────┼────────────────────────────────────────────────────────────────────────────────┤
│ 30  │ Performance  │ loadAllUserIds() carrega entidades completas só para extrair UUIDs             │
└─────┴──────────────┴────────────────────────────────────────────────────────────────────────────────┘

  ---
Total: 30 gaps — 3 críticos, 9 altos, 13 médios, 5 baixos.