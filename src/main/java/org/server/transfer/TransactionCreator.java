package org.server.transfer;

import org.server.model.Transaction;
import org.server.model.Status;
import org.server.dao.Dao;
import org.server.processor.ProcessingService;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCreator {

    private final Dao dao;
    private final ProcessingService processingService;

    public TransactionCreator(Dao dao, ProcessingService processingService) {
        this.dao = dao;
        this.processingService = processingService;
    }

    public UUID createTransaction(UUID sourceUserID, UUID destinationUserID, BigDecimal amount) {
        Transaction transaction = new Transaction(UUID.randomUUID(), sourceUserID, destinationUserID, amount, Status.PENDING);
        dao.addTransaction(transaction);
        processingService.putOperationForProcessing(transaction);
        return transaction.getTransactionID();
    }
}
