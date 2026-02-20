# Funcionalidades do FinStreak

O FinStreak é uma plataforma completa de gestão financeira pessoal que utiliza elementos de gamificação para incentivar a disciplina e a constância no controle de gastos e economias.

## 1. Gestão de Usuários e Perfil
- **Cadastro e Autenticação:** Sistema de registro de novos usuários e login seguro via JWT.
- **Recuperação de Senha:** Funcionalidade de "Esqueci minha senha" com envio de tokens por e-mail para redefinição.
- **Onboarding:** Fluxo guiado para novos usuários configurarem suas preferências iniciais.
- **Perfil Personalizável:** Atualização de dados cadastrais, preferências de tema (Dark/Light) e upload de avatar.

## 2. Controle de Transações
- **Registro de Atividades:** Lançamento de receitas e despesas com descrição, valor, data e categoria.
- **Transações Recorrentes:** Suporte a lançamentos que se repetem (mensal, semanal, etc.), com processamento automático de instâncias futuras.
- **Histórico Detalhado:** Listagem e consulta de transações passadas com filtros e paginação.
- **Saldo em Tempo Real:** Cálculo do saldo atual e histórico de balanço acumulado.

## 3. Planejamento e Orçamentos (Budgets)
- **Definição de Limites:** Criação de orçamentos por categoria para controlar gastos mensais.
- **Acompanhamento de Gastos:** Visualização do quanto já foi gasto em relação ao limite estabelecido em cada categoria.
- **Alertas de Orçamento:** Notificações quando o usuário se aproxima ou ultrapassa o limite definido.

## 4. Metas Financeiras (Goals)
- **Criação de Objetivos:** Definição de metas de economia (ex: "Viagem", "Reserva de Emergência") com valor alvo e data desejada.
- **Depósitos em Metas:** Funcionalidade para alocar dinheiro especificamente para uma meta, acompanhando o progresso percentual.
- **Histórico de Depósitos:** Registro de todas as movimentações financeiras vinculadas a uma meta específica.

## 5. Gamificação (O diferencial)
- **Sistema de Streaks (Ofensivas):** Contagem de dias consecutivos em que o usuário registra atividades financeiras. O app incentiva a manter a "chama acesa".
- **Níveis e XP:** Acúmulo de experiência (XP) ao realizar ações no app (registrar transações, bater metas), permitindo que o usuário suba de nível.
- **Medalhas e Conquistas (Achievements):** Sistema de recompensas visuais por marcos alcançados, como:
    - *Primeiros Passos:* Registro da primeira atividade.
    - *Uma Semana de Foco:* Manter um streak de 7 dias.
    - *Mestre da Constância:* Manter um streak de 30 dias.
- **Perfil de Gamificação:** Visualização clara do nível atual, progresso de XP e medalhas conquistadas.

## 6. Notificações e Engajamento
- **Alertas do Sistema:** Notificações sobre novos níveis alcançados, medalhas ganhas ou aumento de streak.
- **Lembretes de Atividade:** Alertas para ajudar o usuário a não perder sua ofensiva (streak).
- **Central de Notificações:** Interface para visualizar e marcar como lidas as interações recentes do sistema.

## 7. Dashboard e Insights
- **Resumo Geral:** Painel principal com visão consolidada do saldo, últimas transações e progresso das metas.
- **Visualização de Dados:** Gráficos e indicadores que facilitam o entendimento da saúde financeira do usuário.
