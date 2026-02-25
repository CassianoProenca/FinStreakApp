package com.financial.app.application.usecase;

import com.financial.app.application.ports.in.WithdrawFromGoalUseCase;
import com.financial.app.application.ports.out.GoalHistoryPort;
import com.financial.app.application.ports.out.LoadGoalsPort;
import com.financial.app.application.ports.out.SaveGoalPort;
import com.financial.app.application.ports.out.SaveTransactionPort;
import com.financial.app.domain.exception.BusinessException;
import com.financial.app.domain.exception.ResourceNotFoundException;
import com.financial.app.domain.model.Goal;
import com.financial.app.domain.model.GoalDeposit;
import com.financial.app.domain.model.Transaction;
import com.financial.app.domain.model.enums.GoalStatus;
import com.financial.app.domain.model.enums.TransactionCategory;
import com.financial.app.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawFromGoalService implements WithdrawFromGoalUseCase {

    private final LoadGoalsPort loadGoalsPort;
    private final SaveGoalPort saveGoalPort;
    private final GoalHistoryPort goalHistoryPort;
    private final SaveTransactionPort saveTransactionPort;

    @Override
    public GoalDeposit execute(UUID userId, UUID goalId, BigDecimal amount, String description) {
        // 1. Carregar a meta e verificar se pertence ao usuário
        Goal goal = loadGoalsPort.loadByUserId(userId).stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        // 2. Verificar se a meta possui saldo suficiente
        if (goal.getCurrentAmount().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente na meta para resgate");
        }

        // 3. Atualizar o valor atual da meta
        BigDecimal newAmount = goal.getCurrentAmount().subtract(amount);
        goal.setCurrentAmount(newAmount);

        // 4. Se a meta estava concluída e o saldo caiu abaixo do alvo, volta para em progresso
        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getCurrentAmount().compareTo(goal.getTargetAmount()) < 0) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        saveGoalPort.save(goal);

        // 5. Registrar no histórico da meta (como valor negativo no amount ou apenas registrar a retirada)
        // Optamos por registrar o valor positivo no histórico mas a descrição indica resgate
        // ou poderíamos permitir valores negativos na tabela. Vamos usar valor negativo para facilitar somas se houver.
        GoalDeposit withdrawal = GoalDeposit.builder()
                .goalId(goalId)
                .amount(amount.negate())
                .description("Resgate: " + (description != null ? description : "Resgate de valores"))
                .transactionDate(LocalDateTime.now())
                .build();

        GoalDeposit savedWithdrawal = goalHistoryPort.save(withdrawal);

        // 6. Criar transação de resgate (GOAL_WITHDRAWAL) para aumentar saldo disponível
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .description("Resgate - " + goal.getTitle())
                .type(TransactionType.GOAL_WITHDRAWAL)
                .category(TransactionCategory.OTHER)
                .date(LocalDateTime.now())
                .iconKey(goal.getIconKey())
                .goalId(goalId)
                .build();
        saveTransactionPort.save(transaction);

        return savedWithdrawal;
    }
}
