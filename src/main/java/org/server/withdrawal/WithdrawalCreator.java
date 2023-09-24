package org.server.withdrawal;

import org.server.dao.Dao;
import org.server.model.Status;
import org.server.processor.ProcessingService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.server.withdrawal.WithdrawalService.*;

public class WithdrawalCreator {

    private final Dao dao;
    private final ProcessingService processingService;

    public WithdrawalCreator(Dao dao, ProcessingService processingService) {
        this.dao = dao;
        this.processingService = processingService;
    }


    public UUID createWithdrawal(UUID sourceUserID, Address withdrawalAddress, BigDecimal amount) {
        WithdrawalId withdrawalId = new WithdrawalId(UUID.randomUUID());
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(sourceUserID, withdrawalId, amount, withdrawalAddress, Status.PENDING);
        dao.addWithdrawalRequest(withdrawalRequest);
        processingService.putOperationForProcessing(withdrawalRequest);
        return withdrawalId.value();
    }
}
